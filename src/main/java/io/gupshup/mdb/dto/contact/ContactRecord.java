package io.gupshup.mdb.dto.contact;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ContactRecord {

	private int rowNum;
	private String name;
	private String nickname;
	private String salutation;
	private String countryCode;
	private String phoneNumber;
	private String status;
	private String error;
}
