package com.allchange.guestbook.aquery;

import org.json.JSONObject;

import com.androidquery.callback.AjaxStatus;

public interface AqueryFailCallbackListener {

	void AqueryFailCallback(String url, JSONObject json, AjaxStatus status,
			String tag);
}
