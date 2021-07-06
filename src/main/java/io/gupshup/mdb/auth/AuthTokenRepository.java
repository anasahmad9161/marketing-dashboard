package io.gupshup.mdb.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA Repository to perform DB operations for Authentication Entity
 *
 * @author deepanshu
 */
@Repository
public interface AuthTokenRepository extends JpaRepository<AuthenticationEntity, String> {

	/**
	 * To get a list of authentication entities given QR Token
	 * @param qrToken QR Token
	 * @return List of Authentication Entity
	 */
	List<AuthenticationEntity> findByQrToken(String qrToken);

	/**
	 * Return auth entity based on auth token
	 * @param authToken Authentication Entity
	 * @return Optional of Authentication Entity
	 */
	Optional<AuthenticationEntity> findByAuthToken(String authToken);
}
