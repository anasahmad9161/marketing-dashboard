package io.gupshup.mdb.controller.rest;

import io.gupshup.mdb.auth.AuthenticationEntity;
import io.gupshup.mdb.auth.AuthenticationService;
import io.gupshup.mdb.dto.campaign.MessageStatusRequest;
import io.gupshup.mdb.dto.campaign.CampaignStatus;
import io.gupshup.mdb.dto.campaign.ReplySaveRequest;
import io.gupshup.mdb.entities.ChannelEntity;
import io.gupshup.mdb.exceptions.AuthenticationException;
import io.gupshup.mdb.service.CampaignService;
import io.gupshup.mdb.service.CampaignStatusService;
import io.gupshup.mdb.service.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static io.gupshup.mdb.constants.APIConstants.API;
import static io.gupshup.mdb.constants.APIConstants.CAMPAIGN;
import static io.gupshup.mdb.constants.APIConstants.CAMPAIGNID;
import static io.gupshup.mdb.constants.APIConstants.CAMPAIGN_ID;
import static io.gupshup.mdb.constants.APIConstants.STATUS;
import static io.gupshup.mdb.constants.APIConstants.USER;
import static io.gupshup.mdb.constants.APIConstants.USER_ID;
import static io.gupshup.mdb.constants.APIConstants.USERID;
import static io.gupshup.mdb.constants.APIConstants.TOKEN;
import static io.gupshup.mdb.constants.APIConstants.VERSION;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping(value = API + VERSION)
public class CommonController {

	private static final String TOKEN_EXPIRED = "Authentication Token Expired. PLease login again";
	@Autowired
	private AuthenticationService authenticationService;

	@Autowired
	private CommonService commonService;

	@Autowired
	private CampaignStatusService campaignStatusService;

	@Autowired
	private CampaignService campaignService;

	@RequestMapping(value = "/channels", method = GET)
	public ResponseEntity<List<ChannelEntity>> getAllChannels() {
		return new ResponseEntity<>(commonService.getChannels(), OK);
	}

	@RequestMapping(value = "/senders", method = GET)
	public ResponseEntity<List<String>> getSenderInfo(@RequestHeader(TOKEN) String token) {
		AuthenticationEntity authenticationEntity = authenticationService.getAuthEntity(token);
		if(authenticationEntity.isExpired()) throw new AuthenticationException(TOKEN_EXPIRED);
		return new ResponseEntity<>(commonService.getSenderPhoneNumbers(authenticationEntity.getApiKey()), OK);
	}

	@RequestMapping(value = "/stats", method = POST)
	public ResponseEntity<Void> updateCampaignStats(@RequestBody MessageStatusRequest messageStatusRequest) {
		campaignStatusService.createOrUpdateCampaignMessage(messageStatusRequest);
		return ResponseEntity.ok().build();
	}

	@RequestMapping(value = USER + USER_ID + CAMPAIGN + CAMPAIGN_ID + STATUS, method = GET, headers = {TOKEN})
	public ResponseEntity<CampaignStatus> getCampaignStatus(@PathVariable(USERID) String userId,
	                                                        @PathVariable(CAMPAIGNID) String campaignId) {
		return new ResponseEntity<>(campaignStatusService.getCampaignStatus(userId, campaignId), OK);
	}

	@RequestMapping(value = "/replies", method = POST)
	public ResponseEntity<Void> saveReplies(@RequestBody ReplySaveRequest replySaveRequest){
		campaignService.saveReplies(replySaveRequest);
		return ResponseEntity.ok().build();
	}

}
