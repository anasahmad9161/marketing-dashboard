package io.gupshup.mdb.mapper.impl;

import io.gupshup.mdb.dto.contact.Contact;
import io.gupshup.mdb.entities.ContactEntity;
import io.gupshup.mdb.entities.ListEntity;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

public class ContactEntityMapperTest {

	private ContactEntityMapper mapper;
	private ContactEntity entity;

	@Before
	public void setup() {
		mapper = new ContactEntityMapper();
		ListEntity listEntity = new ListEntity();
		listEntity.setListId("testListId");
		entity = new ContactEntity("66-9900990099", "testUserId");
		entity.setName("testName");
		entity.setNickname("testNickName");
		entity.setSalutation("Mr");
		entity.setLists(Set.of(listEntity));
		entity.setId("testId");
	}

	@Test
	public void shouldReturnCorrectOutputForValidInput() {
		Contact contact = mapper.apply(entity);

		assert contact != null;
		assert contact.getId().equals("testId");
		assert contact.getName().equals("testName");
		assert contact.getNickname().equals("testNickName");
		assert contact.getSalutation().equals("Mr");
		assert contact.getPhoneNumber().compareTo("66-9900990099")==0;
		assert contact.getListIds().size()==1;
		assert contact.getListIds().stream().anyMatch(id -> id.equals("testListId"));
	}
}
