package io.gupshup.mdb.auth;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * POJO for Authentication Entity (Token)
 *
 * @author deepanshu
 */
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "token")
@EntityListeners(AuditingEntityListener.class)
public class AuthenticationEntity {

	/**
	 * Entity ID
	 */
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String id;
	/**
	 * QR Token (Mandatory)
	 */
	@NonNull
	private String qrToken;

	/**
	 * Auth Token (Mandatory)
	 */
	@NonNull
	private String authToken;

	/**
	 * If Token is Expired (Mandatory)
	 */
	@NonNull
	private boolean isExpired;

	/**
	 * API Key from Mobile App (Mandatory)
	 */
	@NonNull
	private String apiKey;

	/**
	 * Creation Date
	 */
	@CreatedDate
	private LocalDateTime createdDate;

	/**
	 * Expiry Date of Token
	 */
	private LocalDateTime expiredDate;

	@NonNull
	private String primaryPhoneNumber;
	private String secondaryPhoneNumber;

}
