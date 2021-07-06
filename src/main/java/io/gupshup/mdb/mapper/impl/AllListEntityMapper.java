package io.gupshup.mdb.mapper.impl;

import io.gupshup.mdb.constants.ServiceConstants;
import io.gupshup.mdb.dto.list.ContactCampaignAllList;
import io.gupshup.mdb.entities.ListEntity;
import io.gupshup.mdb.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component("AllListEntityMapper")
class AllListEntityMapper implements Function<ListEntity, ContactCampaignAllList> {

	@Autowired
	private ContactRepository contactRepository;

	@Override
	public ContactCampaignAllList apply(ListEntity entity) {
		int listSize;
		if (entity.getName().equals(ServiceConstants.ALL_CONTACTS)) {
			listSize = contactRepository.findAllByUserId(entity.getUserId()).size();
		} else {
			listSize = entity.getContactEntities().size();
		}
		return ContactCampaignAllList.builder().listId(entity.getListId()).name(entity.getName()).size(listSize)
		                             .creationDate(entity.getCreationDate())
		                             .lastUpdatedDate(entity.getLastUpdatedDate()).build();
	}
}
