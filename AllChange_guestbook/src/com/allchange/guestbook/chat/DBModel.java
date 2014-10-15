package com.allchange.guestbook.chat;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.allchange.guestbook.property.MyApplication;

public class DBModel {

	public final static String TAG = "DBModel";
	public final static String DB_NAME_USER = "user_data";
	public final static String DB_NAME_CHAT = "chat_";
	public final static String DB_NAME_CHATLIST = "chat_list";

	private static DBModel instance;
	String[] columns_userData = { DBConstants.ChatTable._ID,
			DBConstants.ChatTable.COLUMN_EMAIL,
			DBConstants.ChatTable.COLUMN_FIRST_NAME,
			DBConstants.ChatTable.COLUMN_LAST_NAME,
			DBConstants.ChatTable.COLUMN_IMAGE_URL };
	String[] columns_chat = { DBConstants.ChatTable._ID,
			DBConstants.ChatTable.COLUMN_EMAIL,
			DBConstants.ChatTable.COLUMN_MESSAGE,
			DBConstants.ChatTable.COLUMN_MESSAGE_TIME };
	String[] columns_chatList = { DBConstants.ChatTable.COLUMN_TABLE_NAME,
			DBConstants.ChatTable.COLUMN_CHAT_TYPE,
			DBConstants.ChatTable.COLUMN_TARGET };

	public static DBModel getInstance() {
		if (instance == null) {
			instance = new DBModel();
		}
		return instance;
	}

	MyDBOpenHelper openHelper;

	private DBModel() {
		openHelper = new MyDBOpenHelper(MyApplication.getContext());
	}

	public Cursor queryUserData() {
		SQLiteDatabase db = openHelper.getReadableDatabase();
		return db.query(DB_NAME_USER, columns_userData, null, null, null, null,
				null);
	}

	public Cursor queryLastChat(String tableName) {
		SQLiteDatabase db = openHelper.getReadableDatabase();
		return db.query(tableName, columns_chat, null, null, null, null,
				DBConstants.ChatTable.COLUMN_MESSAGE_TIME + " DESC", "1");
	}

	public Cursor queryChat(String tableName) {
		SQLiteDatabase db = openHelper.getReadableDatabase();
		return db.query(tableName, columns_chat, null, null, null, null, null);
	}

	public ArrayList<DBChatListData> queryChatList() {
		SQLiteDatabase db = openHelper.getReadableDatabase();
		Cursor c = db.query(DB_NAME_CHATLIST, columns_chatList, null, null,
				null, null, null);
		int nameIndex = c
				.getColumnIndex(DBConstants.ChatTable.COLUMN_TABLE_NAME);
		int typeIndex = c
				.getColumnIndex(DBConstants.ChatTable.COLUMN_CHAT_TYPE);
		int targetIndex = c.getColumnIndex(DBConstants.ChatTable.COLUMN_TARGET);
		ArrayList<DBChatListData> arrChatLiat = new ArrayList<DBChatListData>();
		while (c.moveToNext()) {
			String tableName = c.getString(nameIndex);
			String type = c.getString(typeIndex);
			String target = c.getString(targetIndex);
			DBChatListData md = new DBChatListData();
			md.tableName = tableName;
			md.type = type;
			md.target = target;
			arrChatLiat.add(md);
			// TODO view create and put in layout
		}
		return arrChatLiat;

	}

	/**
	 * Remove all users and groups from database.
	 */
	public void removeAll() {
		// If whereClause is null, it will delete all rows.
		SQLiteDatabase db = openHelper.getWritableDatabase();
		int size = DBModel.getInstance().getChatListSize();
		for (int i = 0; i < size; i++) {
			db.delete(DB_NAME_CHAT + i, null, null);
			Log.e(TAG, "removeAll : " + DB_NAME_CHAT + i);
		}
		db.delete(DB_NAME_CHATLIST, null, null);
		Log.e(TAG, "removeAll() ");
		db.close();
	}

	public boolean isChatCreated(String target) {
		SQLiteDatabase db = openHelper.getReadableDatabase();
		Cursor c = db.query(DB_NAME_CHATLIST, columns_chatList,
				DBConstants.ChatTable.COLUMN_TARGET + "=?",
				new String[] { target }, null, null, null);
		int nameIndex = c
				.getColumnIndex(DBConstants.ChatTable.COLUMN_TABLE_NAME);
		while (c.moveToNext()) {
			String tableName = c.getString(nameIndex);
			return true;
		}
		return false;
	}

	public String getMember(String tableName) {
		SQLiteDatabase db = openHelper.getReadableDatabase();
		Cursor c = db.query(DB_NAME_CHATLIST, columns_chatList,
				DBConstants.ChatTable.COLUMN_TABLE_NAME + "=?",
				new String[] { tableName }, null, null, null);
		int nameIndex = c.getColumnIndex(DBConstants.ChatTable.COLUMN_TARGET);
		while (c.moveToNext()) {
			String target = c.getString(nameIndex);
			return target;
		}
		return null;
	}

	public int getChatListSize() {
		SQLiteDatabase db = openHelper.getReadableDatabase();
		Cursor c = db.query(DB_NAME_CHATLIST, columns_chatList, null, null,
				null, null, null);
		int size = 0;
		while (c.moveToNext()) {
			size++;
		}
		return size;

	}

	public String selectChatTableName(String target) {
		SQLiteDatabase db = openHelper.getReadableDatabase();
		Cursor c = db.query(DB_NAME_CHATLIST, columns_chatList,
				DBConstants.ChatTable.COLUMN_TARGET + "=?",
				new String[] { target }, null, null, null);
		int nameIndex = c
				.getColumnIndex(DBConstants.ChatTable.COLUMN_TABLE_NAME);
		while (c.moveToNext()) {
			String tableName = c.getString(nameIndex);
			return tableName;
		}
		return insertNewChat(target);
	}

	public String insertNewChat(String target) {
		DBChatListData d = new DBChatListData();
		// 테이블이름 정하기 전에 정해진 테이블 개수 먼저 파악하기
		int size = DBModel.getInstance().getChatListSize();
		d.tableName = DBModel.DB_NAME_CHAT + size;
		d.type = "single";
		d.target = target;
		DBModel.getInstance().insert(d);
		if (mListener != null) {
			mListener.onCreateNewChat();
		}
		return d.tableName;
	}

	/**
	 * Linstener for TripPalsListFragment
	 * 
	 */
	public interface OnCreateNewChatListener {
		public void onCreateNewChat();
	}

	OnCreateNewChatListener mListener;

	public void setOnCreateNewChatListener(OnCreateNewChatListener listener) {
		mListener = listener;
	}

	// public void insert(MyData p) {
	// try {
	// SQLiteDatabase db = openHelper.getWritableDatabase();
	// ContentValues values = new ContentValues();
	// values.put(DBConstants.ChatTable.COLUMN_ID, p.email);
	// values.put(DBConstants.ChatTable.COLUMN_NAME, p.name);
	// values.put(DBConstants.ChatTable.COLUMN_CONTENT, p.description);
	// p.id = db.insert(Table_Name, null, values);
	// db.close();F
	// } catch (Exception e) {
	// System.out.println(e);
	// }
	//
	// // String insertSQL = "INSERT INTO " +
	// // DBConstants.PersonTable.TABLE_NAME
	// // + "(" + DBConstants.PersonTable.COLUMN_NAME + ","
	// // + DBConstants.PersonTable.COLUMN_AGE + ") values('"
	// // + person.name + "'," + person.age + ");";
	// // db.execSQL(insertSQL);
	//
	// }

	// 유저 정보
	public void insert(DBUserData d) {
		try {
			SQLiteDatabase db = openHelper.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put(DBConstants.ChatTable.COLUMN_EMAIL, d.email);
			values.put(DBConstants.ChatTable.COLUMN_FIRST_NAME, d.first_ame);
			values.put(DBConstants.ChatTable.COLUMN_LAST_NAME, d.last_name);
			values.put(DBConstants.ChatTable.COLUMN_IMAGE_URL, d.imageUrl);
			db.insert(DB_NAME_USER, null, values);
			// db.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	// 채팅 내용
	public void insert(DBChatData d, String tableName) {
		try {
			SQLiteDatabase db = openHelper.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put(DBConstants.ChatTable.COLUMN_EMAIL, d.email);
			values.put(DBConstants.ChatTable.COLUMN_MESSAGE, d.message);
			values.put(DBConstants.ChatTable.COLUMN_MESSAGE_TIME, d.time);
			d.id = db.insert(tableName, null, values);
			// db.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public void insert(DBChatListData d) {
		try {
			SQLiteDatabase db = openHelper.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put(DBConstants.ChatTable.COLUMN_TABLE_NAME, d.tableName);
			values.put(DBConstants.ChatTable.COLUMN_CHAT_TYPE, d.type);
			values.put(DBConstants.ChatTable.COLUMN_TARGET, d.target);
			d.id = db.insert(DB_NAME_CHATLIST, null, values);
			// db.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	// public void update(MyData p) {
	// SQLiteDatabase db = openHelper.getWritableDatabase();
	// ContentValues values = new ContentValues();
	// values.put(DBConstants.ChatTable.COLUMN_ID, p.email);
	// values.put(DBConstants.ChatTable.COLUMN_NAME, p.name);
	// values.put(DBConstants.ChatTable.COLUMN_CONTENT, p.description);
	// String selection = DBConstants.ChatTable._ID + " = ? ";
	// String[] args = { "" + p.id };
	// db.update(Table_Name, values, selection, args);
	// db.close();
	// }

	// 유저
	public void update(DBUserData d) {
		SQLiteDatabase db = openHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(DBConstants.ChatTable.COLUMN_EMAIL, d.email);
		values.put(DBConstants.ChatTable.COLUMN_FIRST_NAME, d.first_ame);
		values.put(DBConstants.ChatTable.COLUMN_LAST_NAME, d.last_name);
		values.put(DBConstants.ChatTable.COLUMN_IMAGE_URL, d.imageUrl);
		String selection = DBConstants.ChatTable._ID + " = ? ";
		String[] args = { "" + d.email };
		db.update(DB_NAME_USER, values, selection, args);
		db.close();
	}

	// 채팅
	public void update(DBChatData d, String tableName) {
		SQLiteDatabase db = openHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(DBConstants.ChatTable.COLUMN_EMAIL, d.email);
		values.put(DBConstants.ChatTable.COLUMN_MESSAGE, d.message);
		values.put(DBConstants.ChatTable.COLUMN_MESSAGE_TIME, d.time);
		String selection = DBConstants.ChatTable._ID + " = ? ";
		String[] args = { "" + d.id };
		db.update(tableName, values, selection, args);
		db.close();
	}

	// 리스트
	public void update(DBChatListData d) {
		SQLiteDatabase db = openHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(DBConstants.ChatTable.COLUMN_TABLE_NAME, d.tableName);
		values.put(DBConstants.ChatTable.COLUMN_CHAT_TYPE, d.type);
		values.put(DBConstants.ChatTable.COLUMN_TARGET, d.target);
		String selection = DBConstants.ChatTable._ID + " = ? ";
		String[] args = { "" + d.id };
		db.update(DB_NAME_CHATLIST, values, selection, args);
		db.close();
	}

	// 리스트
	public void deleteChat(String target) {
		Log.e(TAG, "remove chat target : " + target);

		// chat contents clear
		SQLiteDatabase db = openHelper.getWritableDatabase();
		db.delete(selectChatTableName(target), null, null);
		// db.close();

		// chat room clear
		// db = openHelper.getWritableDatabase();
		db.delete(DB_NAME_CHATLIST, DBConstants.ChatTable.COLUMN_TARGET
				+ " = ? ", new String[] { target });
		db.close();
	}

	// public void insertMulti(ArrayList<MyData> mData) {
	// SQLiteDatabase db = openHelper.getWritableDatabase();
	// db.beginTransaction();
	//
	// // 임시로 일단 테이블 다 지우고 다 넣기
	// db.delete(Table_Name, null, null);
	//
	// try {
	// ContentValues values = new ContentValues();
	// for (MyData p : mData) {
	// values.clear();
	// values.put(DBConstants.ChatTable.COLUMN_ID, p.email);
	// values.put(DBConstants.ChatTable.COLUMN_NAME, p.name);
	// values.put(DBConstants.ChatTable.COLUMN_CONTENT, p.description);
	// db.insert(Table_Name, null, values);
	// }
	// db.setTransactionSuccessful();
	// } catch (Exception e) {
	// e.printStackTrace();
	// } finally {
	// db.endTransaction();
	// }
	// db.close();
	// }

}
