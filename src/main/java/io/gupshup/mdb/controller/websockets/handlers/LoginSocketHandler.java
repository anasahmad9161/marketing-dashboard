package io.gupshup.mdb.controller.websockets.handlers;

import io.gupshup.mdb.controller.websockets.entities.LoginQRConnection;
import io.gupshup.mdb.controller.websockets.repository.LoginQRConnectionRepository;
import io.gupshup.mdb.exceptions.AuthenticationException;
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
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Lazy
@Component
public class LoginSocketHandler extends TextWebSocketHandler {

	private static final Logger logger = LoggerFactory.getLogger(LoginSocketHandler.class);
	List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

	@Autowired
	private LoginQRConnectionRepository loginQRConnectionRepository;

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		sessions.add(session);
		super.afterConnectionEstablished(session);
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) {
		logger.info("Received token request for {} and QR Token : {}", session.getId(), message.getPayload());
		String sessionId = session.getId();
		LoginQRConnection connection = loginQRConnectionRepository.findBySessionId(sessionId);
		if (connection != null) throw new AuthenticationException("User already active");
		LoginQRConnection connection1 = loginQRConnectionRepository.findByQrToken(message.getPayload());
		if (connection1 != null) {
			logger.info("QR Code already Present, Updating session");
			removeSessionById(connection1.getSessionId());
			connection1.setSessionId(sessionId);
			loginQRConnectionRepository.save(connection1);
			logger.info("Session Information updated Successfully : {}", sessionId);
		} else {
			LoginQRConnection conn = loginQRConnectionRepository
					.save(new LoginQRConnection(sessionId, message.getPayload()));
			logger.info("Session Information stored Successfully : {}", conn.getSessionId());
		}
	}

	public void sendAuthToken(String sessionId, String authToken) {
		sessions.forEach(web -> {
			if (web.getId().equals(sessionId)) {
				logger.info("Found matching session for sessionId : {}", sessionId);
				try {
					web.sendMessage(new TextMessage(authToken));
				} catch (IOException e) {
					throw new AuthenticationException("Failed to send token");
				}
			}
		});
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		sessions.remove(session);
		super.afterConnectionClosed(session, status);
	}

	private void removeSessionById(String sessionId) {
		sessions.forEach(webSocketSession -> {
			if (webSocketSession.getId().equals(sessionId)) {
				sessions.remove(webSocketSession);
			}
		});
	}
}
