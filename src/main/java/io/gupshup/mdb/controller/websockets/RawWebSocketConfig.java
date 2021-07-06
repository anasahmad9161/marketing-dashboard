package io.gupshup.mdb.controller.websockets;

import io.gupshup.mdb.controller.websockets.handlers.LoginSocketHandler;
import io.gupshup.mdb.controller.websockets.handlers.WhatsappSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import static io.gupshup.mdb.constants.APIConstants.SOCKET;
import static io.gupshup.mdb.constants.APIConstants.WHATSAPP;

@Configuration
@EnableWebSocket
public class RawWebSocketConfig implements WebSocketConfigurer {

	@Autowired
	private LoginSocketHandler loginSocketHandler;

	@Autowired
	private WhatsappSocketHandler whatsappSocketHandler;

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
		webSocketHandlerRegistry.addHandler(loginSocketHandler, SOCKET)
		                        .addHandler(whatsappSocketHandler, WHATSAPP + SOCKET).setAllowedOrigins("*");
	}
}
