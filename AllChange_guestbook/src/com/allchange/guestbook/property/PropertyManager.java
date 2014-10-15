package com.allchange.guestbook.property;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;

public class PropertyManager {
	public static final String SPLITE_STRING = "/@!#SPL#/";

	public static final String METHOD_FACEBOOK = "facebook";
	public static final String METHOD_GOOGLE = "google";

	SharedPreferences mPrefs;
	private static final String PREF_NAME = "prefs";
	SharedPreferences.Editor mEditor;

	private final static String FIELD_LOGIN_METHOD = "loginmethod";
	private String mLoginMethod = null;

	private final static String FIELD_TOKEN = "token";
	private String mToken = null;

	private final static String FIELD_GOOGLE_ID = "google_id";
	private String mGoogleId = null;

	private final static String FIELD_ID = "id";
	private String mId = null;

	private final static String FIELD_PASSWORD = "password";
	private String mPassword = null;

	private final static String FIELD_REGIST_ID = "regist_id";
	private String mRegistId = null;

	private final static String FIELD_GCM_FLAG = "gcm_flag";
	private boolean mGCMflag = true;

	private final static String FIELD_MAINSERVICE_ISRUNNING = "mainservice_isrunning";
	private boolean mMainSeriveIsRunning = true;

	private final static String FIELD_LATITUDE = "latitude";
	private String mLatitude = null;
	private final static String FIELD_LONGITUDE = "longitude";
	private String mLongitude = null;

	private final static String FIELD_DISTANCE = "distance";
	private String mDistance = null;

	private final static String FIELD_START_END_LAT_LNG = "start_end_latlng";
	private String mStartEndLatLng = null;

	private final static String FIELD_CHECK_IN = "checkin";
	private String mCheckin = null;
	private final static String FIELD_CHECK_OUT = "checkout";
	private String mCheckout = null;

	private final static String FIELD_PROPERTIES = "properties";
	private String mProperties = null;

	private final static String FIELD_DB_VERSION = "dv_version";
	private int mDBVersion = 1;

	private final static String FIELD_COOKIE_NAME = "cookie_name";
	private final static String FIELD_COOKIE_VALUE = "cookie_value";
	private String mCookieName = null;
	private String mCookieValue = null;
	private Map<String, String> mCookie = new HashMap<String, String>();

	private static PropertyManager instance;

	public static PropertyManager getInstance() {
		if (instance == null) {
			instance = new PropertyManager();
		}
		return instance;
	}

	private PropertyManager() {
		mPrefs = MyApplication.getContext().getSharedPreferences(PREF_NAME,
				Context.MODE_PRIVATE);
		mEditor = mPrefs.edit();
	}

	public int getDBVersion() {
		mDBVersion = mPrefs.getInt(FIELD_DB_VERSION, 1);
		return mDBVersion;
	}

	public void setDBVersion(int i) {
		mDBVersion = i;
		mEditor.putInt(FIELD_DB_VERSION, i);
		mEditor.commit();
	}

	public boolean getMainServiceIsRunning() {
		mMainSeriveIsRunning = mPrefs.getBoolean(FIELD_MAINSERVICE_ISRUNNING,
				false);
		return mMainSeriveIsRunning;
	}

	public void setMainServiceIsRunning(boolean b) {
		mMainSeriveIsRunning = b;
		mEditor.putBoolean(FIELD_MAINSERVICE_ISRUNNING, b);
		mEditor.commit();
	}

	public boolean getGCMSetting() {
		mGCMflag = mPrefs.getBoolean(FIELD_GCM_FLAG, true);
		return mGCMflag;
	}

	public void setGCMSetting(boolean b) {
		mGCMflag = b;
		mEditor.putBoolean(FIELD_GCM_FLAG, b);
		mEditor.commit();
	}

	public String getRadiusMeter() {
		if (mDistance == null) {
			mDistance = mPrefs.getString(FIELD_DISTANCE, "100");
		}
		return mDistance;
	}

	// 단위 m
	public void setRadiusMeter(String radius) {
		mDistance = radius;
		mEditor.putString(FIELD_DISTANCE, radius);
		mEditor.commit();
	}

	public String getProperties() {
		if (mProperties == null) {
			mProperties = mPrefs.getString(FIELD_PROPERTIES, "");
		}
		return mProperties;
	}

	// 순서
	// gb_name 0
	// gb_address 1
	// user_city 2
	// user_lastname 3
	// user_firstname 4
	// user_birthday 5
	// user_sex 6
	// user_profile_img_path 7
	// gb_channel 8
	// gb_lat_start 9
	// gb_lat_end 10
	// gb_lon_start 11
	// gb_lon_end 12
	// user_nationality 13
	// gb_for 14
	// gb_with 15
	// gb_seeking 16
	public void setProperties(String properties) {
		mProperties = properties;
		mEditor.putString(FIELD_PROPERTIES, properties);
		mEditor.commit();
	}

	public String getStartEndLatLng() {
		if (mStartEndLatLng == null) {
			mStartEndLatLng = mPrefs.getString(FIELD_START_END_LAT_LNG, "100");
		}
		return mStartEndLatLng;
	}

	public void setStartEndLatLng(String startEndLatLng) {
		mStartEndLatLng = startEndLatLng;
		mEditor.putString(FIELD_START_END_LAT_LNG, startEndLatLng);
		mEditor.commit();
	}

	public String getGoogleId() {
		if (mGoogleId == null) {
			mGoogleId = mPrefs.getString(FIELD_GOOGLE_ID, "");
		}
		return mGoogleId;
	}

	public void setGoogleId(String id) {
		mGoogleId = id;
		mEditor.putString(FIELD_GOOGLE_ID, id);
		mEditor.commit();
	}

	public String getCheckin() {
		if (mCheckin == null) {
			mCheckin = mPrefs.getString(FIELD_CHECK_IN, "");
		}
		return mCheckin;
	}

	public void setCheckin(String date) {
		mCheckin = date;
		mEditor.putString(FIELD_CHECK_IN, date);
		mEditor.commit();
	}

	public String getCheckout() {
		if (mCheckout == null) {
			mCheckout = mPrefs.getString(FIELD_CHECK_OUT, "");
		}
		return mCheckout;
	}

	public void setCheckout(String date) {
		mCheckout = date;
		mEditor.putString(FIELD_CHECK_OUT, date);
		mEditor.commit();
	}

	public String getLoginMethod() {
		if (mLoginMethod == null) {
			mLoginMethod = mPrefs.getString(FIELD_LOGIN_METHOD, "");
		}
		return mLoginMethod;
	}

	public void setLoginMethod(String method) {
		mLoginMethod = method;
		mEditor.putString(FIELD_LOGIN_METHOD, method);
		mEditor.commit();
	}

	public String getLatitude() {
		if (mLatitude == null) {
			mLatitude = mPrefs.getString(FIELD_LATITUDE, "");
		}
		return mLatitude;
	}

	public void setLatitude(String lati) {
		mLatitude = lati;
		mEditor.putString(FIELD_LATITUDE, lati);
		mEditor.commit();
	}

	public String getLongitude() {
		if (mLongitude == null) {
			mLongitude = mPrefs.getString(FIELD_LONGITUDE, "");
		}
		return mLongitude;
	}

	public void setLongitude(String longi) {
		mLongitude = longi;
		mEditor.putString(FIELD_LONGITUDE, longi);
		mEditor.commit();
	}

	public String getToken() {
		if (mToken == null) {
			mToken = mPrefs.getString(FIELD_TOKEN, "");
		}
		return mToken;
	}

	public void setToken(String token) {
		mToken = token;
		mEditor.putString(FIELD_TOKEN, token);
		mEditor.commit();
	}

	public String getId() {
		if (mId == null) {
			mId = mPrefs.getString(FIELD_ID, "");
		}
		return mId;
	}

	public void setId(String id) {
		mId = id;
		mEditor.putString(FIELD_ID, id);
		mEditor.commit();
	}

	public String getPassword() {
		if (mPassword == null) {
			mPassword = mPrefs.getString(FIELD_PASSWORD, "");
		}
		return mPassword;
	}

	public void setPassword(String password) {
		mPassword = password;
		mEditor.putString(FIELD_PASSWORD, password);
		mEditor.commit();
	}

	public String getRegistId() {
		if (mRegistId == null) {
			mRegistId = mPrefs.getString(FIELD_REGIST_ID, "");
		}
		return mRegistId;
	}

	public void setRegistId(String registId) {
		mRegistId = registId;
		mEditor.putString(FIELD_REGIST_ID, registId);
		mEditor.commit();
	}

	public Map<String, String> getCookie() {
		if (mCookieName == null && mCookieValue == null) {
			mCookieName = mPrefs.getString(FIELD_COOKIE_NAME, "");
			mCookieValue = mPrefs.getString(FIELD_COOKIE_VALUE, "");
		}
		mCookie.put(mCookieName, mCookieValue);
		return mCookie;
	}

	public void setCookie(String name, String value) {
		mCookieName = name;
		mCookieValue = value;
		mEditor.putString(FIELD_COOKIE_NAME, mCookieName);
		mEditor.putString(FIELD_COOKIE_VALUE, mCookieValue);
		mEditor.commit();
	}

}
