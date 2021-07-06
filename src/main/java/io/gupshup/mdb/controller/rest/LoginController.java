package io.gupshup.mdb.controller.rest;

import io.gupshup.mdb.auth.AuthenticationEntity;
import io.gupshup.mdb.auth.AuthenticationService;
import io.gupshup.mdb.controller.websockets.handlers.LoginSocketHandler;
import io.gupshup.mdb.controller.websockets.entities.LoginQRConnection;
import io.gupshup.mdb.controller.websockets.repository.LoginQRConnectionRepository;
import io.gupshup.mdb.exceptions.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

import static io.gupshup.mdb.constants.APIConstants.API;
import static io.gupshup.mdb.constants.APIConstants.LOGOUT;
import static io.gupshup.mdb.constants.APIConstants.REGISTER;
import static io.gupshup.mdb.constants.APIConstants.TOKEN;
import static io.gupshup.mdb.constants.APIConstants.VERSION;

@RestController
@RequestMapping(value = API + VERSION)
public class LoginController {

	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

	@Autowired
	private AuthenticationService authenticationService;

	@Autowired
	private LoginQRConnectionRepository loginQRConnectionRepository;

	@Autowired
	private LoginSocketHandler loginSocketHandler;

	@RequestMapping(value = REGISTER, method = RequestMethod.POST)
	public ResponseEntity<String> generateToken(@RequestParam(name = "qrToken") String qrToken,
	                                            @RequestParam(name = "apiKey") String apiKey) {
		logger.info("Registering QR Token : {}  with ApiKey : {}", qrToken, apiKey);
		LoginQRConnection connection = loginQRConnectionRepository.findByQrToken(qrToken);
		if (connection == null) {
			logger.info("QR Code is not registered, throwing the exception");
			throw new AuthenticationException("Invalid QR Code.");
		}
		AuthenticationEntity entity = authenticationService.register(qrToken, apiKey);
		logger.info("QR Token Registered Successfully. Sending the auth token back via websocket");
		loginSocketHandler.sendAuthToken(connection.getSessionId(), entity.getAuthToken());
		logger.info("Auth Token Sent for QR Token : {} and API key : {}", qrToken, apiKey);
		return ResponseEntity.ok().build();
	}

	@RequestMapping(value = LOGOUT, method = RequestMethod.GET)
	public ResponseEntity<Void> logout(@RequestParam(name = TOKEN) String authToken) {
		logger.info("Deactivating the Auth Token : {} ", authToken);
		authenticationService.invalidateToken(authToken);
		logger.info("Auth token deactivated at {} ", LocalDateTime.now());
		return ResponseEntity.ok().build();
	}

}
