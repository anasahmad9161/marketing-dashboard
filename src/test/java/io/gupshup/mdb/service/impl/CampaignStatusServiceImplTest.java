package io.gupshup.mdb.service.impl;

import io.gupshup.mdb.dto.campaign.CampaignStatus;
import io.gupshup.mdb.dto.campaign.MessageStatusRequest;
import io.gupshup.mdb.entities.CampaignEntity;
import io.gupshup.mdb.entities.MessageStatusEntity;
import io.gupshup.mdb.exceptions.InvalidRequestException;
import io.gupshup.mdb.repository.MessageStatusRepository;
import io.gupshup.mdb.utils.EntityUtils;
import io.gupshup.mdb.validator.MarketingDashboardValidator;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CampaignStatusServiceImplTest {

	@Mock
	private MessageStatusRepository campaignMessageRepository;
	@Mock
	private MarketingDashboardValidator validator;
	@Mock
	private EntityUtils entityUtils;
	@InjectMocks
	private CampaignStatusServiceImpl campaignStatusService;

	@Test
	public void shouldThrowExceptionIfInvalidRequest() {
		List<String> list = new ArrayList<>();
		list.add("campaignId");
		MessageStatusRequest request = new MessageStatusRequest();
		request.setCampaignId("campaignId");
		request.setStatus("Sent");
		request.setPhone("Phone");
		request.setTimestamp("timestamp");
		when(validator.validateCampaignMessageRequest(request)).thenReturn(list);
		assertThrows(InvalidRequestException.class,
		             () -> campaignStatusService.createOrUpdateCampaignMessage(request));

	}

	@Test
	public void shouldCheckIfCampaignMassageIsAlreadyPresent() {
		MessageStatusEntity campaignMessageEntity = new MessageStatusEntity();
		campaignMessageEntity.setCampaignId("campaignId");
		campaignMessageEntity.setPhone("phone");
		MessageStatusRequest request = new MessageStatusRequest();
		request.setCampaignId("campaignId");
		request.setStatus("SENT");
		request.setPhone("phone");
		request.setTimestamp("timestamp");
		List<String> list = new ArrayList<>();
		when(validator.validateCampaignMessageRequest(any())).thenReturn(list);
		when(campaignMessageRepository.findByCampaignIdAndPhone(any(), anyString()))
				.thenReturn(Optional.of(campaignMessageEntity));
		when(campaignMessageRepository.save(any())).thenReturn(campaignMessageEntity);
		campaignStatusService.createOrUpdateCampaignMessage(request);
		verify(campaignMessageRepository, times(1)).findByCampaignIdAndPhone(any(), anyString());
		verify(campaignMessageRepository, times(1)).save(any());

	}

	@Test
	public void shouldCheckIfCreatingNewCampaignMassage() {
		MessageStatusEntity campaignMessageEntity = new MessageStatusEntity();
		MessageStatusRequest request = new MessageStatusRequest();
		request.setCampaignId("campaignId");
		request.setStatus("STATUS");
		request.setPhone("phone");
		request.setTimestamp("timestamp");
		List<String> list = new ArrayList<>();
		when(validator.validateCampaignMessageRequest(any())).thenReturn(list);
		when(campaignMessageRepository.save(any())).thenReturn(campaignMessageEntity);
		campaignStatusService.createOrUpdateCampaignMessage(request);
		verify(campaignMessageRepository, times(1)).save(any());
	}

	@Test
	public void getCampaignStatusTestIfListOfCampaignIsPresent() {
		MessageStatusEntity entity = new MessageStatusEntity();
		entity.setCampaignId("campaignId");
		entity.setId("massageId");
		entity.setStatus("STATUS");
		entity.setTimestamp("timestamp");
		entity.setPhone("phone");
		List<MessageStatusEntity> messageEntities = new ArrayList<>();
		messageEntities.add(entity);
		CampaignEntity campaignEntity = new CampaignEntity();
		campaignEntity.setCampaignId("campaignId");
		campaignEntity.setUserId("userid");
		when(entityUtils.fetchCampaignEntity(anyString(), anyString())).thenReturn(campaignEntity);
		when(campaignMessageRepository.findAllByCampaignId(anyString())).thenReturn(messageEntities);
		CampaignStatus status = campaignStatusService.getCampaignStatus("userid", "campaignId");
		Assert.assertEquals(1, status.getSuccessCount());
		Assert.assertEquals(0, status.getFailedCount());
	}

	@Test
	public void getCampaignStatusTestIfListOfCampaignIsFailed() {
		MessageStatusEntity entity = new MessageStatusEntity();
		entity.setStatus("FAILED");
		List<MessageStatusEntity> messageEntities = new ArrayList<>();
		messageEntities.add(entity);
		CampaignEntity campaignEntity = new CampaignEntity();

		when(entityUtils.fetchCampaignEntity(anyString(), anyString())).thenReturn(campaignEntity);
		when(campaignMessageRepository.findAllByCampaignId(anyString())).thenReturn(messageEntities);
		CampaignStatus status = campaignStatusService.getCampaignStatus("userid", "CampaignId");
		Assert.assertEquals(0, status.getSuccessCount());
		Assert.assertEquals(1, status.getFailedCount());
	}

}
