package com.allchange.guestbook.chat;

import java.util.HashMap;

import android.database.Cursor;

public class ChatCacheStore {
	private HashMap<String, String> firstNameResovler;
	private HashMap<String, String> lastNameResovler;
	private HashMap<String, String> imageResovler;

	private static ChatCacheStore instance = null;

	public static ChatCacheStore getInstance() {

		if (instance == null) {
			instance = new ChatCacheStore();
		}
		return instance;
	}

	// get UserInfo In DB
	private void getUserDataInDB() {
		Cursor c = DBModel.getInstance().queryUserData();
		int emailIndex = c.getColumnIndex(DBConstants.ChatTable.COLUMN_EMAIL);
		int firstIndex = c
				.getColumnIndex(DBConstants.ChatTable.COLUMN_FIRST_NAME);
		int lastIndex = c
				.getColumnIndex(DBConstants.ChatTable.COLUMN_LAST_NAME);
		int imageIndex = c
				.getColumnIndex(DBConstants.ChatTable.COLUMN_IMAGE_URL);
		while (c.moveToNext()) {
			String email = c.getString(emailIndex);
			String fisrtName = c.getString(firstIndex);
			String lastName = c.getString(lastIndex);
			String image_url = c.getString(imageIndex);
			putAtFirstNameResovler(email, fisrtName);
			putAtLastNameResovler(email, lastName);
			putAtImageResovler(email, image_url);
		}
	}

	public ChatCacheStore() {
		firstNameResovler = new HashMap<String, String>();
		lastNameResovler = new HashMap<String, String>();
		imageResovler = new HashMap<String, String>();
		getUserDataInDB();
	}

	public HashMap<String, String> getFirstNameResovler() {
		return firstNameResovler;
	}

	public HashMap<String, String> getLastNameResovler() {
		return lastNameResovler;
	}

	public HashMap<String, String> getImageResovler() {
		return imageResovler;
	}

	public void putAtResovler(String key, String firstName, String lastName,
			String image_url) {
		if (imageResovler.get(key) != null) {
			try {
				// if (imageResovler.get(key) == null
				// || imageResovler.get(key).equals("")) {
				//
				// } else {
				DBUserData d = new DBUserData();
				d.email = key;
				d.first_ame = firstName;
				d.last_name = lastName;
				d.imageUrl = image_url;
				// db update 하기
				DBModel.getInstance().update(d);
				// }
			} catch (Exception e) {
				// System.out.println("imageResovler : " + imageResovler);
				// System.out.println("key : " + key);
				e.printStackTrace();
			}

		} else {
			DBUserData d = new DBUserData();
			d.email = key;
			d.first_ame = firstName;
			d.last_name = lastName;
			d.imageUrl = image_url;
			// db insert하기
			DBModel.getInstance().insert(d);
		}
		this.firstNameResovler.put(key, firstName);
		this.lastNameResovler.put(key, lastName);
		this.imageResovler.put(key, image_url);
	}

	private void putAtFirstNameResovler(String key, String value) {
		this.firstNameResovler.put(key, value);
	}

	private void putAtLastNameResovler(String key, String value) {
		this.lastNameResovler.put(key, value);
	}

	private void putAtImageResovler(String key, String value) {
		this.imageResovler.put(key, value);
	}

}
