package com.allchange.guestbook.aquery.make.chattingroom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.util.Log;

import com.allchange.guestbook.aquery.AqueryFailCallbackListener;
import com.allchange.guestbook.aquery.AqueryMakeChattingRoomCallbackListener;
import com.allchange.guestbook.aquery.AqueryRootRequest;
import com.allchange.guestbook.main.profiles.ChattingRoomPeopleData;
import com.allchange.guestbook.property.PropertyManager;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

public class AqueryMakeChattingRoom extends AqueryRootRequest {
	private AqueryMakeChattingRoomCallbackListener mListener;
	public static final String TAG = "AqueryMakeChattingRoom";
	AqueryMakeChattingRoomCallbackListener listener;
	AqueryFailCallbackListener fListener;
	AQuery aq;
	String url;
	ArrayList<ChattingRoomPeopleData> member;

	public AqueryMakeChattingRoom(
			AqueryMakeChattingRoomCallbackListener listener, AQuery aq,
			String url, ArrayList<ChattingRoomPeopleData> member,
			AqueryFailCallbackListener fListener) {
		this.listener = listener;
		this.fListener = fListener;

		this.aq = aq;
		this.url = url;
		this.member = member;
		requestMakeChattingRoom(listener, aq, url, member);
	}

	public void requestMakeChattingRoom(
			AqueryMakeChattingRoomCallbackListener listener, AQuery aq,
			String url, final ArrayList<ChattingRoomPeopleData> member) {
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
					mListener.AqueryMakeChattingRoomCallback(url, json, status,
							member);
				else {
					Log.e(TAG, "AqueryCallbackListener ERROR!");
				}
			}
		};
		// member_email_id1 : 대화 상대 이메일 (128)
		// ------------------------- additional requirements
		// ----------------------------
		// member_email_id2 ~ : 대화 상대 이메일 추가 (128)

		Map<String, Object> mParams = new HashMap<String, Object>();
		for (int i = 0; i < member.size(); i++) {
			mParams.put("member_email_id1", member.get(0).email_id);
		}

		cb.cookies(PropertyManager.getInstance().getCookie());
		// request 전송
		aq.ajax(url, mParams, JSONObject.class, cb);
	}

	@Override
	public void reRequest() {
		requestMakeChattingRoom(listener, aq, url, member);
		super.reRequest();
	}
}
