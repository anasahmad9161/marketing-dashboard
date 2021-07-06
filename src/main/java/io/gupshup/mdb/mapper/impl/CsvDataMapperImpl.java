package io.gupshup.mdb.mapper.impl;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import io.gupshup.mdb.dto.contact.ContactRecord;
import io.gupshup.mdb.entities.ContactStaging;
import io.gupshup.mdb.exceptions.FileReadException;
import io.gupshup.mdb.exceptions.InvalidRequestException;
import io.gupshup.mdb.mapper.CsvDataMapper;
import io.gupshup.mdb.service.impl.SupportedCountryCodes;
import io.gupshup.mdb.validator.MarketingDashboardValidator;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.gupshup.mdb.constants.ServiceConstants.COUNTRY_CODE;
import static io.gupshup.mdb.constants.ServiceConstants.FAILED;
import static io.gupshup.mdb.constants.ServiceConstants.FULL_NAME;
import static io.gupshup.mdb.constants.ServiceConstants.MOBILE_NUMBER;
import static io.gupshup.mdb.constants.ServiceConstants.NICKNAME;
import static io.gupshup.mdb.constants.ServiceConstants.SALUTATION;
import static io.gupshup.mdb.constants.ServiceConstants.SUCCESS;

@Component("CsvDataMapper")
class CsvDataMapperImpl implements CsvDataMapper {

	private static final String HEADERS_MISMATCH =
			"Mismatch in header names. Please refer documentation for supported format";
	private static final Logger logger = LoggerFactory.getLogger(CsvDataMapperImpl.class);
	private static final String DUPLICATE_RECORDS = "Duplicate Record";

	@Autowired
	private MarketingDashboardValidator validator;

	@Override
	public Iterable<CSVRecord> getCSVRecords(MultipartFile file) {
		logger.info("Attempting to read file and parse records at {}", LocalDateTime.now());
		List<String> errors = validator.checkForErrorsInFile(file);
		if (errors.isEmpty()) {
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(),
			                                                                      StandardCharsets.UTF_8))) {
				CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader()
				                                                          .withIgnoreHeaderCase().withTrim());
				if (!validator.checkHeaders(parser.getHeaderNames()))
					throw new InvalidRequestException(Collections.singletonList(HEADERS_MISMATCH));
				logger.info("Records parsed successfully for file : {}", file.getOriginalFilename());
				return parser.getRecords();
			} catch (IOException e) {
				throw new FileReadException(e);
			}
		} else {
			throw new InvalidRequestException(errors);
		}
	}

	@Override
	public Pair<Map<String, ContactStaging>, List<ContactRecord>> parseRecordsAndGetContactStaging(String userId,
	                                                                                               Iterable<CSVRecord> records) {
		Map<String, ContactStaging> contactStagingMap = new HashMap<>();
		List<ContactRecord> contactRecords = new ArrayList<>();
		records.forEach(csvRecord -> {
			List<String> errorIfAny = validator.checkForErrorInRecord(csvRecord);
			String name = csvRecord.get(FULL_NAME);
			String nickname = csvRecord.get(NICKNAME);
			if (errorIfAny.isEmpty()) {
				try {
					PhoneNumberUtil util = PhoneNumberUtil.getInstance();
					String countryCodeString = csvRecord.get(COUNTRY_CODE);
					countryCodeString = countryCodeString.replace("+", "");
					int countryCodeInt = Integer.parseInt(countryCodeString);
					String region = util.getRegionCodeForCountryCode(countryCodeInt);
					SupportedCountryCodes code = SupportedCountryCodes.valueOf(region);
					PhoneNumber phoneNumber = util.parse(csvRecord.get(MOBILE_NUMBER), code.name());
					Long phone = phoneNumber.getNationalNumber();
					String phoneSaved = "+" + countryCodeString + "-" + phone;
					if (contactStagingMap.containsKey(phoneSaved)) {
						contactRecords.add(ContactRecord.builder().name(name).nickname(nickname)
						                                .status(FAILED).phoneNumber(String.valueOf(phone))
						                                .rowNum((int) csvRecord.getRecordNumber())
						                                .countryCode(countryCodeString)
						                                .salutation(csvRecord.get(SALUTATION)).error(DUPLICATE_RECORDS)
						                                .build());
					} else {
						ContactStaging contactStaging = new ContactStaging(phoneSaved, userId);
						contactStaging.setDuplicate(false);
						contactStaging.setName(name);
						contactStaging.setNickname(nickname);
						contactStaging.setSalutation(csvRecord.get(SALUTATION));
						contactStagingMap.put(phoneSaved, contactStaging);
						contactRecords.add(ContactRecord.builder().name(name).nickname(nickname).status(SUCCESS)
						                                .phoneNumber(String.valueOf(phone))
						                                .rowNum((int) csvRecord.getRecordNumber())
						                                .countryCode(countryCodeString)
						                                .salutation(csvRecord.get(SALUTATION)).error(Strings.EMPTY)
						                                .build());
					}
				} catch (NumberParseException e) {
					throw new FileReadException(e);
				}
			} else {
				contactRecords.add(ContactRecord.builder().name(name).nickname(nickname).status(FAILED)
				                                .phoneNumber(csvRecord.get(MOBILE_NUMBER))
				                                .rowNum((int) csvRecord.getRecordNumber())
				                                .countryCode(csvRecord.get(COUNTRY_CODE))
				                                .salutation(csvRecord.get(SALUTATION))
				                                .error(String.join(", ", errorIfAny)).build());
			}
		});
		return Pair.of(contactStagingMap, contactRecords);
	}
}
