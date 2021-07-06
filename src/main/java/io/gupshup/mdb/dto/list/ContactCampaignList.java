package io.gupshup.mdb.dto.list;

import io.gupshup.mdb.dto.contact.Contact;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Builder
public class ContactCampaignList {

	private String listId;
	private String name;
	private LocalDateTime creationDate;
	private LocalDateTime lastUpdatedDate;
	private Set<Contact> contacts;
}
