package io.gupshup.mdb.exceptions;

public class AuthenticationException extends RuntimeException{

	private final String message;

	public AuthenticationException(String message) {
		this.message = message;
	}

	@Override
	public String getMessage() {
		return this.message;
	}
}
