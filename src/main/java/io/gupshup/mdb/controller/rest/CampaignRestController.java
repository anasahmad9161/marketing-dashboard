package io.gupshup.mdb.controller.rest;

import io.gupshup.mdb.auth.AuthenticationService;
import io.gupshup.mdb.dto.campaign.Campaign;
import io.gupshup.mdb.dto.campaign.CampaignReport;
import io.gupshup.mdb.dto.campaign.SaveCampaignRequest;
import io.gupshup.mdb.service.CampaignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static io.gupshup.mdb.constants.APIConstants.API;
import static io.gupshup.mdb.constants.APIConstants.CAMPAIGN;
import static io.gupshup.mdb.constants.APIConstants.CAMPAIGNID;
import static io.gupshup.mdb.constants.APIConstants.CAMPAIGNS;
import static io.gupshup.mdb.constants.APIConstants.CAMPAIGN_ID;
import static io.gupshup.mdb.constants.APIConstants.PUBLISH;
import static io.gupshup.mdb.constants.APIConstants.REPORT;
import static io.gupshup.mdb.constants.APIConstants.TOKEN;
import static io.gupshup.mdb.constants.APIConstants.USER;
import static io.gupshup.mdb.constants.APIConstants.USERID;
import static io.gupshup.mdb.constants.APIConstants.USER_ID;
import static io.gupshup.mdb.constants.APIConstants.VERSION;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PATCH;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping(value = API + VERSION + USER + USER_ID, headers = {TOKEN})
public class CampaignRestController {

	@Autowired
	private CampaignService campaignService;

	@Autowired
	private AuthenticationService authenticationService;

	@RequestMapping(value = CAMPAIGN, method = POST)
	public ResponseEntity<Campaign> saveCampaign(@PathVariable(USERID) String userId,
	                                             @RequestBody SaveCampaignRequest saveCampaignRequest) {
		return new ResponseEntity<>(campaignService.saveCampaignInDraft(userId, saveCampaignRequest), CREATED);
	}

	@RequestMapping(value = CAMPAIGNS, method = GET)
	public ResponseEntity<List<Campaign>> getAllCampaigns(@PathVariable(USERID) String userId) {
		return new ResponseEntity<>(campaignService.getAllCampaigns(userId), OK);
	}

	@RequestMapping(value = CAMPAIGN + CAMPAIGN_ID, method = GET)
	public ResponseEntity<Campaign> getCampaign(@PathVariable(USERID) String userId,
	                                            @PathVariable(CAMPAIGNID) String campaignId) {
		return new ResponseEntity<>(campaignService.getCampaign(userId, campaignId), OK);
	}

	@RequestMapping(value = CAMPAIGN + CAMPAIGN_ID + PUBLISH, method = POST)
	public ResponseEntity<Campaign> publishCampaign(@PathVariable(USERID) String userId,
	                                                @PathVariable(CAMPAIGNID) String campaignId) {
		return new ResponseEntity<>(campaignService.publishCampaign(userId, campaignId), OK);
	}

	@RequestMapping(value = CAMPAIGN + PUBLISH, method = POST)
	public ResponseEntity<Campaign> publishAndSaveCampaign(@PathVariable(USERID) String userId,
	                                                       @RequestBody SaveCampaignRequest saveCampaignRequest) {
		return new ResponseEntity<>(campaignService.publishAndSaveCampaign(userId, saveCampaignRequest), CREATED);
	}

	@RequestMapping(value = CAMPAIGN + CAMPAIGN_ID, method = PATCH)
	public ResponseEntity<Campaign> updateCampaign(@PathVariable(USERID) String userId,
	                                               @PathVariable(CAMPAIGNID) String campaignId,
	                                               @RequestBody SaveCampaignRequest saveCampaignRequest) {
		return new ResponseEntity<>(campaignService.updateCampaign(userId, campaignId, saveCampaignRequest), OK);
	}

	@RequestMapping(value = CAMPAIGN + CAMPAIGN_ID, method = DELETE)
	public ResponseEntity<Void> deleteCampaign(@PathVariable(USERID) String userId,
	                                           @PathVariable(CAMPAIGNID) String campaignId) {
		campaignService.deleteCampaign(userId, campaignId);
		return ResponseEntity.ok().build();
	}

	@RequestMapping(value = CAMPAIGN + CAMPAIGN_ID + REPORT, method = GET)
	public ResponseEntity<List<CampaignReport>> getCampaignReport(@PathVariable(USERID) String userId,
	                                                        @PathVariable(CAMPAIGNID) String campaignId) {
		return new ResponseEntity<>(campaignService.getCampaignReport(userId, campaignId), OK);
	}

}
