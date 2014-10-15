package com.allchange.guestbook.aquery.delete.room;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.util.Log;

import com.allchange.guestbook.aquery.AqueryRootRequest;
import com.allchange.guestbook.aquery.EasySSLSocketFactory;
import com.allchange.guestbook.property.PropertyManager;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

public class AqueryDeleteRoom extends AqueryRootRequest {
	public static final String TAG = "AqueryDeleteRoom";
	private AqueryDeleteRoomCallbackListener mListener;
	AqueryDeleteRoomCallbackListener listener;
	AQuery aq;
	String url;
	String room_no;

	public AqueryDeleteRoom(AqueryDeleteRoomCallbackListener listener,
			AQuery aq, String url, String room_no) {
		this.listener = listener;
		this.aq = aq;
		this.url = url;
		this.room_no = room_no;
		// Log.e(TAG, "room_no : " + room_no);
		reqeustDeleteRoom(listener, aq, url, room_no);
	}

	public void reqeustDeleteRoom(AqueryDeleteRoomCallbackListener listener,
			AQuery aq, String url, String room_no) {

		mListener = listener;
		// callback 설정
		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>() {
			@Override
			public void callback(String url, JSONObject json, AjaxStatus status) {
				if (mListener != null)
					mListener.AqueryDeleteRoomCallback(url, json, status);
				else {
					Log.e(TAG, "AqueryCallbackListener ERROR!");
				}
			}
		};

		Map<String, Object> mParams = new HashMap<String, Object>();
		mParams.put("room_no", room_no);

		cb.cookies(PropertyManager.getInstance().getCookie());
		// request 전송
		aq.ajax(url, mParams, JSONObject.class, cb);
	}

	@Override
	public void reRequest() {
		reqeustDeleteRoom(listener, aq, url, room_no);
		super.reRequest();
	}
}
