package io.gupshup.mdb.mapper.impl;

import io.gupshup.mdb.dto.campaign.Campaign;
import io.gupshup.mdb.dto.campaign.Status;
import io.gupshup.mdb.entities.CampaignEntity;
import io.gupshup.mdb.entities.ChannelEntity;
import io.gupshup.mdb.entities.ListEntity;
import io.gupshup.mdb.entities.MessageEntity;
import io.gupshup.mdb.utils.EntityUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;

@RunWith(MockitoJUnitRunner.class)
public class CampaignEntityMapperTest {

	@Mock
	private EntityUtils entityUtils;

	@InjectMocks
	private CampaignEntityMapper campaignEntityMapper;

	@Before
	public void setup() {
		Mockito.when(entityUtils.fetchListEntity(any(), any())).thenReturn(new ListEntity("listId1", "testUser"));
		Mockito.when(entityUtils.fetchChannelEntity(any())).thenReturn(new ChannelEntity("SMS"));
		Mockito.when(entityUtils.fetchMessageEntity(any())).thenReturn(new MessageEntity("message"));
	}

	@Test
	public void shouldReturnDraftCampaignForDraftStatus() {
		LocalDateTime createdDate = LocalDateTime.now();
		CampaignEntity testEntity = testCreateCampaignEntity("DRAFT", createdDate);

		Campaign campaign = campaignEntityMapper.apply(testEntity);

		assert campaign != null;
		assert campaign.getCampaignId().equals("testId");
		assert campaign.getStatus().equals(Status.DRAFT);
		assert campaign.getChannelName().equals("SMS");
		assert campaign.getName().equals("testName");
		assert campaign.getListName().equals("listId1");
		assert campaign.getCreatedDate().equals(createdDate);
		assert campaign.getMessage().equals("message");

	}

	@Test
	public void shouldReturnPublishedCampaignWithStatusEntityForPublishedStatus() {
		LocalDateTime createdDate = LocalDateTime.now();
		LocalDateTime publishedDate = createdDate.plusHours(2);
		CampaignEntity testEntity = testCreateCampaignEntity("PUBLISHED", createdDate);
		testEntity.setPublishedDate(publishedDate);

		Campaign campaign = campaignEntityMapper.apply(testEntity);

		assert campaign != null;
		assert campaign.getCampaignId().equals("testId");
		assert campaign.getStatus().equals(Status.PUBLISHED);
		assert campaign.getChannelName().equals("SMS");
		assert campaign.getName().equals("testName");
		assert campaign.getListName().equals("listId1");
		assert campaign.getCreatedDate().equals(createdDate);
		assert campaign.getMessage().equals("message");

		assert campaign.getPublishedDate().equals(publishedDate);
	}

	@Test
	public void shouldReturnCompletedCampaignWithStatusEntityForCompletedStatus() {
		LocalDateTime createdDate = LocalDateTime.now();
		LocalDateTime publishedDate = createdDate.plusHours(2);
		LocalDateTime completedDate = publishedDate.plusHours(1);
		CampaignEntity testEntity = testCreateCampaignEntity("COMPLETED", createdDate);
		testEntity.setPublishedDate(publishedDate);
		testEntity.setCompletedDate(completedDate);

		Campaign campaign = campaignEntityMapper.apply(testEntity);

		assert campaign != null;
		assert campaign.getCampaignId().equals("testId");
		assert campaign.getStatus().equals(Status.COMPLETED);
		assert campaign.getChannelName().equals("SMS");
		assert campaign.getName().equals("testName");
		assert campaign.getListName().equals("listId1");
		assert campaign.getCreatedDate().equals(createdDate);
		assert campaign.getMessage().equals("message");
		assert campaign.getPublishedDate().equals(publishedDate);
		assert campaign.getCompletedDate().equals(completedDate);
	}


	private CampaignEntity testCreateCampaignEntity(String status, LocalDateTime createdDate) {
		CampaignEntity entity = new CampaignEntity("testUser", "listId1", "channelId1", "testName", "testMessage",
		                                           status, "sender");
		entity.setCreatedDate(createdDate);
		entity.setCampaignId("testId");
		return entity;
	}
}
