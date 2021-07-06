package io.gupshup.mdb.mapper.impl;

import io.gupshup.mdb.dto.campaign.Campaign;
import io.gupshup.mdb.dto.campaign.Status;
import io.gupshup.mdb.entities.CampaignEntity;
import io.gupshup.mdb.entities.ChannelEntity;
import io.gupshup.mdb.entities.ListEntity;
import io.gupshup.mdb.entities.MessageEntity;
import io.gupshup.mdb.exceptions.CustomRuntimeException;
import io.gupshup.mdb.utils.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component("CampaignEntityMapper")
class CampaignEntityMapper implements Function<CampaignEntity, Campaign> {

	@Autowired
	private EntityUtils entityUtils;

	@Override
	public Campaign apply(CampaignEntity entity) {
		Status status = Status.valueOf(entity.getStatus().toUpperCase());
		ListEntity list = entityUtils.fetchListEntity(entity.getUserId(), entity.getListId());
		ChannelEntity channelEntity = entityUtils.fetchChannelEntity(entity.getChannelId());
		MessageEntity messageEntity = entityUtils.fetchMessageEntity(entity.getMessageId());
		switch (status) {
			case DRAFT:
				return Campaign.builder().campaignId(entity.getCampaignId()).name(entity.getName())
				               .listName(list.getName()).channelName(channelEntity.getChannelName())
				               .createdDate(entity.getCreatedDate()).message(messageEntity.getMessage())
				               .status(Status.DRAFT).sender(entity.getSender())
				               .lastUpdatedDate(entity.getLastUpdatedDate()).build();
			case PUBLISHED:
				return Campaign.builder().campaignId(entity.getCampaignId()).name(entity.getName())
				               .listName(list.getName()).channelName(channelEntity.getChannelName())
				               .createdDate(entity.getCreatedDate()).message(messageEntity.getMessage())
				               .status(Status.PUBLISHED).sender(entity.getSender())
				               .publishedDate(entity.getPublishedDate()).lastUpdatedDate(entity.getLastUpdatedDate())
				               .build();
			case COMPLETED:
				return Campaign.builder().campaignId(entity.getCampaignId()).name(entity.getName())
				               .listName(list.getName()).channelName(channelEntity.getChannelName())
				               .createdDate(entity.getCreatedDate()).message(messageEntity.getMessage())
				               .status(Status.COMPLETED).sender(entity.getSender())
				               .publishedDate(entity.getPublishedDate()).completedDate(entity.getCompletedDate())
				               .lastUpdatedDate(entity.getLastUpdatedDate()).build();
			default:
				throw new CustomRuntimeException("Invalid Status Value. Please contact your gupshup representative.");
		}
	}
}
