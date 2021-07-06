package io.gupshup.mdb.utils;

import io.gupshup.mdb.entities.CampaignEntity;
import io.gupshup.mdb.entities.ChannelEntity;
import io.gupshup.mdb.entities.ContactEntity;
import io.gupshup.mdb.entities.ListEntity;
import io.gupshup.mdb.entities.MessageEntity;
import io.gupshup.mdb.exceptions.ResourceNotFountException;
import io.gupshup.mdb.repository.CampaignRepository;
import io.gupshup.mdb.repository.ChannelRepository;
import io.gupshup.mdb.repository.ContactRepository;
import io.gupshup.mdb.repository.ListsRepository;
import io.gupshup.mdb.repository.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static io.gupshup.mdb.constants.APIConstants.CAMPAIGNID;
import static io.gupshup.mdb.constants.APIConstants.CHANNELID;
import static io.gupshup.mdb.constants.APIConstants.CONTACTID;
import static io.gupshup.mdb.constants.APIConstants.LISTID;
import static io.gupshup.mdb.constants.APIConstants.MESSAGEID;
import static io.gupshup.mdb.constants.APIConstants.USERID;
import static io.gupshup.mdb.constants.ServiceConstants.ALL_CONTACTS;
import static io.gupshup.mdb.service.impl.ServiceCommonValidations.validateField;

@Lazy
@Component("EntityUtils")
public class EntityUtils {

	private static final Logger logger = LoggerFactory.getLogger(EntityUtils.class);

	@Autowired
	private CampaignRepository campaignRepository;

	@Autowired
	private ListsRepository listsRepository;

	@Autowired
	private ContactRepository contactRepository;

	@Autowired
	private MessageRepository messageRepository;

	@Autowired
	private ChannelRepository channelRepository;

	public CampaignEntity fetchCampaignEntity(String userId, String campaignId) {
		validateField(userId, USERID);
		validateField(campaignId, CAMPAIGNID);
		CampaignEntity entity = campaignRepository.findByCampaignIdAndUserId(campaignId, userId)
		                                          .orElseThrow(() -> new ResourceNotFountException("Campaign",
		                                                                                           campaignId));
		logger.info("Campaign Found : {} for user Id : {}", campaignId, userId);
		return entity;
	}

	public ListEntity fetchListEntity(String userId, String listId) {
		validateField(userId, USERID);
		validateField(listId, LISTID);
		ListEntity entity = listsRepository.findByListIdAndUserId(listId, userId)
		                                   .orElseThrow(() -> new ResourceNotFountException("List", listId));
		if (entity.getName().equals(ALL_CONTACTS)) {
			entity.getContactEntities().addAll(contactRepository.findAllByUserId(entity.getUserId()));
		}
		logger.info("List Entity Found : {} for user ID : {}", listId, userId);
		return entity;
	}

	public ContactEntity fetchContactEntity(String userId, String contactId) {
		validateField(userId, USERID);
		validateField(contactId, CONTACTID);
		logger.info("Fetching Contact : {} for user : {}", contactId, userId);
		ContactEntity entity = contactRepository.findByIdAndUserId(contactId, userId)
		                                        .orElseThrow(() -> new ResourceNotFountException("Contact",
		                                                                                         contactId));
		logger.info("Contact Entity Found : {} for user ID : {}", contactId, userId);
		return entity;
	}

	public ChannelEntity fetchChannelEntity(String channelId) {
		validateField(channelId, CHANNELID);
		logger.info("Fetching Channel : {}", channelId);
		ChannelEntity channelEntity = channelRepository.findById(channelId)
		                                               .orElseThrow(() -> new ResourceNotFountException("Channel",
		                                                                                                channelId));
		logger.info("Channel Entity found : {}", channelId);
		return channelEntity;
	}

	public MessageEntity fetchMessageEntity(String messageId) {
		validateField(messageId, MESSAGEID);
		logger.info("Fetching Message : {}", messageId);
		MessageEntity messageEntity = messageRepository.findById(messageId)
		                                               .orElseThrow(() -> new ResourceNotFountException("Message",
		                                                                                                messageId));
		logger.info("Message Entity found : {}", messageId);
		return messageEntity;
	}

	public void createChannels(List<String> channels) {
		logger.info("Creating Supported Channels");
		channels.forEach(channel -> {
			Optional<ChannelEntity> channelEntity = channelRepository.findByChannelName(channel);
			if (channelEntity.isPresent()) {
				logger.info("Channel already exits : {}", channel);
			} else {
				channelRepository.save(new ChannelEntity(channel));
			}
		});
		logger.info("Channels created successfully");
	}

}
