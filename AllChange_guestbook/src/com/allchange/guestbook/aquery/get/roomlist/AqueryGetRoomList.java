package com.allchange.guestbook.aquery.get.roomlist;

import org.json.JSONObject;

import android.util.Log;

import com.allchange.guestbook.aquery.AqueryFailCallbackListener;
import com.allchange.guestbook.aquery.AqueryGetRoomListCallbackListener;
import com.allchange.guestbook.aquery.AqueryRootRequest;
import com.allchange.guestbook.property.PropertyManager;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

public class AqueryGetRoomList extends AqueryRootRequest {
	public static final String TAG = "AqueryGetRoomList";
	private AqueryGetRoomListCallbackListener mListener;
	AqueryFailCallbackListener fListener;
	AqueryGetRoomListCallbackListener listener;
	AQuery aq;
	String url;

	public AqueryGetRoomList(AqueryGetRoomListCallbackListener listener,
			AQuery aq, String url, AqueryFailCallbackListener fListener) {
		this.listener = listener;
		this.fListener = fListener;
		this.aq = aq;
		this.url = url;
		requestGetRoomList(listener, aq, url);
	}

	public void requestGetRoomList(AqueryGetRoomListCallbackListener listener,
			AQuery aq, String url) {
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
					mListener.AqueryGetRoomListCallback(url, json, status);
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
		requestGetRoomList(listener, aq, url);
	}
}
