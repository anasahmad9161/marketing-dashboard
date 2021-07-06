package io.gupshup.mdb.mapper.impl;

import io.gupshup.mdb.dto.contact.Contact;
import io.gupshup.mdb.dto.list.ContactCampaignList;
import io.gupshup.mdb.entities.ContactEntity;
import io.gupshup.mdb.entities.ListEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component("ListEntityMapper")
class ListEntityMapper implements Function<ListEntity, ContactCampaignList> {

	@Autowired
	@Qualifier("ContactEntityMapper")
	private Function<ContactEntity, Contact> contactEntityMapper;

	@Override
	public ContactCampaignList apply(ListEntity entity) {
		Set<ContactEntity> contactEntities = entity.getContactEntities();
		Set<Contact> contacts = contactEntities.stream()
		                                       .map(contactEntity -> contactEntityMapper.apply(contactEntity))
		                                       .collect(Collectors.toSet());
		return ContactCampaignList.builder().listId(entity.getListId()).name(entity.getName())
		                          .creationDate(entity.getCreationDate()).lastUpdatedDate(entity.getLastUpdatedDate())
		                          .contacts(contacts).build();
	}
}
