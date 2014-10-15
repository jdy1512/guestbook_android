package com.allchange.guestbook.url;

public class Url {
	// public static String SERVER_URL = "http://14.49.43.118/allchange";
	// public static String SERVER_SSL_URL =
	// "https://14.49.43.118:1512/allchange";

	public static String SERVER_URL = "http://ec2-54-186-141-25.us-west-2.compute.amazonaws.com:8080/allchange";
	// public static String SERVER_SSL_URL =
	// "http://ec2-54-186-141-25.us-west-2.compute.amazonaws.com:8080/facebook";
	public static final String SERVER_FACEBOOK_LOGIN = "http://ec2-54-186-141-25.us-west-2.compute.amazonaws.com:8080/allchange/facebook";
	public static final String SERVER_GOOGLE_LOGIN = "http://ec2-54-186-141-25.us-west-2.compute.amazonaws.com:8080/allchange/google";
	// public static final String SERVER_FACEBOOK_LOGIN =
	// "https://ec2-54-186-141-25.us-west-2.compute.amazonaws.com:8443/allchange/facebook";
	// public static final String SERVER_GOOGLE_LOGIN =
	// "https://ec2-54-186-141-25.us-west-2.compute.amazonaws.com:8443/allchange/google";
	public static final String SERVER_SEARCH = "http://ec2-54-186-141-25.us-west-2.compute.amazonaws.com:8080/allchange/search";

	// public static String SERVER_URL = "http://172.20.3.76/allchange";
	// public static String SERVER_SSL_URL = "http://172.20.3.76:8080/facebook";
	// public static final String SERVER_LOGIN = SERVER_SSL_URL + "/user/login";
	// public static final String SERVER_GOOGLE_LOGIN =
	// "http://172.20.3.76:8080/google";
	// public static final String SERVER_FACEBOOK_LOGIN =
	// "http://172.20.3.76/allchange/facebook";
	// public static final String SERVER_SEARCH =
	// "http://172.20.3.76/allchange/search";

	public static final String CHAT_DOMAIN = "ec2-54-186-141-25.us-west-2.compute.amazonaws.com";

	public static final String SERVER_MAKE_ROOM = SERVER_URL + "/room/make";
	public static final String SERVER_GET_ROOMS = SERVER_URL + "/room";
	public static final String SERVER_GET_CHAT = SERVER_URL + "/room/update";
	public static final String SERVER_WRITE = SERVER_URL + "/room/write";
	public static final String SERVER_GET_ROOM_LIST = SERVER_URL
			+ "/trippal/room";
	public static final String SERVER_MAKE_CHATTING_ROOM = SERVER_URL
			+ "/trippal/room/create";
	public static final String SERVER_GET_PROFILES = SERVER_URL
			+ "/trippal/people";
	public static final String SERVER_CREATE_ACCOMMODATION = SERVER_URL
			+ "/accommodation/create";

	public static final String SERVER_GET_MAP_MEMBERS = SERVER_URL
			+ "/trippal/location";
	public static final String SERVER_UPDATE_MY_BOUNDS = SERVER_URL
			+ "/trippal/location/update";
	public static final String SERVER_GPS_TRACE = SERVER_URL + "/gps/trace";
	public static final String SERVER_SHOUT_OUT = SERVER_URL + "/trippal/shout";
	public static final String SERVER_SHOUT_OUT_WRITE = SERVER_URL
			+ "/trippal/shout/insert";

	public static final String SERVER_GET_PROPERTIES = SERVER_URL
			+ "/properties";
	public static final String SERVER_GET_VISIT = SERVER_URL + "/visit";
	public static final String SERVER_DELETE_ROOM = SERVER_URL
			+ "/trippal/room/delete";
	public static final String SERVER_TRACE_SHOW = SERVER_URL
			+ "/gps/trace/show";

}
