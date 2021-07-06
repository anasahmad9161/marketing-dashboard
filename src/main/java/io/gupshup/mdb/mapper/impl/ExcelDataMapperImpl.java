package io.gupshup.mdb.mapper.impl;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import io.gupshup.mdb.dto.contact.ContactRecord;
import io.gupshup.mdb.entities.ContactStaging;
import io.gupshup.mdb.exceptions.FileReadException;
import io.gupshup.mdb.exceptions.InvalidRequestException;
import io.gupshup.mdb.mapper.ExcelDataMapper;
import io.gupshup.mdb.service.impl.SupportedCountryCodes;
import io.gupshup.mdb.validator.MarketingDashboardValidator;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static io.gupshup.mdb.constants.ServiceConstants.COUNTRY_CODE;
import static io.gupshup.mdb.constants.ServiceConstants.FAILED;
import static io.gupshup.mdb.constants.ServiceConstants.FULL_NAME;
import static io.gupshup.mdb.constants.ServiceConstants.MOBILE_NUMBER;
import static io.gupshup.mdb.constants.ServiceConstants.NICKNAME;
import static io.gupshup.mdb.constants.ServiceConstants.SALUTATION;
import static io.gupshup.mdb.constants.ServiceConstants.SUCCESS;

@Lazy
@Component
class ExcelDataMapperImpl implements ExcelDataMapper {

	private static final Logger logger = LoggerFactory.getLogger(ExcelDataMapperImpl.class);
	private static final String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
	private static final String INVALID_FILE_FORMAT = "Invalid File Format. Please submit file in supported Excel " +
			"Format";
	private static final String HEADERS_MISMATCH =
			"Mismatch in header names. Please refer documentation for supported format";
	private static final String DUPLICATE_RECORDS = "Duplicate Record";


	private static final String[] HEADERS = {FULL_NAME, NICKNAME, SALUTATION, COUNTRY_CODE, MOBILE_NUMBER};

	@Autowired
	private MarketingDashboardValidator validator;

	@Override
	public Pair<Map<String, ContactStaging>, List<ContactRecord>> processExcel(String userid, MultipartFile file) {
		logger.info("Attempting to read file and parse records at {}", LocalDateTime.now());
		List<String> errors = validator.checkForErrorsInFile(file);
		if (!TYPE.equals(file.getContentType())) {
			errors.add(INVALID_FILE_FORMAT);
		}
		if (errors.isEmpty()) {
			try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
				Sheet sheet = workbook.getSheetAt(0);
				return parseRecordsAndGetContactStaging(userid, sheet);
			} catch (IOException e) {
				throw new FileReadException(e);
			}
		} else {
			throw new InvalidRequestException(errors);
		}
	}

	private Pair<Map<String, ContactStaging>, List<ContactRecord>> parseRecordsAndGetContactStaging(String userid,
	                                                                                                Sheet sheet) {
		DataFormatter dataFormatter = new DataFormatter();
		Map<String, ContactStaging> contactStagingMap = new HashMap<>();
		List<ContactRecord> contactRecords = new ArrayList<>();
		Iterator<Row> rowIterator = sheet.rowIterator();
		if (rowIterator.hasNext()) {
			Row headerRow = rowIterator.next();
			for (Cell cell : headerRow) {
				String cellValue = dataFormatter.formatCellValue(cell);
				if (!cellValue.equals(HEADERS[cell.getColumnIndex()])) {
					throw new InvalidRequestException(Collections.singletonList(HEADERS_MISMATCH));
				}
			}
		}
		int totalRecords = 0;
		while (rowIterator.hasNext()) {
			totalRecords += 1;
			Row row = rowIterator.next();
			String[] values = {"", "", "", "", ""};
			for (Cell cell : row) {
				String cellValue = dataFormatter.formatCellValue(cell);
				values[cell.getColumnIndex()] = cellValue;
			}
			List<String> errors = validator.validateExcelRecord(values);
			if (errors.isEmpty()) {
				try {
					PhoneNumberUtil util = PhoneNumberUtil.getInstance();
					String countryCodeString = values[3];
					countryCodeString = countryCodeString.replace("+", "");
					int countryCodeInt = Integer.parseInt(countryCodeString);
					String region = util.getRegionCodeForCountryCode(countryCodeInt);
					SupportedCountryCodes code = SupportedCountryCodes.valueOf(region);
					Phonenumber.PhoneNumber phoneNumber = util.parse(values[4], code.name());
					Long phone = phoneNumber.getNationalNumber();
					String phoneSaved = "+" + countryCodeString + "-" + phone;
					if (contactStagingMap.containsKey(phoneSaved)) {
						contactRecords.add(ContactRecord.builder().name(values[0]).nickname(values[1])
						                                .status(FAILED).phoneNumber(String.valueOf(phone))
						                                .rowNum(totalRecords).countryCode(countryCodeString)
						                                .salutation(values[2]).error(DUPLICATE_RECORDS).build());
					} else {
						ContactStaging contactStaging = new ContactStaging(phoneSaved, userid);
						contactStaging.setDuplicate(false);
						contactStaging.setName(values[0]);
						contactStaging.setNickname(values[1]);
						contactStaging.setSalutation(values[2]);
						contactStagingMap.put(phoneSaved, contactStaging);
						contactRecords.add(ContactRecord.builder().name(values[0]).nickname(values[1])
						                                .status(SUCCESS).phoneNumber(String.valueOf(phone))
						                                .rowNum(totalRecords).countryCode(countryCodeString)
						                                .salutation(values[2]).error(Strings.EMPTY).build());
					}
				} catch (NumberParseException e) {
					throw new FileReadException(e);
				}
			} else {
				contactRecords.add(ContactRecord.builder().name(values[0]).nickname(values[1]).status(FAILED)
				                                .phoneNumber(values[4]).rowNum(totalRecords)
				                                .countryCode(values[3]).salutation(values[2])
				                                .error(String.join(", ", errors)).build());
			}
		}
		logger.info("Total Records to be processed : {}", totalRecords);
		return Pair.of(contactStagingMap, contactRecords);
	}

}
