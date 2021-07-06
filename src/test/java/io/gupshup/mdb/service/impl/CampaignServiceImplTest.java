package io.gupshup.mdb.service.impl;

import io.gupshup.mdb.dto.campaign.Campaign;
import io.gupshup.mdb.dto.campaign.CampaignReport;
import io.gupshup.mdb.dto.campaign.SaveCampaignRequest;
import io.gupshup.mdb.entities.CampaignEntity;
import io.gupshup.mdb.entities.ChannelEntity;
import io.gupshup.mdb.entities.ContactEntity;
import io.gupshup.mdb.entities.ListEntity;
import io.gupshup.mdb.entities.MessageEntity;
import io.gupshup.mdb.entities.MessageStatusEntity;
import io.gupshup.mdb.exceptions.CustomRuntimeException;
import io.gupshup.mdb.exceptions.InvalidRequestException;
import io.gupshup.mdb.exceptions.ResourceAlreadyExistsException;
import io.gupshup.mdb.repository.CampaignRepository;
import io.gupshup.mdb.repository.MessageRepository;
import io.gupshup.mdb.repository.MessageStatusRepository;
import io.gupshup.mdb.utils.EntityUtils;
import io.gupshup.mdb.validator.MarketingDashboardValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import static io.gupshup.mdb.dto.campaign.Status.COMPLETED;
import static io.gupshup.mdb.dto.campaign.Status.PUBLISHED;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CampaignServiceImplTest {

	@Mock
	private RestTemplate restTemplate;
	@Mock
	private CampaignRepository campaignRepository;
	@Mock
	private EntityUtils entityUtils;
	@Mock
	private MessageRepository messageRepository;
	@Mock
	private MessageStatusRepository campaignMessageRepository;
	@Mock
	private Function<CampaignEntity, Campaign> campaignEntityMapper;
	@Mock
	private MarketingDashboardValidator validator;
	@InjectMocks
	private CampaignServiceImpl campaignService;


	@Test
	public void shouldThrowExceptionWhenInvalidRequestForSaveCampaign() {
		List<String> list = new ArrayList<>();
		list.add("error");
		SaveCampaignRequest request = testCreateSaveCampaignRequest("name", "listId", "channelId", "+911234567890",
		                                                            "message");
		when(validator.checkSaveCampaignRequest(any())).thenReturn(list);
		assertThrows(InvalidRequestException.class, () -> campaignService.saveCampaignInDraft("userid", request));
	}

	@Test
	public void shouldThrowExceptionIfResourcePresentForSaveCampaign() {
		CampaignEntity campaignEntity = new CampaignEntity();
		ListEntity entity = new ListEntity("name", "userid");
		SaveCampaignRequest request = testCreateSaveCampaignRequest("name", "listId", "channelId", "+911234567890",
		                                                            "message");
		when(entityUtils.fetchListEntity(anyString(), anyString())).thenReturn(entity);
		when(campaignRepository.findByNameAndUserId("name", "userid")).thenReturn(Optional.of(campaignEntity));
		assertThrows(ResourceAlreadyExistsException.class,
		             () -> campaignService.saveCampaignInDraft("userid", request));
	}

	@Test
	public void shouldThrowExceptionWhenInActiveRequestForSaveCampaign() {
		SaveCampaignRequest request = testCreateSaveCampaignRequest("name", "listId", "channelId", "+911234567890",
		                                                            "message");
		ListEntity entity = new ListEntity();
		entity.setActive(false);
		when(validator.checkSaveCampaignRequest(any())).thenReturn(new ArrayList<>());
		when(entityUtils.fetchListEntity(anyString(), anyString())).thenReturn(entity);
		assertThrows(CustomRuntimeException.class,
		             () -> campaignService.saveCampaignInDraft("userid", request));
	}


	@Test
	public void saveCampaignInDraftTestIfValidRequest() {

		Campaign campaign = Campaign.builder().build();
		campaign.setCampaignId("campaignId");
		campaign.setStatus(COMPLETED);
		campaign.setName("name");
		campaign.setChannelName("channelName");
		campaign.setMessage("massage");
		campaign.setSender("sender");
		List<String> list = new ArrayList<>();
		CampaignEntity campaignEntity = new CampaignEntity();
		campaignEntity.setCampaignId("campaignId");
		SaveCampaignRequest request = testCreateSaveCampaignRequest("name", "listId", "channelId", "+911234567890",
		                                                            "message");
		MessageEntity messageEntity = new MessageEntity("message");
		messageEntity.setMessageId("messageId");
		when(validator.checkSaveCampaignRequest(request)).thenReturn(list);
		ListEntity listEntity = new ListEntity("name", "userid");
		when(entityUtils.fetchListEntity(anyString(), anyString())).thenReturn(listEntity);
		when(campaignEntityMapper.apply(any())).thenReturn(campaign);
		when(messageRepository.save(any())).thenReturn(messageEntity);
		Campaign campaign1 = campaignService.saveCampaignInDraft("userid", request);
		assert campaign1 != null;
		assert campaign1.getCampaignId().equals("campaignId");
		assert campaign1.getName().equals("name");
		assert campaign1.getStatus().equals(COMPLETED);
		assert campaign1.getChannelName().equals("channelName");
		assert campaign1.getMessage().equals("massage");
		assert campaign1.getSender().equals("sender");
	}

	@Test
	public void getCampaignTest() {
		Campaign campaign = Campaign.builder().campaignId("campaignId").sender("sender").channelName("channelName")
		                            .name("name").message("message").build();
		CampaignEntity campaignEntity = new CampaignEntity();
		when(entityUtils.fetchCampaignEntity(anyString(), anyString())).thenReturn(campaignEntity);
		when(campaignEntityMapper.apply(any())).thenReturn(campaign);
		Campaign campaign1 = campaignService.getCampaign("userid", "campaignId");
		assert campaign1.getCampaignId().equals("campaignId");
		assert campaign1.getMessage().equals("message");
	}

	@Test
	public void getAllCampaignsTest() {
		Campaign campaign = Campaign.builder().campaignId("campaignId").sender("sender").channelName("channelName")
		                            .name("name").message("message").status(COMPLETED).build();
		CampaignEntity campaignEntity = new CampaignEntity();
		List<CampaignEntity> campaignEntities = new ArrayList<>();
		campaignEntities.add(campaignEntity);
		when(campaignRepository.findAllByUserId(anyString())).thenReturn(campaignEntities);
		when(campaignEntityMapper.apply(any())).thenReturn(campaign);
		List<Campaign> list = campaignService.getAllCampaigns("userid");
		assert list != null;
		assert list.get(0).getCampaignId().equals("campaignId");
		assert list.get(0).getName().equals("name");
		assert list.get(0).getStatus().equals(COMPLETED);
		assert list.get(0).getSender().equals("sender");
		assert list.get(0).getMessage().equals("message");
		assert list.get(0).getChannelName().equals("channelName");
	}

	@Test
	public void shouldThrowExceptionWhenInvalidRequestForUpdateCampaign() {
		List<String> list = new ArrayList<>();
		list.add("error");
		SaveCampaignRequest request = testCreateSaveCampaignRequest("name", "listId", "channelId", "+911234567890",
		                                                            "message");
		when(validator.checkSaveCampaignRequest(any())).thenReturn(list);
		assertThrows(InvalidRequestException.class,
		             () -> campaignService.updateCampaign("userid", "campaignId", request));
	}

	@Test
	public void shouldThrowExceptionIfStatusIsNotDraftForUpdateCampaign() {
		CampaignEntity entity = new CampaignEntity();
		entity.setStatus(PUBLISHED.name());
		SaveCampaignRequest request = testCreateSaveCampaignRequest("name", "listId", "channelId", "+911234567890",
		                                                            "message");
		when(validator.checkSaveCampaignRequest(any())).thenReturn(new ArrayList<>());
		when(entityUtils.fetchCampaignEntity(anyString(), anyString())).thenReturn(entity);
		assertThrows(InvalidRequestException.class,
		             () -> campaignService.updateCampaign("userid", "campaignId", request));
	}

	@Test
	public void shouldThrowExceptionIfResourcePresentForUpdateCampaign() {
		CampaignEntity entity = new CampaignEntity();
		entity.setStatus("DRAFT");
		entity.setCampaignId("campaignId");
		entity.setUserId("userid");
		entity.setName("name");
		SaveCampaignRequest request = testCreateSaveCampaignRequest("newName", "listId", "channelId", "+911234567890",
		                                                            "message");
		when(entityUtils.fetchCampaignEntity(anyString(), anyString())).thenReturn(entity);
		when(campaignRepository.findByNameAndUserId(anyString(), anyString())).thenReturn(Optional.of(entity));
		assertThrows(ResourceAlreadyExistsException.class, () ->
				campaignService.updateCampaign("userid", "campaignId", request));
	}

	@Test
	public void updateCampaignTestIfValidRequest() {
		Campaign campaign = Campaign.builder().campaignId("campaignId").sender("sender").channelName("channelName")
		                            .name("name").message("message").status(COMPLETED).build();
		CampaignEntity campaignEntity = new CampaignEntity();
		campaignEntity.setCampaignId("campaignId");
		campaignEntity.setStatus("DRAFT");
		campaignEntity.setMessageId("messageId");
		SaveCampaignRequest request = testCreateSaveCampaignRequest("name", "listId", "channelId", "+911234567890",
		                                                            "message");
		when(validator.checkSaveCampaignRequest(request)).thenReturn(new ArrayList<>());
		MessageEntity messageEntity = new MessageEntity("message");
		messageEntity.setMessageId("messageId");
		when(entityUtils.fetchCampaignEntity(anyString(), anyString())).thenReturn(campaignEntity);
		when(campaignRepository.save(any())).thenReturn(campaignEntity);
		when(campaignEntityMapper.apply(any())).thenReturn(campaign);
		when(entityUtils.fetchMessageEntity(any())).thenReturn(messageEntity);
		Campaign campaign1 = campaignService.updateCampaign("userid", "campaignId", request);
		assert campaign1 != null;
		assert campaign1.getCampaignId().equals("campaignId");
		assert campaign1.getName().equals("name");
		assert campaign1.getStatus().equals(COMPLETED);
		assert campaign1.getChannelName().equals("channelName");
		assert campaign1.getMessage().equals("message");
		assert campaign1.getSender().equals("sender");
	}

	@Test
	public void shouldThrowExceptionIfFetchCampaignEntityIsPublishedForDeletingCampaign() {
		CampaignEntity campaignEntity = new CampaignEntity();
		campaignEntity.setCampaignId("campaignId");
		campaignEntity.setStatus("PUBLISHED");
		campaignEntity.setUserId("userid");
		when(entityUtils.fetchCampaignEntity(anyString(), anyString())).thenReturn(campaignEntity);
		assertThrows(CustomRuntimeException.class, () ->
				campaignService.deleteCampaign("userid", "campaignId"));
	}

	@Test
	public void deleteCampaignTest() {
		CampaignEntity campaignEntity = new CampaignEntity();
		campaignEntity.setCampaignId("campaignId");
		campaignEntity.setStatus("Status");
		campaignEntity.setUserId("userid");
		when(entityUtils.fetchCampaignEntity(anyString(), anyString())).thenReturn(campaignEntity);
		campaignService.deleteCampaign("userid", "campaignId");
		verify(entityUtils, times(1)).fetchCampaignEntity(anyString(), anyString());
		verify(campaignRepository, times(1)).delete(any());
	}

	@Test
	public void getCampaignReportTest() {
		CampaignEntity campaignEntity = testCreateEntity("campaignId", "name", "listId", "channelId", "messageId",
		                                                 "userId", "PUBLISHED", "+911234567890");
		MessageStatusEntity campaignMessageEntity = new MessageStatusEntity();
		campaignMessageEntity.setCampaignId("campaignId");
		campaignMessageEntity.setStatus("status");
		campaignMessageEntity.setPhone("phone");
		campaignMessageEntity.setTimestamp("1600002232");
		List<MessageStatusEntity> list = new ArrayList<>();
		list.add(campaignMessageEntity);
		ListEntity listEntity = new ListEntity("name", "userid");
		listEntity.setCreationDate(LocalDateTime.now());
		listEntity.setLastUpdatedDate(LocalDateTime.now());
		ChannelEntity channelEntity = new ChannelEntity("SMS");
		MessageEntity messageEntity = new MessageEntity("message");
		messageEntity.setMessageId("messageId");
		when(entityUtils.fetchCampaignEntity(anyString(), anyString())).thenReturn(campaignEntity);
		when(campaignMessageRepository.findAllByCampaignId(anyString())).thenReturn(list);
		when(entityUtils.fetchListEntity(anyString(), anyString())).thenReturn(listEntity);
		when(entityUtils.fetchChannelEntity(anyString())).thenReturn(channelEntity);
		when(entityUtils.fetchMessageEntity(anyString())).thenReturn(messageEntity);
		List<CampaignReport> list1 = campaignService.getCampaignReport("userid", "campaignId");
		assert list1 != null;
	}

	@Test
	public void shouldThrowExceptionIfListInActiveForPublishCampaign() {
		CampaignEntity campaignEntity = new CampaignEntity();
		campaignEntity.setCampaignId("campaignId");
		campaignEntity.setStatus("Status");
		campaignEntity.setUserId("userid");
		campaignEntity.setListId("listId");
		ListEntity entity = new ListEntity("name", "userid");
		entity.setListId("listId");
		entity.setActive(!entity.isActive());
		when(entityUtils.fetchCampaignEntity(anyString(), anyString())).thenReturn(campaignEntity);
		when(entityUtils.fetchListEntity(anyString(), anyString())).thenReturn(entity);
		assertThrows(CustomRuntimeException.class, () ->
				campaignService.publishCampaign("userid", "campaignId"));
	}

	@Test
	public void shouldThrowExceptionIfListInvalidRequestForPublishCampaign() {
		CampaignEntity campaignEntity = new CampaignEntity();
		campaignEntity.setCampaignId("campaignId");
		campaignEntity.setStatus("Status");
		campaignEntity.setUserId("userid");
		campaignEntity.setListId("listId");
		ListEntity entity = new ListEntity("name", "userid");
		entity.setListId("listId");
		entity.setContactEntities(new HashSet<>());
		when(entityUtils.fetchCampaignEntity(anyString(), anyString())).thenReturn(campaignEntity);
		when(entityUtils.fetchListEntity(anyString(), anyString())).thenReturn(entity);
		assertThrows(InvalidRequestException.class, () ->
				campaignService.publishCampaign("userid", "campaignId"));
	}

	@Test
	public void shouldThrowExceptionIfStatusNotDraftForPublishCampaign() {
		CampaignEntity campaignEntity = new CampaignEntity();
		campaignEntity.setCampaignId("campaignId");
		campaignEntity.setStatus("Status");
		campaignEntity.setUserId("userid");
		campaignEntity.setListId("listId");
		campaignEntity.setStatus("PUBLISHED");
		ContactEntity contactEntity = new ContactEntity("phone", "userid");
		Set<ContactEntity> set = new HashSet<>();
		set.add(contactEntity);
		ListEntity entity = new ListEntity("name", "userid");
		entity.setListId("listId");
		entity.setContactEntities(set);

		when(entityUtils.fetchCampaignEntity(anyString(), anyString())).thenReturn(campaignEntity);
		when(entityUtils.fetchListEntity(anyString(), anyString())).thenReturn(entity);
		assertThrows(CustomRuntimeException.class, () ->
				campaignService.publishCampaign("userid", "campaignId"));
	}

	@Test
	public void publishCampaignForValidRequest() {
		Campaign campaign = Campaign.builder().build();
		campaign.setCampaignId("campaignId");
		campaign.setStatus(PUBLISHED);
		campaign.setName("name");
		campaign.setChannelName("channelName");
		campaign.setMessage("massage");
		campaign.setSender("sender");
		CampaignEntity campaignEntity = testCreateEntity("campaignId", "name", "listId", "channelId", "messageId",
		                                                 "userId", "DRAFT", "+911234567890");
		MessageEntity messageEntity = new MessageEntity("message");
		messageEntity.setMessageId("messageId");
		ChannelEntity channelEntity = new ChannelEntity("SMS");
		ContactEntity contactEntity = new ContactEntity("+91-9967821143", "userid");
		Set<ContactEntity> set = new HashSet<>();
		set.add(contactEntity);
		ListEntity entity = new ListEntity("name", "userid");
		entity.setListId("listId");
		entity.setContactEntities(set);
		when(entityUtils.fetchCampaignEntity(anyString(), anyString())).thenReturn(campaignEntity);
		when(entityUtils.fetchListEntity(anyString(), anyString())).thenReturn(entity);
		when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), eq(String.class)))
				.thenReturn(new ResponseEntity<>("String", HttpStatus.OK));
		when(campaignRepository.save(any())).thenReturn(campaignEntity);
		when(campaignEntityMapper.apply(any())).thenReturn(campaign);
		when(entityUtils.fetchMessageEntity(anyString())).thenReturn(messageEntity);
		when(entityUtils.fetchChannelEntity(anyString())).thenReturn(channelEntity);
		Campaign campaign1 = campaignService.publishCampaign("userid", "campaignId");
		assert campaign1.getCampaignId().equals("campaignId");
		assert campaign1.getMessage().equals("massage");
		assert campaign1.getSender().equals("sender");
		assert campaign1.getStatus().equals(PUBLISHED);
		assert campaign1.getName().equals("name");
	}


	@Test
	public void publishAndSaveCampaignTestIfValidRequest() {
		Campaign campaign = Campaign.builder().build();
		campaign.setCampaignId("campaignId");
		campaign.setMessage("massage");
		campaign.setSender("sender");
		campaign.setName("name");
		campaign.setStatus(PUBLISHED);
		campaign.setListName("listName");
		List<String> list = new ArrayList<>();
		CampaignEntity campaignEntity = testCreateEntity("campaignId", "name", "listId", "channelId", "messageId",
		                                                 "userId", "DRAFT", "+911234567890");
		SaveCampaignRequest request = testCreateSaveCampaignRequest("name", "listId", "channelId", "+911234567890",
		                                                            "message");
		MessageEntity messageEntity = new MessageEntity("message");
		messageEntity.setMessageId("messageId");
		ChannelEntity channelEntity = new ChannelEntity("SMS");
		ContactEntity contactEntity = new ContactEntity("+91-1234567890", "userid");
		Set<ContactEntity> contactEntities = new HashSet<>();
		contactEntities.add(contactEntity);
		ListEntity listEntity = new ListEntity("name", "userid");
		listEntity.setListId("listId");
		listEntity.setContactEntities(contactEntities);
		when(validator.checkSaveCampaignRequest(any())).thenReturn(list);
		when(entityUtils.fetchListEntity(anyString(), anyString())).thenReturn(listEntity);
		when(campaignRepository.save(any())).thenReturn(campaignEntity);
		when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), eq(String.class)))
				.thenReturn(new ResponseEntity<>("String", HttpStatus.OK));
		when(campaignEntityMapper.apply(any())).thenReturn(campaign);
		when(entityUtils.fetchMessageEntity(anyString())).thenReturn(messageEntity);
		when(messageRepository.save(any())).thenReturn(messageEntity);
		when(entityUtils.fetchChannelEntity(anyString())).thenReturn(channelEntity);
		Campaign campaign1 = campaignService.publishAndSaveCampaign("userid", request);
		assert campaign1.getCampaignId().equals("campaignId");
		assert campaign1.getMessage().equals("massage");
		assert campaign1.getSender().equals("sender");
		assert campaign1.getStatus().equals(PUBLISHED);
		assert campaign1.getName().equals("name");
		assert campaign1.getListName().equals("listName");

	}

	private CampaignEntity testCreateEntity(String campaignId, String name, String listId, String channelId,
	                                        String messageId, String userId, String status, String sender) {
		CampaignEntity campaignEntity = new CampaignEntity(userId, listId, channelId, name, messageId, status, sender);
		campaignEntity.setCampaignId(campaignId);
		return campaignEntity;
	}

	private SaveCampaignRequest testCreateSaveCampaignRequest(String name, String listId, String channelId,
	                                                          String sender, String message) {
		return new SaveCampaignRequest(name, listId, channelId, message, sender);
	}
}