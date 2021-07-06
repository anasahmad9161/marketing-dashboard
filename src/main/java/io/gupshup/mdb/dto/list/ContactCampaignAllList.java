package io.gupshup.mdb.dto.list;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ContactCampaignAllList {

	private String listId;
	private String name;
	private LocalDateTime creationDate;
	private LocalDateTime lastUpdatedDate;
	private int size;
}
