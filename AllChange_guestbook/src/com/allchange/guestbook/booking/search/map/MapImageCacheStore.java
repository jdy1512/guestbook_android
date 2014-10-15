package com.allchange.guestbook.booking.search.map;

import java.util.HashMap;

import android.graphics.Bitmap;

public class MapImageCacheStore extends HashMap<String, Bitmap> {

	private static MapImageCacheStore instance = null;

	public static MapImageCacheStore getInstance() {

		if (instance == null) {
			instance = new MapImageCacheStore();
		}

		if (instance.size() > 100) {
			instance.clear();
			instance = null;
			instance = new MapImageCacheStore();
		}
		return instance;
	}
}