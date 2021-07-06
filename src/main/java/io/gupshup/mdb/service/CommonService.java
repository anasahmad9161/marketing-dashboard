package io.gupshup.mdb.service;

import io.gupshup.mdb.entities.ChannelEntity;

import java.util.List;

/**
 * Interface for Common Methods that can be used by other services/controllers
 *
 * @author deepanshu
 */
public interface CommonService {

	/**
	 * Utility method to create common data across users.
	 * Currently, We have "All Contacts" List as one such data
	 * @param userId User ID
	 */
	void createEssentialData(String userId);

	/**
	 * Utility method to get Supported Channels
	 * @return List of Supported channels
	 */
	List<ChannelEntity> getChannels();

	/**
	 * Get Information associated with the API Key for a user
	 * <br>
	 * Validations -
	 * <ul>API Key cannot be blank</ul>
	 * @param apiKey API Key
	 * @return List of User Phone Numbers
	 */
	List<String> getSenderPhoneNumbers(String apiKey);

}