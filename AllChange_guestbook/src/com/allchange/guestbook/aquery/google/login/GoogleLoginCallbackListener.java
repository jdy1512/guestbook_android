package com.allchange.guestbook.aquery.google.login;

import org.json.JSONObject;

import com.androidquery.callback.AjaxStatus;

public interface GoogleLoginCallbackListener {
	public void GoogleLoginCallback(String url, JSONObject json,
			AjaxStatus status, String email);
}
