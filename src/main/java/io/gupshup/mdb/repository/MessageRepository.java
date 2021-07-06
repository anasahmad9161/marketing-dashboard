package io.gupshup.mdb.repository;

import io.gupshup.mdb.entities.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA Repository to perform DB operations for Message Entity
 *
 * @author deepanshu
 */
@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, String> {
}
