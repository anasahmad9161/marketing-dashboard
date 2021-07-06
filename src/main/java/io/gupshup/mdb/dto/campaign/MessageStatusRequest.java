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
public class MessageStatusRequest {

	private String campaignId;
	private String timestamp;
	private String phone;
	private String status;
}
