package io.gupshup.mdb.errors;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class RestError {

	@NonNull
	private String code;
	@NonNull
	private String message;
}
