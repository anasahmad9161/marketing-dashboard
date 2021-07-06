package io.gupshup.mdb.service;

import io.gupshup.mdb.dto.contact.UploadContactsResponse;
import io.gupshup.mdb.entities.ContactEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Interface for Contacts Service. This interface contains methods to be consumed by ContactRestController for the APIs
 *
 * @author deepanshu
 */
public interface ContactsService {

	/**
	 * This method is used to store the contacts uploaded as CSV bu user.
	 * It also updates the "All Contacts" list's last updated time
	 * <br>
	 * Validations -
	 * <ul>User ID cannot be blank</ul>
	 * <ul>File should be of type CSV</ul>
	 * <ul>Contents of file should be in correct format</ul>
	 * @param userId User ID
	 * @param file CSV File
	 * @return Response after processing the file
	 */
	UploadContactsResponse uploadContacts(String userId, MultipartFile file);

	/**
	 * Get List of Contact Entities for a user
	 * <br>
	 * Validations -
	 * <ul>User ID cannot be blank</ul>
	 * @param userId User ID
	 * @return List of Contact Entities
	 */
	List<ContactEntity> getAllContacts(String userId);

	/**
	 * Get Contact Entity given user ID and contact ID
	 * <br>
	 * Validations -
	 * <ul>User ID and Contact ID cannot be blank</ul>
	 * @param userId User ID
	 * @param contactId Contact ID
	 * @return Contact Entity
	 */
	ContactEntity getContact(String userId, String contactId);

	/**
	 * Delete a List of Contacts for a user given contact IDs. It also removes these contacts from their lists.
	 * <br>
	 * Validations -
	 * <ul>User ID cannot be blank</ul>
	 * <ul>List of Contact IDs cannot be empty</ul>
	 * @param userId User ID
	 * @param contactIds List of Contact IDs
	 */
	void deleteContacts(String userId, List<String> contactIds);

}
