package io.gupshup.mdb.service.impl;

import io.gupshup.mdb.controller.websockets.entities.WhatsappQRConnection;
import io.gupshup.mdb.controller.websockets.repository.WhatsappQRConnectionRepository;
import io.gupshup.mdb.dto.campaign.Campaign;
import io.gupshup.mdb.dto.campaign.CampaignReport;
import io.gupshup.mdb.dto.campaign.ReplySaveRequest;
import io.gupshup.mdb.dto.campaign.SaveCampaignRequest;
import io.gupshup.mdb.dto.campaign.Status;
import io.gupshup.mdb.entities.CampaignEntity;
import io.gupshup.mdb.entities.CampaignReply;
import io.gupshup.mdb.entities.ChannelEntity;
import io.gupshup.mdb.entities.ContactEntity;
import io.gupshup.mdb.entities.ListEntity;
import io.gupshup.mdb.entities.MessageEntity;
import io.gupshup.mdb.entities.MessageStatusEntity;
import io.gupshup.mdb.exceptions.CustomRuntimeException;
import io.gupshup.mdb.exceptions.InvalidRequestException;
import io.gupshup.mdb.exceptions.ResourceAlreadyExistsException;
import io.gupshup.mdb.repository.CampaignReplyRepository;
import io.gupshup.mdb.repository.CampaignRepository;
import io.gupshup.mdb.repository.MessageRepository;
import io.gupshup.mdb.repository.MessageStatusRepository;
import io.gupshup.mdb.service.CampaignService;
import io.gupshup.mdb.service.CampaignStatusService;
import io.gupshup.mdb.utils.EntityUtils;
import io.gupshup.mdb.validator.MarketingDashboardValidator;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import static io.gupshup.mdb.constants.APIConstants.USERID;
import static io.gupshup.mdb.constants.ServiceConstants.MESSAGE;
import static io.gupshup.mdb.constants.ServiceConstants.SMS;
import static io.gupshup.mdb.constants.ServiceConstants.WHATSAPP;
import static io.gupshup.mdb.service.impl.ServiceCommonValidations.validateField;

@Service("CampaignService")
class CampaignServiceImpl implements CampaignService {

	private static final Logger logger = LoggerFactory.getLogger(CampaignServiceImpl.class);
	private static final String EMPTY_LIST = "Cannot publish a campaign for an empty list.";
	private static final String NOT_ALLOWED = "Cannot edit a campaign in published or completed status.";
	private static final String DELETE_NOT_ALLOWED = "Cannot Delete a In Progress Campaign. Wait for the campaign to" +
			" complete before deleting.";

	@Value("${soip.server}")
	private String serverUrl;

	@Value("${soip.env}")
	private String env;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private CampaignRepository campaignRepository;

	@Autowired
	private EntityUtils entityUtils;

	@Autowired
	private CampaignStatusService campaignStatusService;

	@Autowired
	private MessageStatusRepository messageStatusRepository;

	@Autowired
	private MessageRepository messageRepository;

	@Autowired
	private CampaignReplyRepository campaignReplyRepository;

	@Autowired
	private WhatsappQRConnectionRepository whatsappQRConnectionRepository;

	@Autowired
	@Qualifier("CampaignEntityMapper")
	private Function<CampaignEntity, Campaign> campaignEntityMapper;

	@Autowired
	private MarketingDashboardValidator validator;

	@Override
	public Campaign saveCampaignInDraft(String userId, SaveCampaignRequest request) {
		CampaignEntity entity = saveEntityInDraft(userId, request);
		logger.info("Execution for save campaign for user : {} finished successfully.", userId);
		return campaignEntityMapper.apply(entity);
	}

	@Override
	public Campaign getCampaign(String userId, String campaignId) {
		CampaignEntity entity = entityUtils.fetchCampaignEntity(userId, campaignId);
		return campaignEntityMapper.apply(entity);
	}

	@Override
	public List<Campaign> getAllCampaigns(String userId) {
		validateField(userId, USERID);
		List<CampaignEntity> campaignEntities = campaignRepository.findAllByUserId(userId);
		logger.info("Campaigns Found for user : {}", userId);
		List<Campaign> campaigns = new ArrayList<>();
		campaignEntities.forEach(entity -> campaigns.add(campaignEntityMapper.apply(entity)));
		return campaigns;
	}

	@Override
	public Campaign publishCampaign(String userId, String campaignId) {
		CampaignEntity entity = entityUtils.fetchCampaignEntity(userId, campaignId);
		return campaignEntityMapper.apply(publishCampaignEntity(entity));
	}

	@Override
	public Campaign publishAndSaveCampaign(String userId, SaveCampaignRequest request) {
		CampaignEntity saveEntity = saveEntityInDraft(userId, request);
		logger.info("Execution for save campaign for user : {} finished successfully.", userId);
		return campaignEntityMapper.apply(publishCampaignEntity(saveEntity));
	}

	@Override
	public Campaign updateCampaign(String userId, String campaignId, SaveCampaignRequest request) {
		logger.info("Request for updating campaign started for campaignId : {}", campaignId);
		List<String> errors = validator.checkSaveCampaignRequest(request);
		if (!errors.isEmpty()) {
			logger.info("Errors detected while updating campaign, exception thrown");
			throw new InvalidRequestException(errors);
		}
		CampaignEntity entity = entityUtils.fetchCampaignEntity(userId, campaignId);
		if (!entity.getStatus().equals(Status.DRAFT.name())) {
			logger.info("Campaign not in Draft State, throwing exception");
			throw new InvalidRequestException(Collections.singletonList(NOT_ALLOWED));
		}
		if (!request.getName().equals(entity.getName())) {
			Optional<CampaignEntity> existingEntity = campaignRepository
					.findByNameAndUserId(request.getName(), entity.getUserId());
			if (existingEntity.isPresent()) {
				logger.info("Resource Already Exists, throwing exception");
				throw new ResourceAlreadyExistsException("Campaign", request.getName());
			}
		}
		MessageEntity messageEntity = entityUtils.fetchMessageEntity(entity.getMessageId());
		if (!request.getMessage().equals(messageEntity.getMessage())) {
			messageEntity.setMessage(request.getMessage());
			messageRepository.save(messageEntity);
		}
		entity.setChannelId(request.getChannelId());
		entity.setListId(request.getListId());
		entity.setName(request.getName());
		entity.setLastUpdatedDate(LocalDateTime.now());
		entity.setSender(request.getSender());
		CampaignEntity updated = campaignRepository.save(entity);
		logger.info("Campaign Entity updated at : {}", LocalDateTime.now());
		return campaignEntityMapper.apply(updated);
	}

	@Override
	public void deleteCampaign(String userId, String campaignId) {
		logger.info("Preparing to delete campaign : {}", campaignId);
		CampaignEntity entity = entityUtils.fetchCampaignEntity(userId, campaignId);
		if (entity.getStatus().equals(Status.PUBLISHED.name())) {
			logger.info("Campaign is already published, exception thrown");
			throw new CustomRuntimeException(DELETE_NOT_ALLOWED);
		} else {
			messageRepository.deleteById(entity.getMessageId());
			campaignRepository.delete(entity);
			logger.info("Campaign Deleted Successfully at {}", LocalDateTime.now());
		}
	}

	@Override
	public List<CampaignReport> getCampaignReport(String userId, String campaignId) {
		logger.info("Preparing Campaign Report for user : {} and Campaign ID : {}", userId, campaignId);
		CampaignEntity entity = entityUtils.fetchCampaignEntity(userId, campaignId);
		List<MessageStatusEntity> messageStatusEntities = messageStatusRepository.findAllByCampaignId(campaignId);
		ListEntity list = entityUtils.fetchListEntity(userId, entity.getListId());
		MessageEntity messageEntity = entityUtils.fetchMessageEntity(entity.getMessageId());
		ChannelEntity channelEntity = entityUtils.fetchChannelEntity(entity.getChannelId());
		List<CampaignReport> campaignReports = new ArrayList<>();
		messageStatusEntities.forEach(message -> {
			LocalDateTime timestamp = LocalDateTime
					.ofInstant(Instant.ofEpochMilli(Long.parseLong(message.getTimestamp())), ZoneId.systemDefault());
			campaignReports.add(CampaignReport.builder().name(entity.getName()).listName(list.getName())
			                                  .channelName(channelEntity.getChannelName())
			                                  .message(messageEntity.getMessage()).status(message.getStatus())
			                                  .sender(entity.getSender()).recipient(message.getPhone())
			                                  .timestamp(timestamp).build());
		});
		return campaignReports;
	}

	@Override
	public void saveReplies(ReplySaveRequest replySaveRequest) {
		logger.info("Saving Reply : {} ", replySaveRequest);
		List<String> errors = validator.validateReplyRequest(replySaveRequest);
		if (!errors.isEmpty()) {
			logger.info("Invalid Request for saving reply, throwing exception");
			throw new InvalidRequestException(errors);
		}
		LocalDateTime timestamp = LocalDateTime
				.ofInstant(Instant.ofEpochMilli(Long.parseLong(replySaveRequest.getTimestamp())),
				           ZoneId.systemDefault());
		campaignReplyRepository.save(new CampaignReply(replySaveRequest.getCampaignId(), replySaveRequest.getMessage(),
		                                               replySaveRequest.getPhone(), timestamp));
		logger.info("Reply saved successfully for campaign : {}", replySaveRequest.getCampaignId());
	}

	private CampaignEntity saveEntityInDraft(String userId, SaveCampaignRequest request) {
		logger.info("Execution for save campaign started for user : {}", userId);
		validateField(userId, USERID);
		List<String> errors = validator.checkSaveCampaignRequest(request);
		if (!errors.isEmpty()) {
			logger.info("Errors detected while saving campaign, exception thrown");
			throw new InvalidRequestException(errors);
		}
		ListEntity listEntity = entityUtils.fetchListEntity(userId, request.getListId());
		if (!listEntity.isActive()) {
			logger.info("Throwing Exception, Inactive List Supplied : {}", listEntity.getName());
			throw new CustomRuntimeException("Inactive List : " + listEntity.getName());
		}
		Optional<CampaignEntity> existingEntity = campaignRepository.findByNameAndUserId(request.getName(), userId);
		if (existingEntity.isPresent()) {
			logger.info("Resource Already Exists, throwing exception");
			throw new ResourceAlreadyExistsException("Campaign", request.getName());
		}
		logger.info("Creating message entity");
		MessageEntity messageEntity = messageRepository.save(new MessageEntity(request.getMessage()));
		CampaignEntity campaignEntity = new CampaignEntity(userId, request.getListId(), request
				.getChannelId(), request.getName(), messageEntity.getMessageId(), Status.DRAFT.name(),
		                                                   request.getSender());
		return campaignRepository.save(campaignEntity);
	}

	private int getListSize(String listId, String userId) {
		ListEntity list = entityUtils.fetchListEntity(userId, listId);
		if (!list.isActive()) {
			logger.info("Throwing Exception, Inactive List Supplied : {}", list.getName());
			throw new CustomRuntimeException("Inactive List : " + list.getName());
		}
		if (list.getContactEntities().size() == 0) {
			logger.info("Empty List Detected, throwing exception");
			throw new InvalidRequestException(Collections.singletonList(EMPTY_LIST));
		}
		return list.getContactEntities().size();
	}

	private CampaignEntity publishCampaignEntity(CampaignEntity entity) {
		logger.info("Request for publishing campaign started for campaignId : {}", entity.getCampaignId());
		int listSize = getListSize(entity.getListId(), entity.getUserId());
		Status status = Status.valueOf(entity.getStatus());
		if (status != Status.DRAFT) {
			logger.info("Campaign not in Draft State, throwing exception");
			throw new CustomRuntimeException("Cannot publish an already published or completed campaign");
		}
		MessageEntity messageEntity = entityUtils.fetchMessageEntity(entity.getMessageId());
		ChannelEntity channelEntity = entityUtils.fetchChannelEntity(entity.getChannelId());
		if(channelEntity.getChannelName().equals(WHATSAPP)){
			Optional<WhatsappQRConnection> whatsappQRConnection = whatsappQRConnectionRepository.findByUserId(entity.getUserId());
			if(whatsappQRConnection.isEmpty() || !whatsappQRConnection.get().isConnected()){
				logger.info("Whatsapp Channel is not connected");
				throw new CustomRuntimeException("Whatsapp is not connected");
			}
		}
		ResponseEntity<String> response = callSoipApiForPublish(entity, messageEntity, channelEntity);
		logger.info("Response of SOIP API for Publishing Campaign : {}", response.getStatusCode());
		if (response.getStatusCode().is2xxSuccessful()) {
			logger.info("Publish Campaign via SOIP succeeded. Creating campaign status entity");
			entity.setStatus(Status.PUBLISHED.name());
			entity.setPublishedDate(LocalDateTime.now());
			entity.setLastUpdatedDate(LocalDateTime.now());
			entity.setPublishedListSize(listSize);
			CampaignEntity updatedEntity = campaignRepository.save(entity);
			logger.info("Campaign Status Updated to Published");
			return updatedEntity;
		} else {
			logger.info("Publish Campaign via SOIP failed, throwing exception");
			JSONObject obj = new JSONObject(response.getBody());
			throw new CustomRuntimeException(obj.getString(MESSAGE));
		}
	}

	private ResponseEntity<String> callSoipApiForPublish(CampaignEntity entity, MessageEntity messageEntity,
	                                                     ChannelEntity channelEntity) {
		logger.info("Preparing Request to call SOIP APIs for Publishing Campaign : {}", entity.getCampaignId());
		String sender = channelEntity.getChannelName().equals(SMS) ? entity.getSender() : entity.getUserId();
		String url = serverUrl + env + "/phone/" + sender.substring(0, 3) + "/" + sender.substring(3) + "/business/msg";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		LinkedMultiValueMap<String, String> map = getParamsMap(entity.getCampaignId(), messageEntity.getMessage(),
		                                                       entity.getListId(), entity.getUserId(),
		                                                       channelEntity.getChannelName());
		HttpEntity<LinkedMultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
		logger.info("Calling SOIP APIs for Publishing Campaign with request= {}", request);
		try {
			return restTemplate.postForEntity(url, request, String.class);
		} catch (HttpClientErrorException e) {
			logger.info("Publish Campaign via SOIP failed, throwing exception");
			JSONObject obj = new JSONObject(e.getResponseBodyAsString());
			throw new CustomRuntimeException(obj.getString(MESSAGE) + ". Please logout and try again.");
		}
	}

	private LinkedMultiValueMap<String, String> getParamsMap(String campaignId, String message, String listId,
	                                                         String userId, String channel) {
		LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		ListEntity list = entityUtils.fetchListEntity(userId, listId);
		Set<ContactEntity> contacts = list.getContactEntities();
		StringBuilder stringBuilder = new StringBuilder();
		contacts.forEach(contact -> {
			String phoneNumber = contact.getPhoneNumber();
			String countryCode = phoneNumber.split("-")[0];
			String phone = phoneNumber.split("-")[1];
			stringBuilder.append(countryCode).append(phone).append(",");
		});
		params.add("id", campaignId);
		params.add(MESSAGE, message);
		params.add("dest", stringBuilder.deleteCharAt(stringBuilder.length() - 1).toString());
		params.add("channel", channel);
		return params;
	}
}
