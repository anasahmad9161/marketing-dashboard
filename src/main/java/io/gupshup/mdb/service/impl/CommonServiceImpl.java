package io.gupshup.mdb.service.impl;

import io.gupshup.mdb.entities.ChannelEntity;
import io.gupshup.mdb.entities.ListEntity;
import io.gupshup.mdb.exceptions.CustomRuntimeException;
import io.gupshup.mdb.repository.ChannelRepository;
import io.gupshup.mdb.repository.ListsRepository;
import io.gupshup.mdb.service.CommonService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static io.gupshup.mdb.constants.ServiceConstants.ALL_CONTACTS;

@Lazy
@Component("CommonService")
class CommonServiceImpl implements CommonService {

	private static final Logger logger = LoggerFactory.getLogger(CommonServiceImpl.class);

	@Value("${soip.server}")
	private String serverUrl;

	@Value("${soip.env}")
	private String env;

	@Autowired
	private ListsRepository listsRepository;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private ChannelRepository channelRepository;

	@Override
	public void createEssentialData(String userId) {
		logger.info("Creating Preliminary Data for user : {}", userId);
		ServiceCommonValidations.validateField(userId, "phoneNumber");
		synchronized (this) {
			Optional<ListEntity> listEntity = listsRepository.findByNameAndUserIdAndIsActive(ALL_CONTACTS, userId, true);
			if (listEntity.isEmpty()) {
				ListEntity listEntityNew = new ListEntity(ALL_CONTACTS, userId);
				listEntityNew.setContactEntities(Set.of());
				listsRepository.save(listEntityNew);
				logger.info("Created Preliminary Data for user : {}", userId);
			}
		}
	}

	@Override
	public List<ChannelEntity> getChannels() {
		logger.info("Returning Supported Channels");
		return channelRepository.findAll();
	}

	@Override
	public List<String> getSenderPhoneNumbers(String apiKey) {
		ServiceCommonValidations.validateField(apiKey, "apiKey");
		List<String> phoneNumbers = new ArrayList<>();
		String getMeUrl = serverUrl + env + "/v1/register/me/?apiKey=";
		logger.info("Calling SOIP API Sender Phone Numbers for user : {}", apiKey);
		try {
			ResponseEntity<String> response = restTemplate.getForEntity(getMeUrl + apiKey, String.class);
			logger.info("Response of SOIP API for Sender Phone Numbers : {}", response.getStatusCode());
			if (response.getStatusCode().is2xxSuccessful()) {
				logger.info("Successful Response for SOIP API for Sender Phone Numbers");
				JSONArray jsonArray = new JSONArray(response.getBody());
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject object = jsonArray.getJSONObject(i);
					phoneNumbers.add(object.getString("phoneno").replace(" ", ""));
				}
			} else if (response.getStatusCode().is4xxClientError()) {
				logger.info("Failure for SOIP API for Sender Phone Numbers, response body : {}", response.getBody());
				throw new CustomRuntimeException(response.getBody());
			} else {
				logger.info("Failure Response for SOIP API for Sender Phone Numbers, throwing exception");
				throw new CustomRuntimeException("Error fetching the details. Please try again.");
			}
		} catch (HttpClientErrorException e) {
			logger.info("Failure Response for SOIP API for Sender Phone Numbers, throwing exception");
			JSONObject obj = new JSONObject(e.getResponseBodyAsString());
			throw new CustomRuntimeException(obj.getString("message"));
		}
		return phoneNumbers;
	}
}
