package io.gupshup.mdb.validator;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import io.gupshup.mdb.dto.campaign.MessageStatusRequest;
import io.gupshup.mdb.dto.campaign.ReplySaveRequest;
import io.gupshup.mdb.dto.campaign.SaveCampaignRequest;
import io.gupshup.mdb.repository.CampaignRepository;
import io.gupshup.mdb.repository.ChannelRepository;
import io.gupshup.mdb.repository.ListsRepository;
import io.gupshup.mdb.service.impl.MessageStatus;
import io.gupshup.mdb.service.impl.SupportedCountryCodes;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.util.Strings;
import org.apache.logging.log4j.util.TriConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static io.gupshup.mdb.constants.APIConstants.CAMPAIGNID;
import static io.gupshup.mdb.constants.ServiceConstants.COUNTRY_CODE;
import static io.gupshup.mdb.constants.ServiceConstants.FULL_NAME;
import static io.gupshup.mdb.constants.ServiceConstants.MOBILE_NUMBER;
import static io.gupshup.mdb.constants.ServiceConstants.NICKNAME;
import static io.gupshup.mdb.constants.ServiceConstants.SALUTATION;

@Component("MarketingDashboardValidator")
class MarketingDashboardValidatorImpl implements MarketingDashboardValidator {

	private static final String EMPTY_FILE = "Empty File : ";
	private static final String EMPTY_FIELD = "Empty Field : ";
	private static final List<String> HEADERS = List.of(FULL_NAME, NICKNAME, SALUTATION, MOBILE_NUMBER, COUNTRY_CODE);
	private static final String MANDATORY_FIELD_MISSING = "Please provide either Full Name or Nickname";
	private static final String INVALID_NUMBER = "Invalid Mobile Number";
	private static final String INVALID_COUNTRY_CODE = "Invalid Country Code";
	private static final String INVALID_LIST_ID = "List Does not exists. Please check List ID";
	private static final String INVALID_CHANNEL_ID = "Channel Does not exists. Please check Channel Names";
	private static final String INVALID_CAMPAIGN_ID = "Campaign Does not exists. Please check Campaign ID";
	private static final String INVALID_VALUE = "Invalid Value ";

	private final TriConsumer<String, String, List<String>> stringSanityChecker = (field, fieldName, errors) -> {
		if (Strings.isBlank(field)) errors.add(EMPTY_FIELD + fieldName);
	};

	@Autowired
	private ListsRepository listsRepository;
	@Autowired
	private ChannelRepository channelRepository;
	@Autowired
	private CampaignRepository campaignRepository;

	@Override
	public List<String> checkForErrorsInFile(MultipartFile file) {
		List<String> errors = new ArrayList<>();
		if (file.isEmpty()) errors.add(EMPTY_FILE + file.getOriginalFilename());
		return errors;
	}

	@Override
	public Boolean checkHeaders(List<String> headerNames) {
		return headerNames.containsAll(HEADERS);
	}

	@Override
	public List<String> checkForErrorInRecord(CSVRecord csvRecord) {
		List<String> errors = new ArrayList<>();
		if (Strings.isBlank(csvRecord.get(FULL_NAME)) && Strings.isBlank(csvRecord.get(NICKNAME))) {
			errors.add(MANDATORY_FIELD_MISSING);
		}
		String countryCodeString = csvRecord.get(COUNTRY_CODE);
		String mobileNumberString = csvRecord.get(MOBILE_NUMBER);
		errors.addAll(checkPhoneAndCountryCode(countryCodeString, mobileNumberString));
		return errors;
	}

	@Override
	public List<String> checkSaveCampaignRequest(SaveCampaignRequest request) {
		List<String> errors = new ArrayList<>();
		stringSanityChecker.accept(request.getName(), "Name", errors);
		stringSanityChecker.accept(request.getMessage(), "Message", errors);
		stringSanityChecker.accept(request.getListId(), "ListId", errors);
		stringSanityChecker.accept(request.getChannelId(), "ChannelId", errors);
		stringSanityChecker.accept(request.getSender(), "Sender", errors);
		if (!errors.isEmpty()) return errors;
		boolean listExists = listsRepository.existsById(request.getListId());
		if (!listExists) errors.add(INVALID_LIST_ID);
		boolean channelExists = channelRepository.existsById(request.getChannelId());
		if (!channelExists) errors.add(INVALID_CHANNEL_ID);
		return errors;
	}

	@Override
	public List<String> validateCampaignMessageRequest(MessageStatusRequest request) {
		List<String> errors = new ArrayList<>();
		stringSanityChecker.accept(request.getCampaignId(), CAMPAIGNID, errors);
		stringSanityChecker.accept(request.getTimestamp(), "timestamp", errors);
		stringSanityChecker.accept(request.getStatus(), "status", errors);
		stringSanityChecker.accept(request.getPhone(), "phone", errors);
		if (!errors.isEmpty()) return errors;
		if (!campaignRepository.existsById(request.getCampaignId())) {
			errors.add(INVALID_CAMPAIGN_ID);
		}
		try {
			MessageStatus.valueOf(request.getStatus().toUpperCase());
		} catch (IllegalArgumentException e) {
			errors.add(INVALID_VALUE + e.getLocalizedMessage());
		}
		return errors;
	}

	@Override
	public List<String> validateReplyRequest(ReplySaveRequest request) {
		List<String> errors = new ArrayList<>();
		stringSanityChecker.accept(request.getCampaignId(), CAMPAIGNID, errors);
		stringSanityChecker.accept(request.getTimestamp(), "timestamp", errors);
		stringSanityChecker.accept(request.getMessage(), "message", errors);
		stringSanityChecker.accept(request.getPhone(), "phone", errors);
		if (!errors.isEmpty()) return errors;
		if (!campaignRepository.existsById(request.getCampaignId())) {
			errors.add(INVALID_CAMPAIGN_ID);
		}
		return errors;
	}

	@Override
	public List<String> validateExcelRecord(String[]  values) {
		List<String> errors = new ArrayList<>();
		if (Strings.isBlank(values[0]) && Strings.isBlank(values[1])) {
			errors.add(MANDATORY_FIELD_MISSING);
		}
		errors.addAll(checkPhoneAndCountryCode(values[3], values[4]));
		return errors;
	}

	private List<String> checkPhoneAndCountryCode(String countryCodeString, String mobileNumberString) {
		List<String> errors = new ArrayList<>();
		stringSanityChecker.accept(countryCodeString, COUNTRY_CODE, errors);
		stringSanityChecker.accept(mobileNumberString, MOBILE_NUMBER, errors);
		if (mobileNumberString.matches(".*[a-z].*")) {
			errors.add(INVALID_NUMBER);
		}
		PhoneNumberUtil util = PhoneNumberUtil.getInstance();
		try {
			countryCodeString = countryCodeString.replace("+", "");
			int countryCodeInt = Integer.parseInt(countryCodeString);
			String region = util.getRegionCodeForCountryCode(countryCodeInt);
			SupportedCountryCodes code = SupportedCountryCodes.valueOf(region);
			PhoneNumber phoneNumber = util.parse(mobileNumberString, code.name());
			if (!util.isValidNumberForRegion(phoneNumber, code.name())) {
				errors.add(INVALID_NUMBER);
			}
		} catch (IllegalArgumentException e) {
			errors.add(INVALID_COUNTRY_CODE);
		} catch (NumberParseException e) {
			errors.add(INVALID_NUMBER);
		}
		return errors;
	}
}
