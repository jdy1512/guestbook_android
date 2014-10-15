package com.allchange.guestbook.marektmap;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Message;

import com.skp.openplatform.android.sdk.api.APIRequest;
import com.skp.openplatform.android.sdk.common.PlanetXSDKConstants.CONTENT_TYPE;
import com.skp.openplatform.android.sdk.common.PlanetXSDKConstants.HttpMethod;
import com.skp.openplatform.android.sdk.common.PlanetXSDKException;
import com.skp.openplatform.android.sdk.common.RequestBundle;
import com.skp.openplatform.android.sdk.common.RequestListener;
import com.skp.openplatform.android.sdk.common.ResponseMessage;

public class TWeather {

	// API Call
	APIRequest api;
	RequestBundle requestBundle;

	// Comm Data
	String URL = Const.SERVER_PUBLIC + "/weather/current/minutely";

	Map<String, Object> param;

	public TWeather() {
		// requestASync();
	}

	public void initRequestBundle(String latitude, String longitude) {
		param = new HashMap<String, Object>();
		param.put("lon", longitude);
		param.put("lat", latitude);
		param.put("version", "1");
		param.put("appKey", "29de418b-27ea-335d-bd96-ce5313bd3fd8");
		
		String payload = "{\"weather\"minutely\":{\"name\":\"New Tag Here\"}}";

		requestBundle = new RequestBundle();
		requestBundle.setUrl(URL);
		requestBundle.setParameters(param);
		requestBundle.setPayload(payload);
		requestBundle.setHttpMethod(HttpMethod.GET);
		requestBundle.setResponseType(CONTENT_TYPE.JSON);
		requestBundle.setRequestType(CONTENT_TYPE.JSON);
	}

	public void clearResult() {
		setResult("");
	}

	public void setResult(String result) {
		// tvResult.setText(result);
	}

	public void requestASync(String latitude, String longitude) {
		api = new APIRequest();
		initRequestBundle(latitude, longitude);

		try {
			api.request(requestBundle, reqListener);
		} catch (PlanetXSDKException e) {
			e.printStackTrace();
		}
	}

	String hndResult = "";

	Handler msgHandler = new Handler() {
		public void dispatchMessage(Message msg) {
			try {
				JSONObject jsonObj = new JSONObject(hndResult.toString());
				if (mListener != null) {
					mListener.onGetWeather(jsonObj);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

		};
	};

	RequestListener reqListener = new RequestListener() {

		@Override
		public void onPlanetSDKException(PlanetXSDKException e) {
			hndResult = e.toString();
			msgHandler.sendEmptyMessage(0);
		}

		@Override
		public void onComplete(ResponseMessage result) {
			hndResult = result.toString();
			msgHandler.sendEmptyMessage(0);
		}
	};

	public interface OnGetWetherListener {
		public void onGetWeather(JSONObject result);
	}

	OnGetWetherListener mListener;

	public void setOnGetWetherListener(OnGetWetherListener listener) {
		mListener = listener;
	}
}
