package io.gupshup.mdb.repository;

import io.gupshup.mdb.entities.MessageStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * JPA Repository to perform DB operations for Message Status Entity
 *
 * @author deepanshu
 */
@Repository
public interface MessageStatusRepository extends JpaRepository<MessageStatusEntity, String> {

	/**
	 * Return list of MessageStatusEntity for a campaign using Campaign ID
	 * @param campaignId Campaign ID
	 * @return List of MessageStatusEntity
	 */
	List<MessageStatusEntity> findAllByCampaignId(String campaignId);

	/**
	 * Find MessageStatusEntity with given Campaign ID and Phone Number (Recipient)
	 * @param campaignId Campaign ID
	 * @param phone Phone Number of Recipient
	 * @return Optional of MessageStatusEntity (Empty if no such entity found)
	 */
	@Transactional
	Optional<MessageStatusEntity> findByCampaignIdAndPhone(String campaignId, String phone);
}
