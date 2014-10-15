package com.allchange.guestbook.aquery.make.accomodation;

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

public class AqueryMakeAccomodation extends AqueryRootRequest {
	public static final String TAG = "AqueryMakeAccomodation";
	private AqueryCallbackListener mListener;
	AqueryCallbackListener listener;
	AqueryFailCallbackListener fListener;
	AQuery aq;
	String url;
	String gb_name;
	String check_indate;
	String check_outdate;
	String gb_latitude;
	String gb_longitude;
	String gb_channel;
	String gb_address;
	String gb_for;
	String gb_with;
	String gb_seeking;

	public AqueryMakeAccomodation(AqueryCallbackListener listener, AQuery aq,
			String url, String gb_name, String gb_address, String gb_for,
			String gb_with, String gb_seeking, String check_indate,
			String check_outdate, String gb_latitude, String gb_longitude,
			String gb_channel, AqueryFailCallbackListener fListener) {
		this.listener = listener;
		this.fListener = fListener;
		this.aq = aq;
		this.url = url;
		this.gb_name = gb_name;
		this.gb_for = gb_for;
		this.gb_with = gb_with;
		this.gb_seeking = gb_seeking;
		this.check_indate = check_indate;
		this.check_outdate = check_outdate;
		this.gb_latitude = gb_latitude;
		this.gb_longitude = gb_longitude;
		this.gb_address = gb_address;
		this.gb_channel = gb_channel;
		requestMakeAccomodation(listener, aq, url, gb_name, gb_address, gb_for,
				gb_with, gb_seeking, check_indate, check_outdate, gb_latitude,
				gb_longitude, gb_channel, fListener);
	}

	public void requestMakeAccomodation(AqueryCallbackListener listener,
			AQuery aq, String url, String gb_name, String gb_address,
			String gb_for, String gb_with, String gb_seeking,
			String check_indate, String check_outdate, String gb_latitude,
			String gb_longitude, String gb_channel,
			final AqueryFailCallbackListener fListener) {
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

		// gb_name : 게스트하우스 name (256)
		// gb_visit1 : 방문목적 (ex:business)
		// gb_check_indate : 체크인 날짜 (yyyymmdd, 8)
		// gb_check_outdate : 체크아웃 날짜 (yyyymmdd, 8)
		// gb_latitude : 위도 (###.#######, 128)
		// gb_longitude : 경도 (###.#######, 128)
		// gb_lat_start : 위도 좌측상단 (###.#######, 128)
		// gb_lon_start : 경도 좌측상단 (###.#######, 128)
		// gb_lat_end : 위도 우측하단 (###.#######, 128)
		// gb_lon_end : 경도 우측하단 (###.#######, 128)
		// gb_channel : 채널 (ex- airbnb, 64)
		Map<String, Object> mParams = new HashMap<String, Object>();
		mParams.put("gb_name", gb_name);
		mParams.put("gb_for", gb_for);
		mParams.put("gb_with", gb_with);
		mParams.put("gb_seeking", gb_seeking);
		mParams.put("gb_check_indate", check_indate);
		mParams.put("gb_check_outdate", check_outdate);
		mParams.put("gb_latitude", gb_latitude);
		mParams.put("gb_longitude", gb_longitude);
		mParams.put("gb_channel", gb_channel);
		mParams.put("gb_address", gb_address);

		double distan = 0.000003524459795033278;

		// default 반경 3km
		distan *= 3000;
		mParams.put("gb_lat_start", (Double.parseDouble(gb_latitude) - distan)
				+ "");
		mParams.put("gb_lon_start", (Double.parseDouble(gb_longitude) - distan)
				+ "");
		mParams.put("gb_lat_end", (Double.parseDouble(gb_latitude) + distan)
				+ "");
		mParams.put("gb_lon_end", (Double.parseDouble(gb_longitude) + distan)
				+ "");

		cb.cookies(PropertyManager.getInstance().getCookie());
		// request 전송
		aq.ajax(url, mParams, JSONObject.class, cb);
	}

	@Override
	public void reRequest() {

		requestMakeAccomodation(listener, aq, url, gb_name, gb_address, gb_for,
				gb_with, gb_seeking, check_indate, check_outdate, gb_latitude,
				gb_longitude, gb_channel, fListener);
		super.reRequest();
	}
}
