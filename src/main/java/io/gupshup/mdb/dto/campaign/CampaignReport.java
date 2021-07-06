package io.gupshup.mdb.dto.campaign;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class CampaignReport {

	private String name;
	private String listName;
	private String channelName;
	private String message;
	private String sender;
	private String recipient;
	private String status;
	private LocalDateTime timestamp;
}
