package io.gupshup.mdb.repository;

import io.gupshup.mdb.entities.ChannelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * JPA Repository to perform DB operations for Channel Entity
 *
 * @author deepanshu
 */
@Repository
public interface ChannelRepository extends JpaRepository<ChannelEntity, String> {

	/**
	 * Find a channel by name
	 * @param channelName Name of the channel
	 * @return Optional of ChannelEntity
	 */
	Optional<ChannelEntity> findByChannelName(String channelName);
}
