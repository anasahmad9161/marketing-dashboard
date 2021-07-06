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
 * POJO for LoginQRConnection
 *
 * @author deepanshu
 */
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "connection")
public class LoginQRConnection {

	/**
	 * WebsocketConnection ID
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
	 * QR Token (Mandatory)
	 */
	@NonNull
	private String qrToken;

}
