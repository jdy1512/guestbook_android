package com.allchange.guestbook.aquery.get.map.members;

import org.json.JSONObject;

import android.util.Log;

import com.allchange.guestbook.aquery.AqueryFailCallbackListener;
import com.allchange.guestbook.aquery.AqueryRootRequest;
import com.allchange.guestbook.property.PropertyManager;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

public class AqueryGetMapMembers extends AqueryRootRequest {
	public static String TAG = "AqueryGetMapMembers";
	private AqueryGetMapMembersCallbackListener mListener;
	AqueryGetMapMembersCallbackListener listener;
	AqueryFailCallbackListener fListener;
	AQuery aq;
	String url;

	public AqueryGetMapMembers(AqueryGetMapMembersCallbackListener listener,
			AQuery aq, String url, AqueryFailCallbackListener fListener) {
		this.listener = listener;
		this.fListener = fListener;
		this.aq = aq;
		this.url = url;
		requestGetMapMembers(listener, aq, url);
	}

	public void requestGetMapMembers(
			AqueryGetMapMembersCallbackListener listener, AQuery aq, String url) {
		mListener = listener;
		// callback 설정
		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>() {
			@Override
			public void callback(String url, JSONObject json, AjaxStatus status) {
				// TODO Auto-generated method stub
				if (fListener != null) {
					fListener.AqueryFailCallback(url, json, status, TAG);
				}
				if (mListener != null)
					mListener.AqueryGetMapMembersCallback(url, json, status);
				else {
					Log.e(TAG, "AqueryCallbackListener ERROR!");
				}
			}
		};

		cb.cookies(PropertyManager.getInstance().getCookie());
		// request 전송
		aq.ajax(url, JSONObject.class, cb);
	}

	@Override
	public void reRequest() {
		requestGetMapMembers(listener, aq, url);
		super.reRequest();
	}
}
