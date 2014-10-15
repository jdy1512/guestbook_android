package com.allchange.guestbook.aquery.make.room;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.util.Log;

import com.allchange.guestbook.aquery.AqueryCallbackListener;
import com.allchange.guestbook.aquery.AqueryFailCallbackListener;
import com.allchange.guestbook.aquery.AqueryRootRequest;
import com.allchange.guestbook.property.PropertyManager;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

public class AqueryMakeRoom extends AqueryRootRequest {
	public static final String TAG = "AqueryMakeRoom";
	private AqueryCallbackListener mListener;
	AqueryCallbackListener listener;
	AqueryFailCallbackListener fListener;
	AQuery aq;
	String url;
	String check_indate;
	String check_outdate;
	String gh_no;

	public AqueryMakeRoom(AqueryCallbackListener listener, AQuery aq,
			String url, String check_indate, String check_outdate, String gh_no,AqueryFailCallbackListener fListener) {
		this.listener = listener;
		this.fListener = fListener;
		this.aq = aq;
		this.url = url;
		this.check_indate = check_indate;
		this.check_outdate = check_outdate;
		this.gh_no = gh_no;
		requestMakeRoom(listener, aq, url, check_indate, check_outdate, gh_no);
	}

	public void requestMakeRoom(AqueryCallbackListener listener, AQuery aq,
			String url, String check_indate, String check_outdate, String gh_no) {
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
					mListener.AqueryCallback(url, json, status);
				else {
					Log.e(TAG, "AqueryCallbackListener ERROR!");
				}
			}
		};

		Map<String, Object> mParams = new HashMap<String, Object>();
		mParams.put("check_indate", check_indate);
		mParams.put("check_outdate", check_outdate);
		mParams.put("gh_no", gh_no);

		cb.cookies(PropertyManager.getInstance().getCookie());
		// request 전송
		aq.ajax(url, mParams, JSONObject.class, cb);
	}

	@Override
	public void reRequest() {
		requestMakeRoom(listener, aq, url, check_indate, check_outdate, gh_no);
		super.reRequest();
	}
}
