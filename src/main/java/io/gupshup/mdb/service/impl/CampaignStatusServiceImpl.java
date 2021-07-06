package io.gupshup.mdb.service.impl;

import io.gupshup.mdb.dto.campaign.CampaignStatus;
import io.gupshup.mdb.dto.campaign.MessageStatusRequest;
import io.gupshup.mdb.entities.CampaignEntity;
import io.gupshup.mdb.entities.MessageStatusEntity;
import io.gupshup.mdb.exceptions.InvalidRequestException;
import io.gupshup.mdb.repository.MessageStatusRepository;
import io.gupshup.mdb.service.CampaignStatusService;
import io.gupshup.mdb.utils.EntityUtils;
import io.gupshup.mdb.validator.MarketingDashboardValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Lazy
@Service("CampaignStatusService")
class CampaignStatusServiceImpl implements CampaignStatusService {

	private static final Logger logger = LoggerFactory.getLogger(CampaignStatusServiceImpl.class);

	@Autowired
	private MessageStatusRepository messageStatusRepository;

	@Autowired
	private MarketingDashboardValidator validator;

	@Autowired
	private EntityUtils entityUtils;

	@Override
	public void createOrUpdateCampaignMessage(MessageStatusRequest request) {
		logger.info("Received Campaign Message Status : {}", request);
		List<String> errors = validator.validateCampaignMessageRequest(request);
		if (!errors.isEmpty()) {
			logger.info("CampaignMessageRequest contains errors, throwing exception");
			throw new InvalidRequestException(errors);
		}
		String recipient = request.getPhone().replace("+", "").replace(" ", "");
		synchronized (this) {
			Optional<MessageStatusEntity> entity = messageStatusRepository
					.findByCampaignIdAndPhone(request.getCampaignId(), recipient);
			if (entity.isPresent()) {
				logger.info("MessageStatusEntity already present, updating status and timestamp");
				MessageStatusEntity messageEntity = entity.get();
				messageEntity.setStatus(request.getStatus().toUpperCase());
				messageEntity.setTimestamp(request.getTimestamp());
				messageStatusRepository.save(messageEntity);
			} else {
				logger.info("Creating new CampaignMessageEntity");
				MessageStatusEntity messageEntity = new MessageStatusEntity(request.getCampaignId(), recipient,
				                                                            request.getStatus().toUpperCase(),
				                                                            request.getTimestamp());
				messageStatusRepository.save(messageEntity);
			}
		}
	}

	@Override
	public CampaignStatus getCampaignStatus(String userId, String campaignId) {
		logger.info("Getting Campaign Status for campaign : {} for user : {}", campaignId, userId);
		CampaignEntity campaignEntity = entityUtils.fetchCampaignEntity(userId, campaignId);
		List<MessageStatusEntity> messageEntities = messageStatusRepository.findAllByCampaignId(campaignId);
		logger.info("Fetched Message Entities, Total Size : {}", messageEntities.size());
		int successCount = 0;
		int failedCount = 0;

		for (MessageStatusEntity entity : messageEntities) {
			if (entity.getStatus().equals(MessageStatus.FAILED.name())) {
				failedCount += 1;
			} else {
				successCount += 1;
			}
		}
		logger.info("Stats Calculated Success = {} failed = {}", successCount, failedCount);
		return CampaignStatus.builder().successCount(successCount).failedCount(failedCount)
		                     .totalListSize(campaignEntity.getPublishedListSize()).build();
	}
}
