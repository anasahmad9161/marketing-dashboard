package io.gupshup.mdb.auth.impl;

import io.gupshup.mdb.auth.AuthTokenRepository;
import io.gupshup.mdb.auth.AuthenticationEntity;
import io.gupshup.mdb.auth.AuthenticationService;
import io.gupshup.mdb.exceptions.AuthenticationException;
import io.gupshup.mdb.service.CommonService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component("AuthenticationService")
class AuthenticationServiceImpl implements AuthenticationService {

	private static final String ACTIVE_USER = "User is already active. Please logout and try again.";
	private static final String QR_TOKEN_EXPIRED = "Token generated using this qrToken is expired. Please provide " +
			"new qrToken";
	private static final String INVALID_TOKEN = "Invalid Token.";
	private static final String NO_PHONES = "No Phone Numbers found for this API Key.";
	private static final String USER_NOT_FOUND = "User not found. Please check user ID";

	@Autowired
	private CommonService commonService;

	@Autowired
	private AuthTokenRepository repository;

	@Override
	public AuthenticationEntity register(String qrToken, String apiKey) {
		List<AuthenticationEntity> authenticationEntity = repository.findByQrToken(qrToken);
		if (!authenticationEntity.isEmpty()) {
			if (!authenticationEntity.get(0).isExpired()) throw new AuthenticationException(ACTIVE_USER);
			else throw new AuthenticationException(QR_TOKEN_EXPIRED);
		} else {
			List<String> phoneNumbers = commonService.getSenderPhoneNumbers(apiKey);
			if (phoneNumbers.isEmpty()) throw new AuthenticationException(NO_PHONES);
			AuthenticationEntity token = new AuthenticationEntity(qrToken, UUID.randomUUID().toString(), false,
			                                                      apiKey, phoneNumbers.get(0));
			commonService.createEssentialData(phoneNumbers.get(0));
			if (phoneNumbers.size() > 1) {
				token.setSecondaryPhoneNumber(phoneNumbers.get(1));
				commonService.createEssentialData(phoneNumbers.get(1));
			}
			return repository.save(token);
		}
	}

	@Override
	public boolean validateAuthToken(String phoneNumber, String authToken) {
		AuthenticationEntity token = getByAuthToken(authToken);
		List<String> validPhones = new ArrayList<>();
		CollectionUtils.addIgnoreNull(validPhones, token.getPrimaryPhoneNumber());
		CollectionUtils.addIgnoreNull(validPhones, token.getSecondaryPhoneNumber());
		boolean isPhoneNumberValid = validPhones.contains(phoneNumber);
		if (!isPhoneNumberValid) throw new AuthenticationException(USER_NOT_FOUND);
		return !token.isExpired();
	}

	@Override
	public void invalidateToken(String authToken) {
		AuthenticationEntity token = getByAuthToken(authToken);
		token.setExpired(true);
		token.setExpiredDate(LocalDateTime.now());
		repository.save(token);
	}

	@Override
	public AuthenticationEntity getAuthEntity(String authToken) {
		return getByAuthToken(authToken);
	}

	private AuthenticationEntity getByAuthToken(String authToken) {
		if(Strings.isBlank(authToken)) {
			throw new AuthenticationException(INVALID_TOKEN);
		}
		return repository.findByAuthToken(authToken)
		          .orElseThrow(() -> new AuthenticationException(INVALID_TOKEN));
	}
}
