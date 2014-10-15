package com.allchange.guestbook.main.feedchat;

import java.util.HashMap;

public class MapEmailImage extends HashMap<String, String> {

	private static MapEmailImage instance = null;

	public static MapEmailImage getInstance() {
		if (instance == null) {
			instance = new MapEmailImage();
		}
		return instance;
	}
}
