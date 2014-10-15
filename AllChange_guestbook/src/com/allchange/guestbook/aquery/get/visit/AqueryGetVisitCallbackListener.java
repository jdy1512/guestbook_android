package com.allchange.guestbook.aquery.get.visit;

import org.json.JSONObject;

import com.androidquery.callback.AjaxStatus;

public interface AqueryGetVisitCallbackListener {
	public void AqueryGetVisitCallback(String url, JSONObject json,
			AjaxStatus status);
}
