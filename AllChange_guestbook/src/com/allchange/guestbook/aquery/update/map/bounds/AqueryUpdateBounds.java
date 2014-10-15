package com.allchange.guestbook.aquery.update.map.bounds;

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

public class AqueryUpdateBounds extends AqueryRootRequest {
	public static String TAG = "AqueryUpdateBounds";
	private AqueryUpdateBoundsCallbackListener mListener;
	AqueryUpdateBoundsCallbackListener listener;
	AqueryFailCallbackListener fListener;
	AQuery aq;
	String url;
	String gb_lat_start;
	String gb_lon_start;
	String gb_lat_end;
	String gb_lon_end;

	public AqueryUpdateBounds(AqueryUpdateBoundsCallbackListener listener,
			AQuery aq, String url, String gb_lat_start, String gb_lon_start,
			String gb_lat_end, String gb_lon_end,AqueryFailCallbackListener fListener) {
		this.listener = listener;
		this.fListener = fListener;
		this.aq = aq;
		this.url = url;
		this.gb_lat_start = gb_lat_start;
		this.gb_lon_start = gb_lon_start;
		this.gb_lat_end = gb_lat_end;
		this.gb_lon_end = gb_lon_end;

		requestUpdateBounds(listener, aq, url, gb_lat_start, gb_lon_start,
				gb_lat_end, gb_lon_end);
	}

	public void requestUpdateBounds(
			AqueryUpdateBoundsCallbackListener listener, AQuery aq, String url,
			String gb_lat_start, String gb_lon_start, String gb_lat_end,
			String gb_lon_end) {
		mListener = listener;
		// callback 설정
		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>() {
			@Override
			public void callback(String url, JSONObject json, AjaxStatus status) {
				// TODO Auto-generated method stub
				if (fListener != null) {
					fListener.AqueryFailCallback(url, json, status, TAG);
				}
				if (mListener != null) {
					mListener.AqueryUpdateBoundsCallback(url, json, status);
				} else {
					Log.e(TAG, "AqueryCallbackListener ERROR!");
				}
			}
		};

		Map<String, Object> mParams = new HashMap<String, Object>();

		// default 반경 3km
		// distan *= 3000;
		mParams.put("lat_start", gb_lat_start);
		mParams.put("lon_start", gb_lon_start);
		mParams.put("lat_end", gb_lat_end);
		mParams.put("lon_end", gb_lon_end);
		Log.e("TAG", gb_lat_start + ", " + gb_lon_start + ", " + gb_lat_end
				+ ", " + gb_lon_end);

		cb.cookies(PropertyManager.getInstance().getCookie());
		// request 전송
		aq.ajax(url, mParams, JSONObject.class, cb);
	}

	@Override
	public void reRequest() {
		requestUpdateBounds(listener, aq, url, gb_lat_start, gb_lon_start,
				gb_lat_end, gb_lon_end);
		super.reRequest();
	}
}
