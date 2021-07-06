package io.gupshup.mdb.mapper.impl;

import io.gupshup.mdb.dto.contact.Contact;
import io.gupshup.mdb.entities.ContactEntity;
import io.gupshup.mdb.entities.ListEntity;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component("ContactEntityMapper")
class ContactEntityMapper implements Function<ContactEntity, Contact> {

	@Override
	public Contact apply(ContactEntity entity) {
		Set<ListEntity> listEntitySet = entity.getLists();
		Set<String> listIds = listEntitySet.stream().map(ListEntity::getListId).collect(Collectors.toSet());
		return Contact.builder().id(entity.getId()).name(entity.getName()).nickname(entity.getNickname())
		              .phoneNumber(entity.getPhoneNumber()).salutation(entity.getSalutation()).listIds(listIds).build();
	}
}
