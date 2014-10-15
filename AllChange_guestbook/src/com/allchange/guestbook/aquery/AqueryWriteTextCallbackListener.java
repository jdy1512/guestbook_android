package com.allchange.guestbook.aquery;

import org.json.JSONObject;

import com.androidquery.callback.AjaxStatus;

public interface AqueryWriteTextCallbackListener {
	public void writeTextCallback(String url, JSONObject json, AjaxStatus status);
}
