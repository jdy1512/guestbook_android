package com.allchange.guestbook.aquery.get.feed;

import org.json.JSONObject;

import com.androidquery.callback.AjaxStatus;

public interface AqueryGetFeedCallbackListener {
	public void getFeedCallback(String url, JSONObject json, AjaxStatus status);
}
