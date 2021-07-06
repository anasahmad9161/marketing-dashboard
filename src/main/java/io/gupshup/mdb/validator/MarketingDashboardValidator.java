package io.gupshup.mdb.validator;

import io.gupshup.mdb.dto.campaign.MessageStatusRequest;
import io.gupshup.mdb.dto.campaign.ReplySaveRequest;
import io.gupshup.mdb.dto.campaign.SaveCampaignRequest;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MarketingDashboardValidator {

	List<String> checkForErrorsInFile(MultipartFile file);

	Boolean checkHeaders(List<String> headerNames);

	List<String> checkForErrorInRecord(CSVRecord csvRecord);

	List<String> checkSaveCampaignRequest(SaveCampaignRequest request);

	List<String> validateCampaignMessageRequest(MessageStatusRequest messageStatusRequest);

	List<String> validateReplyRequest(ReplySaveRequest replySaveRequest);

	List<String> validateExcelRecord(String[] values);
}
