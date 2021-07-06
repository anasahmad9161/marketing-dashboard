package io.gupshup.mdb.repository;

import io.gupshup.mdb.entities.CampaignEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA Repository to perform DB operations for Campaign Entity
 *
 * @author deepanshu
 */
@Repository
public interface CampaignRepository extends JpaRepository<CampaignEntity, String> {

	/**
	 * To return list of Campaign Entities belonging to a particular user
	 * @param userId User Id
	 * @return List of CampaignEntity
	 */
	List<CampaignEntity> findAllByUserId(String userId);

	/**
	 * Find a CampaignEntity with given User ID and Name
	 * @param name Name of Campaign e.g - Campaign 1
	 * @param userId User ID
	 * @return Optional of CampaignEntity (Empty if no such entity found)
	 */
	Optional<CampaignEntity> findByNameAndUserId(String name, String userId);

	/**
	 * Find a CampaignEntity with given Campaign ID and User ID
	 * @param id Campaign ID
	 * @param userId User ID
	 * @return Optional of CampaignEntity (Empty if no such entity found)
	 */
	Optional<CampaignEntity> findByCampaignIdAndUserId(String id, String userId);
}
