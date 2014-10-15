package com.allchange.guestbook.aquery.get.map.members;

import org.json.JSONObject;

import com.androidquery.callback.AjaxStatus;

public interface AqueryGetMapMembersCallbackListener {
	public void AqueryGetMapMembersCallback(String url, JSONObject json, AjaxStatus status);
}
