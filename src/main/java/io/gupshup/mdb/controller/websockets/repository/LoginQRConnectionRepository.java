package io.gupshup.mdb.controller.websockets.repository;

import io.gupshup.mdb.controller.websockets.entities.LoginQRConnection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA Repository to perform DB operations for LoginQRConnection Entity
 *
 * @author deepanshu
 */
@Repository
public interface LoginQRConnectionRepository extends JpaRepository<LoginQRConnection, String> {

	/**
	 * Find LoginQRConnection given session ID
	 * @param sessionId session ID
	 * @return LoginQRConnection
	 */
	LoginQRConnection findBySessionId(String sessionId);

	/**
	 * Find LoginQRConnection given QR Token
	 * @param qrToken QR Token
	 * @return LoginQRConnection
	 */
	LoginQRConnection findByQrToken(String qrToken);

}
