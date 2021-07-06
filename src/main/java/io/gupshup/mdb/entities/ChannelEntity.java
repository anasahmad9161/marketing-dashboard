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
 * POJO for Channel Entity
 *
 * @author deepanshu
 */
@Getter
@Setter
@Entity
@Table(name = "channel")
@RequiredArgsConstructor
@NoArgsConstructor
public class ChannelEntity {

	/**
	 * Channel ID
	 */
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String channelId;

	/**
	 * Name of the channel (Mandatory)
	 */
	@NonNull
	private String channelName;
}
