package io.gupshup.mdb.service.impl;

import io.gupshup.mdb.entities.ContactEntity;
import io.gupshup.mdb.entities.ListEntity;
import io.gupshup.mdb.exceptions.InvalidRequestException;
import io.gupshup.mdb.exceptions.ResourceAlreadyExistsException;
import io.gupshup.mdb.repository.ContactRepository;
import io.gupshup.mdb.repository.ListsRepository;
import io.gupshup.mdb.utils.EntityUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ListsServiceImplTest {

	@Captor
	ArgumentCaptor<ListEntity> Captor;

	@Mock
	private ListsRepository listsRepository;
	@Mock
	private ContactRepository contactRepository;
	@Mock
	private EntityUtils entityUtils;

	@InjectMocks
	private ListsServiceImpl listsService;

	@Test
	public void saveListTestIfResourcePresent() {
		when(listsRepository.findByNameAndUserIdAndIsActive("name", "userid", true))
				.thenReturn(Optional.of(new ListEntity("name", "userid")));
		assertThrows(ResourceAlreadyExistsException.class, () -> listsService.saveList("name", "userid"));
	}

	@Test
	public void saveListTest() {
		ListEntity expected = new ListEntity("name", "user");
		expected.setListId("id");
		expected.setContactEntities(new HashSet<>());
		when(listsRepository.save(any())).thenReturn(expected);
		ListEntity entity = listsService.saveList("name", "user");
		assert entity.getContactEntities().isEmpty();
		assertEquals("id", entity.getListId());
		assertEquals("name", entity.getName());
		assertEquals("user", entity.getUserId());
	}

	@Test
	public void shouldThrowExceptionIfInvalidValueForUserId() {
		assertThrows(InvalidRequestException.class, () -> listsService.getAllLists(""));
	}

	@Test
	public void shouldReturnListOfListEntitiesForValidUserId() {
		when(listsRepository.findAllByUserIdAndIsActiveOrderByCreationDateDesc("user", true))
				.thenReturn(List.of(new ListEntity("name", "user")));
		List<ListEntity> listEntities = listsService.getAllLists("user");

		assert !listEntities.isEmpty();
		assert listEntities.stream().anyMatch(ent -> ent.getName().equals("name"));
	}

	@Test
	public void shouldReturnRecordIfExists() {
		ListEntity entity = new ListEntity("name", "userid");
		entity.setContactEntities(new HashSet<>());
		when(entityUtils.fetchListEntity(anyString(), anyString())).thenReturn(entity);
		ListEntity entityOut = listsService.getList("userid", "listId");

		assert entityOut != null;
		assert entityOut.getName().equals("name");
		assert entityOut.getUserId().equals("userid");
	}

	@Test
	public void shouldThrowExceptionIfUpdatedNameIsAlreadyPresent() {
		when(entityUtils.fetchListEntity(anyString(), anyString())).thenReturn(new ListEntity("oldName", "user"));
		when(listsRepository.findByNameAndUserIdAndIsActive("name", "userid", true))
				.thenReturn(Optional.of(new ListEntity("name", "user")));
		assertThrows(ResourceAlreadyExistsException.class, () -> listsService.updateName("listId", "name", "userid"));
	}

	@Test
	public void updateNameTest() {
		when(entityUtils.fetchListEntity(anyString(), anyString())).thenReturn(new ListEntity("oldName", "user"));
		when(listsRepository.save(any())).thenReturn(new ListEntity("newName", "userid"));

		ListEntity entity = listsService.updateName("listId", "oldName", "user");

		assert entity != null;
		assert entity.getName().equals("newName");
		assert entity.getUserId().equals("userid");
	}

	@Test
	public void shouldThrowExceptionIfEmptyListOfContacts() {
		List<String> list = new ArrayList<>();
		when(entityUtils.fetchListEntity(anyString(), anyString())).thenReturn(new ListEntity("oldName", "user"));
		assertThrows(InvalidRequestException.class, () -> listsService.addContactsToList("listId", "listId", list));
	}

	@Test
	public void addContactOfListTest() {
		List<String> listOfContacts = List.of("contactId");
		ContactEntity contactEntityToBeAdded = new ContactEntity("phoneNumber", "userid");
		ContactEntity contactEntityExisting = new ContactEntity("phoneNo", "user1");
		List<ContactEntity> newContactList = new ArrayList<>();
		newContactList.add(contactEntityToBeAdded);
		contactEntityToBeAdded.setLists(new HashSet<>());

		ListEntity listEntity = new ListEntity("oldName", "userid");
		Set<ContactEntity> contactEntities = new HashSet<>();
		contactEntities.add(contactEntityExisting);
		listEntity.setContactEntities(contactEntities);

		when(entityUtils.fetchListEntity(anyString(), anyString())).thenReturn(listEntity);
		when(contactRepository.findAllById(any())).thenReturn(newContactList);
		when(listsRepository.save(any())).thenReturn(listEntity);
		ListEntity entityOut = listsService.addContactsToList("userid", "listId", listOfContacts);
		assert entityOut != null;
		assert entityOut.getUserId().equals("userid");
		assert entityOut.isActive();
		verify(contactRepository, times(1)).findAllById(listOfContacts);
		verify(listsRepository, times(1)).save(any());
	}

	@Test
	public void shouldThrowExceptionIfRemoveListOfContacts() {
		List<String> list = new ArrayList<>();
		when(entityUtils.fetchListEntity(anyString(), anyString())).thenReturn(new ListEntity("oldName", "user"));
		assertThrows(InvalidRequestException.class,
		             () -> listsService.removeContactsFromList("listId", "listId", list));
	}

	@Test
	public void removeContactsFromListTest() {
		List<String> list = List.of("userid");
		ContactEntity contactEntity = new ContactEntity("phoneNumber", "userid");
		ContactEntity contactEntity1 = new ContactEntity("phoneNo", "user1");
		List<ContactEntity> list1 = new ArrayList<>();
		list1.add(contactEntity);
		list1.add(contactEntity1);

		ListEntity dummyEntity = new ListEntity("oldName", "userid");
		Set<ListEntity> set = new HashSet<>();

		dummyEntity.setLastUpdatedDate(LocalDateTime.now());
		Set<ContactEntity> contactEntities = new HashSet<>();
		contactEntities.add(contactEntity);
		contactEntities.add(contactEntity1);
		dummyEntity.setContactEntities(contactEntities);
		set.add(dummyEntity);
		contactEntity.setLists(set);
		contactEntity1.setLists(set);
		when(entityUtils.fetchListEntity(anyString(), anyString())).
				                                                           thenReturn(dummyEntity);
		when(contactRepository.findAllById(any())).thenReturn(list1);
		when(listsRepository.save(any())).thenReturn(new ListEntity("newName", "userid"));

		ListEntity entityOut = listsService.removeContactsFromList("userid", "listId", list);
		assert entityOut != null;
		assert entityOut.getUserId().equals("userid");
		verify(contactRepository, times(1)).findAllById(list);
		verify(listsRepository, times(1)).save(any());

	}

	@Test
	public void deleteListIfEmptyListTest() {
		ListEntity dummyEntity = new ListEntity("oldName", "userid");
		dummyEntity.setContactEntities(new HashSet<>());
		when(entityUtils.fetchListEntity(anyString(), anyString())).thenReturn(dummyEntity);
		listsService.deleteList("userid", "listId");
		verify(entityUtils, times(1)).fetchListEntity("userid", "listId");
		verify(listsRepository, times(1)).save(any());
	}

	@Test
	public void deleteListIfListHaveSomeRecords() {
		ContactEntity contactEntity = new ContactEntity("phone", "user");
		ContactEntity contactEntity1 = new ContactEntity("phone1", "user1");

		ListEntity dummyEntity = new ListEntity("oldName", "userid");
		Set<ContactEntity> contactEntities = new HashSet<>();
		contactEntities.add(contactEntity);
		contactEntities.add(contactEntity1);
		dummyEntity.setContactEntities(contactEntities);
		when(entityUtils.fetchListEntity(anyString(), anyString())).thenReturn(dummyEntity);
		listsService.deleteList("userid", "listId");
		verify(entityUtils, times(2)).fetchListEntity("userid", "listId");
		verify(listsRepository, times(2)).save(any());
	}

}
