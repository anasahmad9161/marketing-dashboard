package io.gupshup.mdb.repository;

import io.gupshup.mdb.entities.ListEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * JPA Repository to perform DB operations for List Entity
 *
 * @author deepanshu
 */
@Repository
public interface ListsRepository extends JpaRepository<ListEntity, String> {

	/**
	 * Find a List Entity given Name, User ID and IsActive
	 * @param name Name of the List
	 * @param userId User ID
	 * @param isActive True, if list is not deleted
	 * @return Optional of ListEntity (Empty if no such entity found)
	 */
	@Transactional
	Optional<ListEntity> findByNameAndUserIdAndIsActive(String name, String userId, boolean isActive);

	/**
	 * Find a List Entity given List ID and User ID
	 * @param id List ID
	 * @param userId User ID
	 * @return Optional of ListEntity (Empty if no such entity found)
	 */
	Optional<ListEntity> findByListIdAndUserId(String id, String userId);

	/**
	 * To fetch a list of List Entity on the basis of User ID and IsActive in descending order of Creation Date
	 * @param userId User ID
	 * @param isActive True, if list is not deleted
	 * @return List of List Entities
	 */
	List<ListEntity> findAllByUserIdAndIsActiveOrderByCreationDateDesc(String userId, boolean isActive);
}
