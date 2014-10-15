package com.allchange.guestbook.aquery.get.visit;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.util.Log;

import com.allchange.guestbook.aquery.AqueryFailCallbackListener;
import com.allchange.guestbook.aquery.AqueryRootRequest;
import com.allchange.guestbook.property.PropertyManager;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

public class AqueryGetVisit extends AqueryRootRequest {
	public static final String TAG = "AqueryGetVisit";
	private AqueryGetVisitCallbackListener mListener;
	AqueryGetVisitCallbackListener listener;
	AqueryFailCallbackListener fListener;
	AQuery aq;
	String url;
	String user_email_id;

	public AqueryGetVisit(AqueryGetVisitCallbackListener listener, AQuery aq,
			String url, String user_email_id,
			AqueryFailCallbackListener fListener) {
		this.fListener = fListener;
		this.listener = listener;
		this.aq = aq;
		this.url = url;
		this.user_email_id = user_email_id;
		reqeustGetProfiles(listener, aq, url, user_email_id);
	}

	public void reqeustGetProfiles(AqueryGetVisitCallbackListener listener,
			AQuery aq, String url, String user_email_id) {

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
					mListener.AqueryGetVisitCallback(url, json, status);
				else {
					Log.e(TAG, "AqueryCallbackListener ERROR!");
				}
			}
		};

		Map<String, Object> mParams = new HashMap<String, Object>();
		mParams.put("user_email_id", user_email_id);

		cb.cookies(PropertyManager.getInstance().getCookie());
		// request 전송
		aq.ajax(url, mParams, JSONObject.class, cb);
	}

	@Override
	public void reRequest() {

		reqeustGetProfiles(listener, aq, url, user_email_id);
		super.reRequest();
	}
}
