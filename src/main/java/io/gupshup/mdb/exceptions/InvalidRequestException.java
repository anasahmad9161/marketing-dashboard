package io.gupshup.mdb.exceptions;

import java.util.List;

public class InvalidRequestException extends RuntimeException {

	private final List<String> errors;

	public InvalidRequestException(List<String> errors) {
		this.errors = errors;
	}

	@Override
	public String getMessage() {
		return String.join(", ", errors);
	}
}
