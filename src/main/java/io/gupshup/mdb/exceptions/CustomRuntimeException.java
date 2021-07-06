package io.gupshup.mdb.exceptions;

public class CustomRuntimeException extends RuntimeException {

	private final String message;

	public CustomRuntimeException(String message) {this.message = message;}

	@Override
	public String getMessage() {
		return this.message;
	}
}
