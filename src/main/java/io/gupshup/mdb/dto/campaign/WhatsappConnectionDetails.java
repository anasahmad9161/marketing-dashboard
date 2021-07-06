package io.gupshup.mdb.dto.campaign;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class WhatsappConnectionDetails {

	@NotBlank(message = "User ID is mandatory")
	private String userId;
	@NotBlank(message = "Connected Phone is mandatory")
	private String connectedPhone;
	private String sessionId;
	private String clientId;
	private String serverId;

}
