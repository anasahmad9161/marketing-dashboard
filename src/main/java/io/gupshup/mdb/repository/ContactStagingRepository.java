package io.gupshup.mdb.repository;

import io.gupshup.mdb.entities.ContactStaging;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * JPA Repository to perform DB operations for Contact Staging Entity
 *
 * @author deepanshu
 */
@Repository
public interface ContactStagingRepository extends JpaRepository<ContactStaging, String> {

	String UPDATE_CONTACT_STAGING = "update contact_staging set duplicate = true where exists " +
			"(select * from contact c where c.user_id = contact_staging.user_id and c.phone_number=contact_staging" +
			".phone_number)";

	/**
	 * Custom SQL query for updating contact staging table with duplicate set to true if those contacts are already
	 * stored for a user
	 *
	 * SQL = {@value ContactStagingRepository#UPDATE_CONTACT_STAGING}
	 */
	@Transactional
	@Modifying
	@Query(value = UPDATE_CONTACT_STAGING, nativeQuery = true)
	void updateContactStaging();

	/**
	 * To fetch a list of ContactStaging entities based on duplicate
	 * @param duplicate True, if Contact is already stored
	 * @return List of ContactStaging entities
	 */
	List<ContactStaging> findAllByDuplicate(Boolean duplicate);
}
