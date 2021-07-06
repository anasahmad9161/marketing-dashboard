package io.gupshup.mdb.constants;

public class APIConstants {

	public static final String API = "/api/";
	public static final String VERSION = "v1";
	public static final String USER = "/user";
	public static final String USER_ID = "/{userId}";
	public static final String CONTACT = "/contact";
	public static final String CONTACTS = "/contacts";
	public static final String CONTACT_ID = "/{contactId}";
	public static final String TOKEN = "token";
	public static final String LIST = "/list";
	public static final String LIST_ID = "/{listId}";
	public static final String LISTS = "/lists";
	public static final String CAMPAIGN = "/campaign";
	public static final String CAMPAIGNS = "/campaigns";
	public static final String CAMPAIGN_ID = "/{campaignId}";
	public static final String STATUS = "/status";
	public static final String SOCKET = "/socket";
	public static final String PUBLISH = "/publish";
	public static final String REPORT = "/report";
	public static final String REGISTER = "/register";
	public static final String LOGOUT = "/logout";
	public static final String USERID = "userId";
	public static final String CONTACTID = "contactId";
	public static final String LISTID = "listId";
	public static final String CAMPAIGNID = "campaignId";
	public static final String CHANNELID = "channelId";
	public static final String MESSAGEID = "messageId";
	public static final String WHATSAPP = "/whatsapp";
	public static final String ENABLE = "/enable";
	public static final String DISABLE = "/disable";
	public static final String ADMIN = "/admin";

	private APIConstants() {
		throw new IllegalStateException("Static Constants Class");
	}

}
