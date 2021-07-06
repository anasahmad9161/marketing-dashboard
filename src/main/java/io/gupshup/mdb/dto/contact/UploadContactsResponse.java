package io.gupshup.mdb.dto.contact;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadContactsResponse {

	private List<String> contactIds;
	private int failed;
	private int duplicates;
	private List<ContactRecord> details;
}
