package io.gupshup.mdb.service.impl;

import io.gupshup.mdb.dto.contact.ContactRecord;
import io.gupshup.mdb.dto.contact.UploadContactsResponse;
import io.gupshup.mdb.entities.ContactEntity;
import io.gupshup.mdb.entities.ContactStaging;
import io.gupshup.mdb.entities.ListEntity;
import io.gupshup.mdb.exceptions.InvalidRequestException;
import io.gupshup.mdb.mapper.CsvDataMapper;
import io.gupshup.mdb.mapper.ExcelDataMapper;
import io.gupshup.mdb.repository.ContactRepository;
import io.gupshup.mdb.repository.ContactStagingRepository;
import io.gupshup.mdb.repository.ListsRepository;
import io.gupshup.mdb.service.ContactsService;
import io.gupshup.mdb.utils.EntityUtils;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static io.gupshup.mdb.constants.APIConstants.USERID;
import static io.gupshup.mdb.constants.ServiceConstants.ALL_CONTACTS;
import static io.gupshup.mdb.service.impl.ServiceCommonValidations.validateField;

@Service("ContactsService")
class ContactsServiceImpl implements ContactsService {

	private static final Logger logger = LoggerFactory.getLogger(ContactsServiceImpl.class);

	@Autowired
	private CsvDataMapper csvDataMapper;

	@Autowired
	private ExcelDataMapper excelDataMapper;

	@Autowired
	private ContactRepository contactRepository;

	@Autowired
	private ContactStagingRepository contactStagingRepository;

	@Autowired
	private ListsRepository listsRepository;

	@Autowired
	private EntityUtils entityUtils;

	@Override
	public UploadContactsResponse uploadContacts(String userId, MultipartFile file) {
		logger.info("Execution for Upload Contacts is started at {}", LocalDateTime.now());
		validateField(userId, USERID);
		String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
		Pair<Map<String, ContactStaging>, List<ContactRecord>> parseOutcome = null;
		int totalRecords;
		if (extension != null && (extension.equals("xlsx") || extension.equals("xls"))) {
			parseOutcome = excelDataMapper.processExcel(userId, file);
			totalRecords = parseOutcome.getSecond().size();
		} else if (extension != null && extension.equals("csv")) {
			Iterable<CSVRecord> records = csvDataMapper.getCSVRecords(file);
			totalRecords = IterableUtils.size(records);
			logger.info("Total Records to be processed : {} for file : {}", totalRecords, file.getOriginalFilename());
			parseOutcome = csvDataMapper.parseRecordsAndGetContactStaging(userId, records);
		} else {
			throw new InvalidRequestException(Collections.singletonList("Invalid File Format"));
		}
		Collection<ContactStaging> contactStagingList = parseOutcome.getFirst().values();
		contactStagingRepository.saveAll(contactStagingList);
		contactStagingRepository.updateContactStaging();
		List<ContactStaging> duplicateContactStagingList = contactStagingRepository.findAllByDuplicate(true);
		duplicateContactStagingList.forEach(item -> {
			ContactEntity contactEntity = contactRepository
					.findByUserIdAndPhoneNumber(item.getUserId(), item.getPhoneNumber());
			contactEntity.setName(item.getName());
			contactEntity.setNickname(item.getNickname());
			contactEntity.setSalutation(item.getSalutation());
			contactRepository.save(contactEntity);
		});
		contactRepository.saveAllNonDuplicates();
		contactStagingRepository.deleteAll();
		List<ContactEntity> contactEntities = contactRepository
				.findByUserIdAndPhoneNumberIn(userId, parseOutcome.getFirst().keySet());
		List<String> contactIds = contactEntities.stream().map(ContactEntity::getId).collect(Collectors.toList());
		UploadContactsResponse response = new UploadContactsResponse(contactIds, totalRecords - contactIds
				.size(), duplicateContactStagingList.size(), parseOutcome.getSecond());
		logger.info("Updating All Contacts List");
		Optional<ListEntity> allContactsList = listsRepository
				.findByNameAndUserIdAndIsActive(ALL_CONTACTS, userId, true);
		if (allContactsList.isPresent()) {
			allContactsList.get().setLastUpdatedDate(LocalDateTime.now());
			listsRepository.save(allContactsList.get());
		}
		logger.info("Execution Complete. Total Records : {} Success Count : {}", totalRecords, contactIds.size());
		return response;
	}

	@Override
	public List<ContactEntity> getAllContacts(String userId) {
		validateField(userId, USERID);
		logger.info("Fetching All Contacts for user : {}", userId);
		return contactRepository.findAllByUserId(userId);
	}

	@Override
	public ContactEntity getContact(String userId, String contactId) {
		return entityUtils.fetchContactEntity(userId, contactId);
	}

	@Override
	public void deleteContacts(String userId, List<String> contactIds) {
		logger.info("Deleting {} contact entities", contactIds.size());
		List<ContactEntity> entities = new ArrayList<>();
		contactIds.forEach(id -> {
			ContactEntity entity = entityUtils.fetchContactEntity(userId, id);
			ContactEntity updated = removeAllLists(entity);
			entities.add(updated);
		});
		contactRepository.deleteAll(entities);
		logger.info("Deletion Complete");
	}

	private ContactEntity removeAllLists(ContactEntity contactEntity) {
		Set<ListEntity> originalSet = contactEntity.getLists();
		originalSet.forEach(listEntity -> listEntity.getContactEntities().remove(contactEntity));
		contactEntity.setLists(Set.of());
		listsRepository.saveAll(originalSet);
		return contactRepository.save(contactEntity);
	}
}
