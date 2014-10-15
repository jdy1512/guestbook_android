package com.allchange.guestbook.aquery.facebook.login;

import org.json.JSONObject;

import com.androidquery.callback.AjaxStatus;

public interface FBLoginCallbackListener {
	public void FBLoginCallback(String url, JSONObject json, AjaxStatus status);
}
