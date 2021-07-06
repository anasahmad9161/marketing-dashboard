package io.gupshup.mdb.controller.websockets.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * POJO for WhatsappQRConnection
 *
 * @author deepanshu
 */
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "whatsapp_qr_connection")
public class WhatsappQRConnection {

	/**
	 * WhatsappQRConnection ID
	 */
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String id;

	/**
	 * Session ID (Mandatory)
	 */
	@NonNull
	private String sessionId;

	/**
	 * For which user you want to establish the connection (mandatory)
	 */
	@NonNull
	private String userId;

	private String whatsappNumber;
	private String whatsappSessionId;
	private String whatsappClientId;
	private String whatsappServerId;
	private boolean connected = false;
}
