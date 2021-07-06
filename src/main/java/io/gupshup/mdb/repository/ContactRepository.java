package io.gupshup.mdb.repository;

import io.gupshup.mdb.entities.ContactEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * JPA Repository to perform DB operations for Contact Entity
 *
 * @author deepanshu
 */
@Repository
public interface ContactRepository extends JpaRepository<ContactEntity, String> {

	String INSERT_NON_DUPLICATE = "insert into contact (id,user_id, phone_number,name,nickname,salutation) " +
			"select uuid(),user_id,phone_number,name,nickname,salutation from contact_staging where duplicate = false";

	/**
	 * To fetch a list of contact entities belonging to a user
	 * @param userId User ID
	 * @return List of ContactEntity
	 */
	List<ContactEntity> findAllByUserId(String userId);

	/**
	 * Return a Contact Entity given User ID and Phone Number (Contact's Phone Number)
	 * @param userId User ID
	 * @param phoneNumber Phone Number
	 * @return ContactEntity
	 */
	ContactEntity findByUserIdAndPhoneNumber(String userId, String phoneNumber);

	/**
	 * Custom SQL Query to insert unique contacts for a user
	 *
	 * SQL = {@value ContactRepository#INSERT_NON_DUPLICATE}
	 */
	@Transactional
	@Modifying
	@Query(value = INSERT_NON_DUPLICATE, nativeQuery = true)
	void saveAllNonDuplicates();

	/**
	 * To fetch a list of contact entities with given User ID and Set of Phone Numbers
	 * @param userID User ID
	 * @param phoneNumbers Set of Phone Numbers
	 * @return List of ContactEntity
	 */
	List<ContactEntity> findByUserIdAndPhoneNumberIn(String userID, Set<String> phoneNumbers);

	/**
	 * To fetch a Contact Entity given User ID and Entity ID
	 * @param id Entity ID
	 * @param userId User ID
	 * @return Optional of ContactEntity (Empty if no such entity found)
	 */
	Optional<ContactEntity> findByIdAndUserId(String id, String userId);
}
