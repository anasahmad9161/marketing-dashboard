package io.gupshup.mdb.validator;

import io.gupshup.mdb.dto.campaign.MessageStatusRequest;
import io.gupshup.mdb.dto.campaign.ReplySaveRequest;
import io.gupshup.mdb.dto.campaign.SaveCampaignRequest;
import io.gupshup.mdb.repository.CampaignRepository;
import io.gupshup.mdb.repository.ChannelRepository;
import io.gupshup.mdb.repository.ListsRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import static io.gupshup.mdb.constants.ServiceConstants.COUNTRY_CODE;
import static io.gupshup.mdb.constants.ServiceConstants.FULL_NAME;
import static io.gupshup.mdb.constants.ServiceConstants.MOBILE_NUMBER;
import static io.gupshup.mdb.constants.ServiceConstants.NICKNAME;
import static io.gupshup.mdb.constants.ServiceConstants.SALUTATION;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MarketingDashboardValidatorImplTest {

	@Mock
	private ListsRepository listsRepository;

	@Mock
	private ChannelRepository channelRepository;

	@Mock
	private CampaignRepository campaignRepository;

	@Mock
	private MultipartFile file;

	@InjectMocks
	private MarketingDashboardValidatorImpl validator;

	@Test
	public void shouldReturnErrorsIfFileIsEmpty() throws Exception {
		File file1 = new File("src/test/resources/empty.csv");
		FileInputStream input = new FileInputStream(file1);
		file = new MockMultipartFile("file1", file1.getName(), "text/csv", input);
		List<String> errors = validator.checkForErrorsInFile(file);
		assert !errors.isEmpty();
		assert errors.get(0).equals("Empty File : empty.csv");
	}

	@Test
	public void shouldNotReturnErrorsIfFileIsNonEmpty() throws Exception {
		File file1 = new File("src/test/resources/sampleTest.csv");
		FileInputStream input = new FileInputStream(file1);
		file = new MockMultipartFile("file1", file1.getName(), "text/csv", input);
		List<String> errors = validator.checkForErrorsInFile(file);
		assert errors.isEmpty();
	}

	@Test
	public void shouldReturnTrueIfHeadersMatch() {
		assert validator.checkHeaders(List.of(FULL_NAME, NICKNAME, SALUTATION, MOBILE_NUMBER, COUNTRY_CODE));
	}

	@Test
	public void shouldReturnFalseIfHeadersDoesNotMatch() {
		assert !validator.checkHeaders(List.of(FULL_NAME, NICKNAME, SALUTATION, "Phone Number", COUNTRY_CODE));
	}

	@Test
	public void shouldReturnExpectedErrorsForInvalidSaveCampaignRequest() {
		SaveCampaignRequest request = new SaveCampaignRequest();
		request.setChannelId("SAAS");
		request.setListId(null);
		request.setMessage("   ");
		request.setName("       ");
		List<String> errors = validator.checkSaveCampaignRequest(request);

		assert errors.size() == 4;
		assert errors.stream().anyMatch(err -> err.equals("Empty Field : Name"));
	}

	@Test
	public void shouldReturnExpectedErrorsForSaveCampaignRequestIfListOrChannelDoesNotExist() {
		SaveCampaignRequest request = new SaveCampaignRequest();
		request.setChannelId("id1");
		request.setListId("id2");
		request.setMessage("message");
		request.setName("name");
		request.setSender("sender");
		when(listsRepository.existsById("id2")).thenReturn(false);
		when(channelRepository.existsById("id1")).thenReturn(false);

		List<String> errors = validator.checkSaveCampaignRequest(request);
		assert errors.size() == 2;
		assert errors.get(0).equals("List Does not exists. Please check List ID");
		assert errors.get(1).equals("Channel Does not exists. Please check Channel Names");
	}

	@Test
	public void shouldReturnEmptyListForValidSaveCampaignRequest() {
		SaveCampaignRequest request = new SaveCampaignRequest();
		request.setChannelId("id1");
		request.setListId("id2");
		request.setMessage("message");
		request.setName("name");
		request.setSender("sender");
		when(listsRepository.existsById("id2")).thenReturn(true);
		when(channelRepository.existsById("id1")).thenReturn(true);
		assert validator.checkSaveCampaignRequest(request).isEmpty();
	}

	@Test
	public void shouldReturnExpectedErrorsForCampaignMessageRequestIfCampaignDoesNotExistOrInvalidStatusValue() {
		MessageStatusRequest messageStatusRequest = new MessageStatusRequest();
		messageStatusRequest.setCampaignId("id");
		messageStatusRequest.setStatus("invalid");
		messageStatusRequest.setPhone("phone");
		messageStatusRequest.setTimestamp("timestamp");

		when(campaignRepository.existsById("id")).thenReturn(false);
		List<String> errors = validator.validateCampaignMessageRequest(messageStatusRequest);
		assert errors.size() == 2;
		assert errors.get(0).equals("Campaign Does not exists. Please check Campaign ID");
		assert errors.get(1).equals("Invalid Value No enum constant io.gupshup.mdb.service.impl.MessageStatus.INVALID");
	}

	@Test
	public void shouldReturnExpectedErrorsForCampaignMessageRequestIfInvalidRequest() {
		MessageStatusRequest messageStatusRequest = new MessageStatusRequest();
		messageStatusRequest.setCampaignId("  ");
		messageStatusRequest.setStatus("");
		messageStatusRequest.setPhone(null);

		List<String> errors = validator.validateCampaignMessageRequest(messageStatusRequest);
		assert errors.size() == 4;
		assert errors.stream().anyMatch(err -> err.equals("Empty Field : status"));
	}

	@Test
	public void shouldNotReturnErrorsForCampaignMessageRequestIfValidRequest() {
		MessageStatusRequest messageStatusRequest = new MessageStatusRequest();
		messageStatusRequest.setCampaignId("id");
		messageStatusRequest.setStatus("SENT");
		messageStatusRequest.setPhone("phone");
		messageStatusRequest.setTimestamp("timestamp");

		when(campaignRepository.existsById("id")).thenReturn(true);
		List<String> errors = validator.validateCampaignMessageRequest(messageStatusRequest);
		assert errors.isEmpty();
	}

	@Test
	public void shouldReturnExpectedErrorsForReplySaveRequestIfCampaignDoesNotExist() {
		ReplySaveRequest replySaveRequest = new ReplySaveRequest();
		replySaveRequest.setCampaignId("id");
		replySaveRequest.setMessage("Message");
		replySaveRequest.setPhone("phone");
		replySaveRequest.setTimestamp("timestamp");

		when(campaignRepository.existsById("id")).thenReturn(false);
		List<String> errors = validator.validateReplyRequest(replySaveRequest);
		assert errors.size() == 1;
		assert errors.get(0).equals("Campaign Does not exists. Please check Campaign ID");
	}

	@Test
	public void shouldReturnExpectedErrorsForReplySaveRequestIfInvalidRequest() {
		ReplySaveRequest replySaveRequest = new ReplySaveRequest();
		replySaveRequest.setCampaignId(" ");
		replySaveRequest.setMessage("");
		replySaveRequest.setPhone("    ");
		replySaveRequest.setTimestamp(null);

		List<String> errors = validator.validateReplyRequest(replySaveRequest);
		assert errors.size() == 4;
		assert errors.stream().anyMatch(err -> err.equals("Empty Field : phone"));
	}

	@Test
	public void shouldNotReturnErrorsForReplySaveRequestIfValidRequest() {
		ReplySaveRequest replySaveRequest = new ReplySaveRequest();
		replySaveRequest.setCampaignId("id");
		replySaveRequest.setMessage("Message");
		replySaveRequest.setPhone("phone");
		replySaveRequest.setTimestamp("timestamp");

		when(campaignRepository.existsById("id")).thenReturn(true);
		List<String> errors = validator.validateReplyRequest(replySaveRequest);
		assert errors.isEmpty();
	}

}
