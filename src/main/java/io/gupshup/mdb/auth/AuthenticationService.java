package io.gupshup.mdb.auth;

/**
 * Authentication Service Interface.
 *
 * @author deepanshu
 */
public interface AuthenticationService {

	/**
	 * Registers a user against a QR token and an API key. Fetches the details associated with API key and create
	 * users and essential data
	 * <br>
	 * Validations -
	 * <ul>QR token should not be in use</ul>
	 * <ul>There must be at least phone number associated to this API key</ul>
	 * @param qrToken QR Token
	 * @param apiKey API Key
	 * @return Authentication Entity
	 */
	AuthenticationEntity register(String qrToken, String apiKey);

	/**
	 * Validates the auth token for a user, this is invoked for almost all the API calls
	 * <br>
	 * Validations -
	 * <ul>User ID must be associated with this auth token</ul>
	 * @param userId User ID
	 * @param authToken Auth Token
	 * @return validity of token (True/False)
	 */
	boolean validateAuthToken(String userId, String authToken);

	/**
	 * Deactivates the auth token
	 * <br>
	 * Validations -
	 * <ul>Token must be present in DB</ul>
	 * @param authToken Auth Token
	 */
	void invalidateToken(String authToken);

	/**
	 * Get Authentication Entity given auth token
	 * <br>
	 * Validations -
	 * <ul>Token must be present in DB</ul>
	 * @param authToken Auth Token
	 * @return Authentication Entity
	 */
	AuthenticationEntity getAuthEntity(String authToken);
}
