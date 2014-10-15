package com.allchange.guestbook.chatroom.list;

public class ChatRoomInFoData {
	private static ChatRoomInFoData instance = null;

	public boolean doitable = false;
	public String gh_no;
	public String check_indate;
	public String check_outdate;

	public static ChatRoomInFoData getInstance() {
		if (instance == null) {
			instance = new ChatRoomInFoData();
		}
		return instance;
	}

	public void clearAllData() {

		doitable = false;
		gh_no = "";
		check_indate = "";
		check_outdate = "";
	}
}
