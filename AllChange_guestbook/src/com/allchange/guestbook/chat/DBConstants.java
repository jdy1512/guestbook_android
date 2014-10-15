package com.allchange.guestbook.chat;

import android.provider.BaseColumns;

public class DBConstants {
	public static class ChatTable implements BaseColumns {
		// public static final String TABLE_NAME = "persontbl";
		// public static final String COLUMN_NAME = "name";
		// public static final String COLUMN_CONTENT = "content";

		public static final String COLUMN_EMAIL = "email";
		public static final String COLUMN_FIRST_NAME = "firstname";
		public static final String COLUMN_LAST_NAME = "lastname";
		public static final String COLUMN_IMAGE_URL = "imageurl";

		public static final String COLUMN_MESSAGE = "message";
		public static final String COLUMN_MESSAGE_TIME = "time";

		public static final String COLUMN_CHAT_TYPE = "chattype";
		public static final String COLUMN_TABLE_NAME = "tablename";
		public static final String COLUMN_TARGET = "target";
	}
}
