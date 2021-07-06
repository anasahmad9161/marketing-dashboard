package io.gupshup.mdb.dto.campaign;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SaveCampaignRequest {

	private String name;
	private String listId;
	private String channelId;
	private String message;
	private String sender;
}
