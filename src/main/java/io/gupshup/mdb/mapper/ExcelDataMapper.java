package io.gupshup.mdb.mapper;

import io.gupshup.mdb.dto.contact.ContactRecord;
import io.gupshup.mdb.entities.ContactStaging;
import org.springframework.data.util.Pair;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface ExcelDataMapper {

	Pair<Map<String, ContactStaging>, List<ContactRecord>> processExcel(String userid, MultipartFile file);

}
