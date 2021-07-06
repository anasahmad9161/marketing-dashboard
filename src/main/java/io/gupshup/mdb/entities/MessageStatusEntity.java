package io.gupshup.mdb.entities;

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
 * POJO for Message Status Entity (Message details being sent by Mobile APP)
 *
 * @author deepanshu
 */
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "message_status")
public class MessageStatusEntity {

	/**
	 * Message Status ID
	 */
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String id;

	/**
	 * Campaign ID (Mandatory)
	 */
	@NonNull
	private String campaignId;

	/**
	 * Phone Number of Recipient (Mandatory)
	 */
	@NonNull
	private String phone;

	/**
	 * Status of Message (Sent, Delivered, Failed) (Mandatory)
	 */
	@NonNull
	private String status;

	/**
	 * Timestamp of Message (Mandatory)
	 */
	@NonNull
	private String timestamp;

}
