package io.gupshup.mdb.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * POJO for Campaign Entity
 *
 * @author deepanshu
 */
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "campaign")
@EntityListeners(AuditingEntityListener.class)
public class CampaignEntity {

	/**
	 * Campaign ID
	 */
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String campaignId;

	/**
	 * User ID (Mandatory)
	 */
	@NonNull
	private String userId;
	/**
	 * List ID (Mandatory)
	 */
	@NonNull
	private String listId;
	/**
	 * Channel ID (Mandatory)
	 */
	@NonNull
	private String channelId;
	/**
	 * Name of the campaign (Mandatory)
	 */
	@NonNull
	private String name;
	/**
	 * Message in the campaign (Mandatory)
	 */
	@NonNull
	private String messageId;
	/**
	 * Campaign Status (DRAFT, PUBLISHED, COMPLETED) (Mandatory)
	 */
	@NonNull
	private String status;
	/**
	 * Sender Phone Number (Mandatory)
	 */
	@NonNull
	private String sender;
	/**
	 * Date of Creation
	 */
	@CreatedDate
	private LocalDateTime createdDate;
	/**
	 * Date of Publish
	 */
	private LocalDateTime publishedDate;
	/**
	 * Date of Completion
	 */
	private LocalDateTime completedDate;
	/**
	 * Last Modified Date
	 */
	@LastModifiedDate
	private LocalDateTime lastUpdatedDate;
	/**
	 * Size at the time of publish (Default value = 0)
	 */
	private int publishedListSize = 0;
}
