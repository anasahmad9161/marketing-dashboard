package io.gupshup.mdb.service.impl;

import io.gupshup.mdb.entities.ContactEntity;
import io.gupshup.mdb.entities.ListEntity;
import io.gupshup.mdb.exceptions.CustomRuntimeException;
import io.gupshup.mdb.exceptions.InvalidRequestException;
import io.gupshup.mdb.exceptions.ResourceAlreadyExistsException;
import io.gupshup.mdb.repository.ContactRepository;
import io.gupshup.mdb.repository.ListsRepository;
import io.gupshup.mdb.service.ListsService;
import io.gupshup.mdb.utils.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static io.gupshup.mdb.constants.APIConstants.USERID;
import static io.gupshup.mdb.constants.ServiceConstants.ALL_CONTACTS;
import static io.gupshup.mdb.service.impl.ServiceCommonValidations.validateField;
import static java.util.Collections.singletonList;

@Service("ListsService")
class ListsServiceImpl implements ListsService {

	private static final Logger logger = LoggerFactory.getLogger(ListsServiceImpl.class);

	private static final String EMPTY_LIST = "Please provide list of Contact IDs";
	private static final String OPERATION_NOT_ALLOWED = "Operation not allowed for All Contacts List";
	private static final String EXCEPTION_INACTIVE_LIST_SUPPLIED = "Throwing Exception, Inactive List Supplied : {}";
	private static final String INACTIVE_LIST = "Inactive List : ";
	@Autowired
	private ListsRepository listsRepository;

	@Autowired
	private ContactRepository contactRepository;

	@Autowired
	private EntityUtils entityUtils;

	@Override
	public ListEntity saveList(String name, String userId) {
		logger.info("Request received for creating list with name : {} and user ID : {}", name, userId);
		validateField(name, "Name");
		validateField(userId, USERID);
		Optional<ListEntity> contactCampaignList = listsRepository.findByNameAndUserIdAndIsActive(name, userId, true);
		contactCampaignList.ifPresent(list -> {
			logger.info("Resource Already Exists : List with name : {}", name);
			throw new ResourceAlreadyExistsException("List", name);
		});
		ListEntity list = new ListEntity(name, userId);
		list.setContactEntities(Set.of());
		return listsRepository.save(list);
	}

	@Override
	public List<ListEntity> getAllLists(String userId) {
		validateField(userId, USERID);
		return listsRepository.findAllByUserIdAndIsActiveOrderByCreationDateDesc(userId, true);
	}

	@Override
	public ListEntity getList(String userId, String listId) {
		ListEntity entity = entityUtils.fetchListEntity(userId, listId);
		checkIfListIsActive(entity);
		return entity;
	}

	@Override
	public ListEntity updateName(String listId, String updatedName, String userId) {
		logger.info("Request received for updating list name");
		validateField(updatedName, "Name");
		ListEntity originalList = entityUtils.fetchListEntity(userId, listId);
		checkIfListIsActive(originalList);
		checkIfAllContacts(originalList.getName(), "Update ");
		Optional<ListEntity> contactCampaignList = listsRepository
				.findByNameAndUserIdAndIsActive(updatedName, userId, true);
		contactCampaignList.ifPresent(list -> {
			logger.info("Resource Already Exists : List with name : {}" , updatedName);
			throw new ResourceAlreadyExistsException("List", updatedName);
		});
		originalList.setName(updatedName);
		originalList.setLastUpdatedDate(LocalDateTime.now());
		return listsRepository.save(originalList);
	}

	@Override
	public ListEntity addContactsToList(String userId, String listId, List<String> contactIds) {
		logger.info("Request received for adding contacts in list : {}", listId);
		ListEntity originalList = entityUtils.fetchListEntity(userId, listId);
		checkIfListIsActive(originalList);
		checkIfAllContacts(originalList.getName(), "Add Contacts ");
		if (contactIds.isEmpty()) {
			logger.info("Empty list of contacts received, throwing exception");
			throw new InvalidRequestException(singletonList(EMPTY_LIST));
		}
		List<ContactEntity> listOfContactEntities = contactRepository.findAllById(contactIds);
		Set<ContactEntity> originalSetOfContactEntities = originalList.getContactEntities();
		originalSetOfContactEntities.addAll(listOfContactEntities);
		originalList.setContactEntities(originalSetOfContactEntities);
		listOfContactEntities.forEach(contact -> contact.getLists().add(originalList));
		originalList.setLastUpdatedDate(LocalDateTime.now());
		contactRepository.saveAll(listOfContactEntities);
		return listsRepository.save(originalList);
	}

	@Override
	public ListEntity removeContactsFromList(String userId, String listId, List<String> contactIds) {
		logger.info("Request received for removing contacts in list : {}", listId);
		ListEntity originalList = entityUtils.fetchListEntity(userId, listId);
		checkIfListIsActive(originalList);
		checkIfAllContacts(originalList.getName(), "Remove Contacts ");
		if (contactIds.isEmpty()) {
			logger.info("Empty list of contacts received, throwing exception");
			throw new InvalidRequestException(singletonList(EMPTY_LIST));
		}
		List<ContactEntity> listOfContactEntities = contactRepository.findAllById(contactIds);
		Set<ContactEntity> originalSetOfContactEntities = originalList.getContactEntities();
		listOfContactEntities.forEach(listOfContact -> {
			originalSetOfContactEntities.remove(listOfContact);
			listOfContact.getLists().remove(originalList);
		});
		originalList.setContactEntities(originalSetOfContactEntities);
		originalList.setLastUpdatedDate(LocalDateTime.now());
		contactRepository.saveAll(listOfContactEntities);
		return listsRepository.save(originalList);
	}

	@Override
	public void deleteList(String userId, String listId) {
		logger.info("Request received for deleting list : {}", listId);
		ListEntity originalList = entityUtils.fetchListEntity(userId, listId);
		checkIfAllContacts(originalList.getName(), "Delete ");
		List<String> contactIds = originalList.getContactEntities().stream().map(ContactEntity::getId)
		                                      .collect(Collectors.toList());
		if (contactIds.isEmpty()) {
			originalList.setActive(false);
			listsRepository.save(originalList);
		} else {
			ListEntity update = removeContactsFromList(userId, listId, contactIds);
			originalList.setActive(false);
			listsRepository.save(update);
		}
	}

	private void checkIfAllContacts(String name, String operation) {
		if (name.equals(ALL_CONTACTS)) {
			logger.info("Request received for {} for list name : {}", operation, name);
			throw new InvalidRequestException(Collections.singletonList(operation + OPERATION_NOT_ALLOWED));
		}
	}

	private void checkIfListIsActive(ListEntity entity) {
		if (!entity.isActive()) {
			logger.info(EXCEPTION_INACTIVE_LIST_SUPPLIED , entity.getName());
			throw new CustomRuntimeException(INACTIVE_LIST + entity.getName());
		}
	}
}
