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
 * POJO for Message Entity
 *
 * @author deepanshu
 */
@Getter
@Setter
@Entity
@Table(name = "message")
@RequiredArgsConstructor
@NoArgsConstructor
public class MessageEntity {

	/**
	 * Message ID
	 */
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String messageId;

	/**
	 * Message Content (Mandatory)
	 */
	@NonNull
	private String message;
}
