package io.gupshup.mdb.exceptions;

public class ResourceAlreadyExistsException extends RuntimeException {

	private final String resourceName;
	private final String resourceParamValue;

	public ResourceAlreadyExistsException(String resourceName, String resourceParamValue) {
		this.resourceName = resourceName;
		this.resourceParamValue = resourceParamValue;
	}

	@Override
	public String getMessage() {
		return "Duplicate " + this.resourceName + " : " + this.resourceParamValue;
	}
}
