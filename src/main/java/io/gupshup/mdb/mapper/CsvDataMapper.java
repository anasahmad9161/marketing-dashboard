package io.gupshup.mdb.mapper;

import io.gupshup.mdb.dto.contact.ContactRecord;
import io.gupshup.mdb.entities.ContactStaging;
import org.apache.commons.csv.CSVRecord;
import org.springframework.data.util.Pair;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface CsvDataMapper {

	Iterable<CSVRecord> getCSVRecords(MultipartFile file);

	Pair<Map<String, ContactStaging>, List<ContactRecord>> parseRecordsAndGetContactStaging(String userId, Iterable<CSVRecord> records);
}
