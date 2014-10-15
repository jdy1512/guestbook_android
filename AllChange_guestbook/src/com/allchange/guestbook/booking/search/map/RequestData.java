package com.allchange.guestbook.booking.search.map;

public class RequestData {

	public static RequestData instance = null;

	public static RequestData getInstance() {
		if (instance == null) {
			instance = new RequestData();
		}
		return instance;
	}

	public String word = "";
	public double sw_lat;
	public double sw_lng;
	public double ne_lat;
	public double ne_lng;
	public boolean byMap;
	public float zoom;

	public int page = 1;
}
