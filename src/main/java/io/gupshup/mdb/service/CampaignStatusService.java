package io.gupshup.mdb.service;

import io.gupshup.mdb.dto.campaign.MessageStatusRequest;
import io.gupshup.mdb.dto.campaign.CampaignStatus;

/**
 * Interface for Campaign Status Service. This interface contains methods to be consumed by CommonController for the APIs
 *
 * @author deepanshu
 */
public interface CampaignStatusService {

	/**
	 * Create a new record for Campaign Message (Message Details sent by phone app) or update existing record
	 * <br>
	 * Validations -
	 * <ul>None of the fields in CampaignMessageRequest should be blank</ul>
	 * <ul>Channel should be SMS</ul>
	 * <ul>Status should be either SENT, DELIVERED or FAILED</ul>
	 * @param messageStatusRequest CampaignMessageRequest
	 */
	void createOrUpdateCampaignMessage(MessageStatusRequest messageStatusRequest);

	/**
	 * Get Campaign Status (Statistics of the published campaign) for a user given campaign ID
	 * <br>
	 * Validations -
	 * <ul>User ID and Campaign ID must not be blank</ul>
	 * @param userId User ID
	 * @param campaignId Campaign ID
	 * @return CampaignStatus DTO
	 */
	CampaignStatus getCampaignStatus(String userId, String campaignId);

}
