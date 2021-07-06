package io.gupshup.mdb.exceptions;

public class ResourceNotFountException extends RuntimeException {

	private final String resourceName;
	private final String resourceParam;

	public ResourceNotFountException(String resourceName, String resourceParam) {
		this.resourceName = resourceName;
		this.resourceParam = resourceParam;
	}

	@Override
	public String getMessage() {
		return "Unable to fetch resource " + this.resourceName + " with param : " + this.resourceParam;
	}
}
