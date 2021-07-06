package io.gupshup.mdb.service;

import io.gupshup.mdb.entities.ListEntity;

import java.util.List;

/**
 * Interface for Lists Service. This interface contains methods to be consumed by ListRestController for the APIs
 *
 * @author deepanshu
 */
public interface ListsService {

	/**
	 * Save a new List for a user
	 * <br>
	 * Validations -
	 * <ul>Name and User ID cannot be blank</ul>
	 * <ul>Name should not be duplicate for the user</ul>
	 *
	 * @param name   Name of the list
	 * @param userId User ID
	 * @return List Entity
	 */
	ListEntity saveList(String name, String userId);

	/**
	 * Get All active lists for a user in descending order of creation date
	 * <br>
	 * Validations -
	 * <ul>User ID cannot be blank</ul>
	 *
	 * @param userId User ID
	 * @return List of ListEntity
	 */
	List<ListEntity> getAllLists(String userId);

	/**
	 * Get a List Entity for a user given list ID
	 * <br>
	 * Validations -
	 * <ul>User ID and List ID cannot be blank</ul>
	 * <ul>List must be active</ul>
	 *
	 * @param userId User ID
	 * @param listId List ID
	 * @return List Entity
	 */
	ListEntity getList(String userId, String listId);

	/**
	 * Update a list name and return updated list entity for a user given list Id and new name. It also updates the
	 * last updated date.
	 * <br>
	 * Validations -
	 * <ul>User ID and List ID cannot be blank</ul>
	 * <ul>Updated Name should not be preexisting</ul>
	 * <ul>List should not be "All Contacts"</ul>
	 *
	 * @param listId      List ID
	 * @param updatedName New Name
	 * @param userId      User ID
	 * @return Updated List Entity
	 */
	ListEntity updateName(String listId, String updatedName, String userId);

	/**
	 * Add Contacts to a list given listID, contact IDs to be added, and user ID. It also updates the
	 * last updated date.
	 * <br>
	 * Validations -
	 * <ul>User ID and List ID cannot be blank</ul>
	 * <ul>List must be active</ul>
	 * <ul>List of Contact IDs should not be empty</ul>
	 * <ul>List should not be "All Contacts"</ul>
	 *
	 * @param userId     User ID
	 * @param listId     List ID
	 * @param contactIds List of Contact IDs
	 * @return Updated List Entity
	 */
	ListEntity addContactsToList(String userId, String listId, List<String> contactIds);

	/**
	 * Remove Contacts to a list given listID, contact IDs to be removed, and user ID. It also updates the
	 * last updated date.
	 * <br>
	 * Validations -
	 * <ul>User ID and List ID cannot be blank</ul>
	 * <ul>List must be active</ul>
	 * <ul>List of Contact IDs should not be empty</ul>
	 * <ul>List should not be "All Contacts"</ul>
	 *
	 * @param userId     User ID
	 * @param listId     List ID
	 * @param contactIds List of Contact IDs
	 * @return Updated List Entity
	 */
	ListEntity removeContactsFromList(String userId, String listId, List<String> contactIds);

	/**
	 * It deactivates the List Entity (We do not delete Lists from Database). It also disassociate the list with the
	 * contact entities.
	 * <br>
	 * Validations -
	 * <ul>User ID and List ID cannot be blank</ul>
	 * <ul>List should not be "All Contacts"</ul>
	 * @param userId User ID
	 * @param listId List ID
	 */
	void deleteList(String userId, String listId);
}
