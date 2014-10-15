package com.allchange.guestbook.aquery.write.text;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.util.Log;

import com.allchange.guestbook.aquery.AqueryFailCallbackListener;
import com.allchange.guestbook.aquery.AqueryRootRequest;
import com.allchange.guestbook.aquery.AqueryWriteTextCallbackListener;
import com.allchange.guestbook.property.PropertyManager;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

public class AqueryWriteText extends AqueryRootRequest {
	public static final String TAG = "AqueryWriteText";
	private AqueryWriteTextCallbackListener mListener;
	AqueryWriteTextCallbackListener listener;
	AqueryFailCallbackListener fListener;
	AQuery aq;
	String url;
	String message;

	public AqueryWriteText(AqueryWriteTextCallbackListener listener, AQuery aq,
			String url, String message, AqueryFailCallbackListener fListener) {
		this.listener = listener;
		this.fListener = fListener;
		this.aq = aq;
		this.url = url;
		this.message = message;
		RequestWriteText(listener, aq, url, message);
	}

	public void RequestWriteText(AqueryWriteTextCallbackListener listener,
			AQuery aq, String url, String message) {
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
					mListener.writeTextCallback(url, json, status);
				else {
					Log.e(TAG, "AqueryCallbackListener ERROR!");
				}
			}
		};

		Map<String, Object> mParams = new HashMap<String, Object>();
		mParams.put("message", message);

		cb.cookies(PropertyManager.getInstance().getCookie());
		// request 전송
		aq.ajax(url, mParams, JSONObject.class, cb);
	}

	@Override
	public void reRequest() {
		RequestWriteText(listener, aq, url, message);
		super.reRequest();
	}
}
