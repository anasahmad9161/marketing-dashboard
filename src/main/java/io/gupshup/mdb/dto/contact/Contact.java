package io.gupshup.mdb.dto.contact;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Builder
public class Contact {

	private String id;
	private String name;
	private String nickname;
	private String salutation;
	private String phoneNumber;
	private Set<String> listIds;
}
