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
import java.time.LocalDateTime;

/**
 * POJO for Campaign Reply Entity
 *
 * @author deepanshu
 */
@Getter
@Setter
@Entity
@Table(name = "replies")
@RequiredArgsConstructor
@NoArgsConstructor
public class CampaignReply {

	/**
	 * Reply ID
	 */
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String replyId;

	@NonNull
	private String campaignId;

	@NonNull
	private String message;

	@NonNull
	private String phone;

	@NonNull
	private LocalDateTime timestamp;

}
