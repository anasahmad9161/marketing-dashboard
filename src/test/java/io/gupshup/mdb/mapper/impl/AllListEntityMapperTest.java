package io.gupshup.mdb.mapper.impl;

import io.gupshup.mdb.dto.list.ContactCampaignAllList;
import io.gupshup.mdb.entities.ListEntity;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Set;

public class AllListEntityMapperTest {

	private AllListEntityMapper allListEntityMapper;

	@Before
	public void setup() {
		allListEntityMapper = new AllListEntityMapper();
	}

	@Test
	public void shouldReturnCorrectOutputGivenValidInput() {
		LocalDateTime createdDate = LocalDateTime.now();
		LocalDateTime lastUpdatedDate = createdDate.plusDays(2);
		ListEntity listEntity = new ListEntity("list 1", "user 1");
		listEntity.setListId("id1");
		listEntity.setContactEntities(Set.of());
		listEntity.setCreationDate(createdDate);
		listEntity.setLastUpdatedDate(lastUpdatedDate);

		ContactCampaignAllList contactCampaignAllList = allListEntityMapper.apply(listEntity);

		assert contactCampaignAllList != null;
		assert contactCampaignAllList.getListId().equals("id1");
		assert contactCampaignAllList.getCreationDate().equals(createdDate);
		assert contactCampaignAllList.getName().equals("list 1");
		assert contactCampaignAllList.getSize() == 0;
		assert contactCampaignAllList.getLastUpdatedDate().equals(lastUpdatedDate);
	}
}
