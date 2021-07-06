package io.gupshup.mdb.controller.rest;

import io.gupshup.mdb.controller.websockets.entities.WhatsappQRConnection;
import io.gupshup.mdb.controller.websockets.handlers.WhatsappSocketHandler;
import io.gupshup.mdb.controller.websockets.repository.WhatsappQRConnectionRepository;
import io.gupshup.mdb.dto.campaign.WhatsappConnectionDetails;
import io.gupshup.mdb.dto.campaign.WhatsappConnectionStatus;
import io.gupshup.mdb.dto.campaign.WhatsappQR;
import io.gupshup.mdb.exceptions.AuthenticationException;
import io.gupshup.mdb.exceptions.InvalidRequestException;
import io.gupshup.mdb.service.WhatsappCampaignService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static io.gupshup.mdb.constants.APIConstants.API;
import static io.gupshup.mdb.constants.APIConstants.STATUS;
import static io.gupshup.mdb.constants.APIConstants.USERID;
import static io.gupshup.mdb.constants.APIConstants.USER_ID;
import static io.gupshup.mdb.constants.APIConstants.VERSION;
import static io.gupshup.mdb.constants.APIConstants.WHATSAPP;

@RestController
@RequestMapping(value = API + VERSION + WHATSAPP)
public class WhatsappRegistrationController {

	private static final Logger logger = LoggerFactory.getLogger(WhatsappRegistrationController.class);
	private static final String WHATSAPP_NOT_FOUND = "Whatsapp Not Connected";
	private static final String NO_PHONE = "No request found for Phone";
	private static final List<String> noPhoneError = Collections.singletonList("No request found for Phone");

	@Autowired
	private WhatsappSocketHandler whatsappSocketHandler;

	@Autowired
	private WhatsappQRConnectionRepository qrConnectionRepository;

	@Autowired
	private WhatsappCampaignService whatsappCampaignService;

	@RequestMapping(value = "/image", method = RequestMethod.POST)
	public ResponseEntity<Void> sendQRImageForRegistration(@RequestBody @Valid WhatsappQR whatsappQR) {
		logger.info("Sending QR Image for Whatsapp Registration for phone : {}", whatsappQR.getUserId());
		WhatsappQRConnection connection = qrConnectionRepository.findByUserId(whatsappQR.getUserId())
		                                                        .orElseThrow(() -> {
			                                                        logger.info(NO_PHONE);
			                                                        throw new InvalidRequestException(noPhoneError);
		                                                        });
		Map<String, String> jsonMap = Collections.singletonMap("qrToken", whatsappQR.getImage());
		whatsappSocketHandler.sendMessage(connection.getSessionId(), new JSONObject(jsonMap).toString());
		logger.info("Message Sent Successfully : {}", new JSONObject(jsonMap));
		return ResponseEntity.ok().build();
	}

	@RequestMapping(value = STATUS, method = RequestMethod.POST)
	public ResponseEntity<Void> updateConnectionDetails(@RequestBody @Valid WhatsappConnectionDetails details) {
		logger.info("Received Status for Whatsapp Registration for user : {}", details);
		WhatsappQRConnection connection = qrConnectionRepository.findByUserId(details.getUserId())
		                                                        .orElseThrow(() -> {
			                                                        logger.info(NO_PHONE);
			                                                        throw new AuthenticationException(NO_PHONE);
		                                                        });
		logger.info("Updating Whatsapp QR Connection details");
		connection.setWhatsappClientId(details.getClientId());
		connection.setWhatsappNumber(details.getConnectedPhone());
		connection.setWhatsappServerId(details.getServerId());
		connection.setWhatsappSessionId(details.getSessionId());
		connection.setConnected(true);
		qrConnectionRepository.save(connection);
		logger.info("Sending Details to Dashboard");
		Map<String, String> jsonMap = Collections.singletonMap("status", "connected");
		whatsappSocketHandler.sendMessage(connection.getSessionId(), new JSONObject(jsonMap).toString());
		logger.info("Message Sent Successfully : {}", new JSONObject(jsonMap));
		return ResponseEntity.ok().build();
	}

	@RequestMapping(value = USER_ID + STATUS, method = RequestMethod.GET)
	public ResponseEntity<WhatsappConnectionStatus> getWhatsappConnectionStatus(@PathVariable(USERID) String userId) {
		logger.info("Request for getting whatsapp connection status for user {}", userId);
		Optional<WhatsappQRConnection> qrConnection = qrConnectionRepository.findByUserId(userId);
		if (qrConnection.isEmpty() || !qrConnection.get().isConnected()) {
			logger.info("No Connection Found or Whatsapp is Disconnected");
			WhatsappConnectionStatus status = WhatsappConnectionStatus.builder().whatsappNumber("").connected(false)
			                                                          .error(WHATSAPP_NOT_FOUND).build();
			return new ResponseEntity<>(status, HttpStatus.OK);
		}
		logger.info("Whatsapp Connection Status : {}", qrConnection.get().isConnected());
		WhatsappConnectionStatus status = WhatsappConnectionStatus.builder().whatsappNumber(
				qrConnection.get().getWhatsappNumber()).connected(true).build();
		return new ResponseEntity<>(status, HttpStatus.OK);
	}

	@RequestMapping(value = USER_ID + STATUS, method = RequestMethod.PATCH)
	public ResponseEntity<Void> disconnectWhatsappForUser(@PathVariable(USERID) String userId){
		logger.info("Request for disconnecting whatsapp for user {}", userId);
		Optional<WhatsappQRConnection> qrConnection = qrConnectionRepository.findByUserId(userId);
		if(qrConnection.isEmpty() || !qrConnection.get().isConnected()){
			logger.info("Connection Request not found or already disconnected");
			throw new InvalidRequestException(Collections.singletonList(WHATSAPP_NOT_FOUND));
		}
		qrConnection.get().setConnected(false);
		qrConnectionRepository.save(qrConnection.get());
		logger.info("QR Connection Updated Successfully");
		return ResponseEntity.ok().build();
	}
}
