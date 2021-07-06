package io.gupshup.mdb.controller.websockets.repository;

import io.gupshup.mdb.controller.websockets.entities.WhatsappQRConnection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

/**
 * JPA Repository to perform DB operations for WhatsappQRConnection Entity
 *
 * @author deepanshu
 */
@Repository
public interface WhatsappQRConnectionRepository extends JpaRepository<WhatsappQRConnection, String> {


	/**
	 * Find WhatsappQRConnection given Session ID
	 * @param sessionId session ID
	 * @return Optional of WhatsappQRConnection
	 */
	@Transactional
	Optional<WhatsappQRConnection> findBySessionId(String sessionId);

	/**
	 * Find WhatsappQRConnection given User ID
	 * @param userId User ID
	 * @return Optional of WhatsappQRConnection
	 */
	@Transactional
	Optional<WhatsappQRConnection> findByUserId(String userId);
}
