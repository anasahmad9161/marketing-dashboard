package io.gupshup.mdb.controller.websockets.handlers;

import io.gupshup.mdb.controller.websockets.entities.WhatsappQRConnection;
import io.gupshup.mdb.controller.websockets.repository.WhatsappQRConnectionRepository;
import io.gupshup.mdb.exceptions.AuthenticationException;
import io.gupshup.mdb.exceptions.CustomRuntimeException;
import io.gupshup.mdb.service.WhatsappCampaignService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

@Lazy
@Component
public class WhatsappSocketHandler extends TextWebSocketHandler {

	private static final Logger logger = LoggerFactory.getLogger(WhatsappSocketHandler.class);
	List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

	@Autowired
	private WhatsappQRConnectionRepository qrConnectionRepository;

	@Autowired
	private WhatsappCampaignService whatsappCampaignService;

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		sessions.add(session);
		super.afterConnectionEstablished(session);
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
		logger.info("Received Whatsapp Connection Request from {} with user : {}", session.getId(), message.getPayload());
		synchronized (this) {
			Optional<WhatsappQRConnection> connection = qrConnectionRepository.findByUserId(message.getPayload());
			if (connection.isPresent()) {
				logger.info("Updating Session Info for Phone Number : {}", message.getPayload());
				WhatsappQRConnection oldConnection = connection.get();
				oldConnection.setSessionId(session.getId());
				oldConnection.setConnected(false);
				qrConnectionRepository.save(oldConnection);
			} else {
				logger.info("Saving new Whatsapp QR Connection for user : {}", message.getPayload());
				qrConnectionRepository.save(new WhatsappQRConnection(session.getId(), message.getPayload()));
			}
		}
		try {
			whatsappCampaignService.sendPNToken(message.getPayload());
		} catch (CustomRuntimeException e) {
			if(session.isOpen()){
				Map<String, String> errorMap = Collections.singletonMap("error", e.getLocalizedMessage());
				session.sendMessage(new TextMessage(new JSONObject(errorMap).toString()));
			}
		}
	}

	public void sendMessage(String sessionId, String message) {
		sessions.forEach(web -> {
			if (web.getId().equals(sessionId)) {
				logger.info("Found matching session for sessionId : {}", sessionId);
				try {
					web.sendMessage(new TextMessage(message));
				} catch (IOException e) {
					throw new AuthenticationException("Failed to send message");
				}
			}
		});
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		sessions.remove(session);
		super.afterConnectionClosed(session, status);
	}
}
