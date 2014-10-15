package com.allchange.guestbook.aquery;

import org.json.JSONObject;

import com.androidquery.callback.AjaxStatus;

public interface AqueryCallbackListener {
	public void AqueryCallback(String url, JSONObject json, AjaxStatus status);
}
