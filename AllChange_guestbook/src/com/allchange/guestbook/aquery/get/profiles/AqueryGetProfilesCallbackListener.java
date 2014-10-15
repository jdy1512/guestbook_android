package com.allchange.guestbook.aquery.get.profiles;

import org.json.JSONObject;

import com.androidquery.callback.AjaxStatus;

public interface AqueryGetProfilesCallbackListener {
	public void AqueryGetProfilesCallback(String url, JSONObject json, AjaxStatus status);
}
