package io.gupshup.mdb.mapper.impl;

import io.gupshup.mdb.dto.contact.Contact;
import io.gupshup.mdb.dto.list.ContactCampaignList;
import io.gupshup.mdb.entities.ContactEntity;
import io.gupshup.mdb.entities.ListEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.function.Function;

@RunWith(MockitoJUnitRunner.class)
public class ListEntityMapperTest {

	@Mock
	private Function<ContactEntity, Contact> contactEntityMapper;

	@InjectMocks
	private ListEntityMapper listEntityMapper;

	@Test
	public void shouldReturnCorrectOutputForValidInput() {
		LocalDateTime createdDate = LocalDateTime.now();
		ContactEntity contactEntity = new ContactEntity();
		ListEntity entity = new ListEntity("testList", "testUserId");
		entity.setListId("testId");
		entity.setCreationDate(createdDate);
		entity.setLastUpdatedDate(createdDate);
		entity.setContactEntities(Set.of(contactEntity));

		Mockito.when(contactEntityMapper.apply(contactEntity))
		       .thenReturn(Contact.builder().id("testContactId").build());

		ContactCampaignList list = listEntityMapper.apply(entity);

		assert list != null;
		assert list.getListId().equals("testId");
		assert list.getName().equals("testList");
		assert list.getCreationDate().equals(createdDate);
		assert list.getLastUpdatedDate().equals(createdDate);
		assert list.getContacts().size() == 1;
		assert list.getContacts().stream().anyMatch(cont -> cont.getId().equals("testContactId"));
	}
}
