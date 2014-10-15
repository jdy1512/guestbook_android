package com.allchange.guestbook.main.feedchat;

public class MessageData {

	public String email_id;
	public String firstname;
	public String lastname;
	public String last_message;
	public String last_message_dtime;
	public String profile_img_path;

	public MessageData() {
	}

	public MessageData(String a, String b) {
		email_id = a;
		last_message = b;
	}
}
