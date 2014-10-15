package com.allchange.guestbook.aquery.delete.room;

import org.json.JSONObject;

import com.androidquery.callback.AjaxStatus;

public interface AqueryDeleteRoomCallbackListener {
	public void AqueryDeleteRoomCallback(String url, JSONObject json,
			AjaxStatus status);
}
