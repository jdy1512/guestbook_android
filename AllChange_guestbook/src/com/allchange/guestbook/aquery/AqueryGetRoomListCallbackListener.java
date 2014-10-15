package com.allchange.guestbook.aquery;

import org.json.JSONObject;

import com.androidquery.callback.AjaxStatus;

public interface AqueryGetRoomListCallbackListener {
	public void AqueryGetRoomListCallback(String url, JSONObject json, AjaxStatus status);
}
