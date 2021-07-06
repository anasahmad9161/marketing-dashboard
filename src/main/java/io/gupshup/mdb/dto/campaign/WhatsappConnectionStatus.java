package io.gupshup.mdb.dto.campaign;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class WhatsappConnectionStatus {

	private String whatsappNumber;
	private boolean connected;
	private String error;

}
