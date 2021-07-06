package io.gupshup.mdb.dto.campaign;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class Campaign {

	private String campaignId;
	private String name;
	private String listName;
	private String channelName;
	private String message;
	private Status status;
	private String sender;
	private LocalDateTime createdDate;
	private LocalDateTime publishedDate;
	private LocalDateTime completedDate;
	private LocalDateTime lastUpdatedDate;
}
