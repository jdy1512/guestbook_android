package com.allchange.guestbook.aquery.update.map.bounds;

import org.json.JSONObject;

import com.androidquery.callback.AjaxStatus;

public interface AqueryUpdateBoundsCallbackListener {
	public void AqueryUpdateBoundsCallback(String url, JSONObject json, AjaxStatus status);
}
