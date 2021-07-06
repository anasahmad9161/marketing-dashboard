package io.gupshup.mdb.constants;

public class ServiceConstants {

	//CSV Headers
	public static final String FULL_NAME = "Full Name";
	public static final String NICKNAME = "Nickname";
	public static final String SALUTATION = "Salutation";
	public static final String MOBILE_NUMBER = "Mobile Number";
	public static final String COUNTRY_CODE = "Country Code";
	public static final String COUNTRY_REGION = "IN";

	//Errors
	public static final String EMPTY_FIELD = "Empty Field : ";

	public static final String ALL_CONTACTS = "All Contacts";
	public static final String SUCCESS = "SUCCESS";
	public static final String FAILED = "FAILED";
	public static final String MESSAGE = "message";
	public static final String WHATSAPP = "Whatsapp";
	public static final String SMS = "SMS";

	private ServiceConstants() {
		throw new IllegalStateException("Static Constants Class");
	}

}
