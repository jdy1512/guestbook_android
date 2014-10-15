package com.allchange.guestbook.chat;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDBOpenHelper extends SQLiteOpenHelper {

	private final static String DB_NAME = "guestbookdb";

	private final static int DB_VERSION = 1;

	public MyDBOpenHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// Log.e("MyDBOpenHelper", "onCreate : " + "CREATE TABLE " + tableName
		// + "(" + DBConstants.ChatTable._ID
		// + " integer PRIMARY KEY autoincrement, "
		// + DBConstants.ChatTable.COLUMN_NAME + " text,"
		// + DBConstants.ChatTable.COLUMN_CONTENT + " text);");
		String sql = "CREATE TABLE " + "user_data" + "("
				+ DBConstants.ChatTable._ID
				+ " integer PRIMARY KEY autoincrement, "
				+ DBConstants.ChatTable.COLUMN_EMAIL + " text,"
				+ DBConstants.ChatTable.COLUMN_FIRST_NAME + " text,"
				+ DBConstants.ChatTable.COLUMN_LAST_NAME + " text,"
				+ DBConstants.ChatTable.COLUMN_IMAGE_URL + " text);";

		db.execSQL(sql);

		sql = "CREATE TABLE " + "chat_list" + "("
				+ DBConstants.ChatTable.COLUMN_TABLE_NAME
				+ " text PRIMARY KEY," + DBConstants.ChatTable.COLUMN_CHAT_TYPE
				+ " text," + DBConstants.ChatTable.COLUMN_TARGET + " text);";

		db.execSQL(sql);
		for (int i = 0; i < 100; i++) {
			sql = "CREATE TABLE " + "chat_" + i + "("
					+ DBConstants.ChatTable._ID
					+ " integer PRIMARY KEY autoincrement, "
					+ DBConstants.ChatTable.COLUMN_EMAIL + " text,"
					+ DBConstants.ChatTable.COLUMN_MESSAGE + " text,"
					+ DBConstants.ChatTable.COLUMN_MESSAGE_TIME + " integer,"
					+ DBConstants.ChatTable.COLUMN_IMAGE_URL + " text);";
			db.execSQL(sql);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// db.execSQL("DROP TABLE IF EXISTS " + tableName);
		// onCreate(db);
	}
}
