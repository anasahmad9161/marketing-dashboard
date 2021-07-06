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
public class ReplySaveRequest {

	private String campaignId;
	private String message;
	private String phone;
	private String timestamp;
}
