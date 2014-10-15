package com.allchange.guestbook.aquery.get.properties;

import org.json.JSONObject;

import com.androidquery.callback.AjaxStatus;

public interface AqueryGetPropertiesCallbackListener {
	public void AqueryGetPropertiesCallback(String url, JSONObject json,
			AjaxStatus status);
}
