package io.gupshup.mdb.service;

import io.gupshup.mdb.dto.campaign.Campaign;
import io.gupshup.mdb.dto.campaign.CampaignReport;
import io.gupshup.mdb.dto.campaign.ReplySaveRequest;
import io.gupshup.mdb.dto.campaign.SaveCampaignRequest;

import java.util.List;

/**
 * Interface for Campaign Service. This interface contains methods to be consumed by CampaignRestController for the APIs
 *
 * @author deepanshu
 */
public interface CampaignService {

	/**
	 * Save Campaign in DRAFT Status for a user given User ID and Campaign Parameters.
	 * <br>
	 * Validations -
	 * <ul>User ID must not be blank</ul>
	 * <ul>None of the fields in SaveCampaignRequest should be blank</ul>
	 * <ul>List must pe present and should be active</ul>
	 * <ul>Campaign Name cannot be duplicate</ul>
	 *
	 * @param userId User ID
	 * @param request SaveCampaignRequest containing parameters for campaign
	 * @return Campaign DTO
	 */
	Campaign saveCampaignInDraft(String userId, SaveCampaignRequest request);

	/**
	 * Get Campaign Details for a user given campaign ID.
	 * <br>
	 * Validations -
	 * <ul>User ID and Campaign ID must not be blank</ul>
	 * @param userId User ID
	 * @param campaignId Campaign ID
	 * @return Campaign DTO
	 */
	Campaign getCampaign(String userId, String campaignId);

	/**
	 * Get List of all the campaigns for a user
	 * <br>
	 * Validations -
	 * <ul>User ID cannot be blank</ul>
	 * @param userId User ID
	 * @return List of Campaign DTO
	 */
	List<Campaign> getAllCampaigns(String userId);

	/**
	 * Publish a Campaign saved in DRAFT status and update it's status, published date and published list size.
	 * This method uses SOIP APIs for sending out messages to contacts.
	 * <br>
	 * Validations -
	 * <ul>User ID and Campaign ID must not be blank</ul>
	 * <ul>Campaign Must be in DRAFT Status</ul>
	 * <ul>List associated with the campaign cannot be an empty or inactive</ul>
	 * @param userId User ID
	 * @param campaignId Campaign ID
	 * @return Campaign DTO
	 */
	Campaign publishCampaign(String userId, String campaignId);

	/**
	 * Save and publish a new campaign for a user, Use SOIP APIs to send messages
	 * <br>
	 * Validations -
	 * <ul>User ID must not be blank</ul>
	 * <ul>None of the fields in SaveCampaignRequest should be blank</ul>
	 * <ul>List must pe present and should be active</ul>
	 * <ul>Campaign Name cannot be duplicate</ul>
	 * <ul>List associated with the campaign cannot be an empty or inactive</ul>
	 * @param userId User ID
	 * @param request SaveCampaignRequest containing campaign params
	 * @return Campaign DTO
	 */
	Campaign publishAndSaveCampaign(String userId, SaveCampaignRequest request);

	/**
	 * Update already saved campaign details for a user given campaign ID and parameters to update
	 * <br>
	 * Validations -
	 * <ul>User ID and Campaign ID must not be blank</ul>
	 * <ul>Campaign Must be in DRAFT Status</ul>
	 * <ul>None of the fields in SaveCampaignRequest should be blank</ul>
	 * <ul>New Campaign Name cannot be duplicate</ul>
	 * @param userId User ID
	 * @param campaignId Campaign ID
	 * @param request SaveCampaignRequest containing campaign params
	 * @return Campaign DTO
	 */
	Campaign updateCampaign(String userId, String campaignId, SaveCampaignRequest request);

	/**
	 * Deletes a campaign from database for a user given campaign ID
	 * <br>
	 * Validations -
	 * <ul>User ID and Campaign ID must not be blank</ul>
	 * <ul>Campaign should not be in PUBLISHED Status</ul>
	 * @param userId User ID
	 * @param campaignId Campaign ID
	 */
	void deleteCampaign(String userId, String campaignId);

	/**
	 * Get Campaign Report comprising details about campaign message sent to contact list
	 * @param userId User ID
	 * @param campaignId Campaign ID
	 * @return List of CampaignReport DTO
	 */
	List<CampaignReport> getCampaignReport(String userId, String campaignId);

	/**
	 * Save Replies received for a campaign
	 * @param replySaveRequest Reply Request
	 */
	void saveReplies(ReplySaveRequest replySaveRequest);
}
