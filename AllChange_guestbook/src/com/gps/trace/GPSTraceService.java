package com.gps.trace;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import com.allchange.guestbook.property.PropertyManager;
import com.allchange.guestbook.url.Url;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

public class GPSTraceService extends Service {
	private static final int TIMER = 1000;
	private static final int DISTANCE = 0;
	private static final int LATLNG_LEN = 237;
	private static final String TAG = "GPSTraceService";
	private boolean running;
	private boolean sleep;

	private LocationManager locationManager;
	private Criteria criteria;
	private String provider;
	private Handler handler;

	private SharedPreferences mPref;
	private Editor mPrefEdit;
	private final String CHECK_HOUR_TIMER = "CheckHourTimer";
	private final String SEND_TO_SERVER = "SendToServer";
	private final String WAIT_SERVER = "WaitServer";
	private final String GPS_LATITUDE = "GPSTraceLatitude";
	private final String GPS_LONGITUDE = "GPSTraceLongitude";
	private String[] lat_list, lon_list;
	private String defaultLatlng;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.e(TAG, "onCreate");
		running = true;
		handler = new Handler();

		// LocationManager 생성
		locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		criteria = new Criteria();
		provider = null;

		// Preference
		mPref = PreferenceManager.getDefaultSharedPreferences(this);
		mPrefEdit = mPref.edit();

		defaultLatlng = "";
		for (int i = 0; i < LATLNG_LEN; i++) {
			defaultLatlng += "0";
			if (i != LATLNG_LEN - 1) {
				defaultLatlng += ",";
			}
		}

		startWakeUpThread();
	}// oncreate

	public void checkMyLocation() {
		provider = getBestProvider();
		// gps off이면 network통해서 받아오도록..
		if (provider == null) {
			provider = getNetworkProvider();
		}
		// gps 제공자 없음
		if (provider == null) {
			return;
		}
		// TIMER 밀리초마다 DISTANCE 이상 이동시 GPS정보를 업데이트하는 리스너 등록
		locationManager.requestLocationUpdates(provider, TIMER, DISTANCE,
				mListener);
	}

	public String getNetworkProvider() {
		return LocationManager.NETWORK_PROVIDER;
	}

	public String getBestProvider() {
		return locationManager.getBestProvider(criteria, true);
	}

	public LocationListener mListener = new LocationListener() {
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onLocationChanged(Location location) {
			showGPS(location);
		}
	};

	public void GPSToPreference(int index, Location location) {
		lat_list = mPref.getString(GPS_LATITUDE, defaultLatlng).split(",");
		lon_list = mPref.getString(GPS_LONGITUDE, defaultLatlng).split(",");
		lat_list[index] = "" + location.getLatitude();
		lon_list[index] = "" + location.getLongitude();
		String latitude = "";
		String longitude = "";
		for (int i = 0; i < LATLNG_LEN; i++) {
			latitude += lat_list[i];
			longitude += lon_list[i];
			if (i != LATLNG_LEN - 1) {
				latitude += ",";
				longitude += ",";
			}
		}
		mPrefEdit.putString(GPS_LATITUDE, latitude);
		mPrefEdit.putString(GPS_LONGITUDE, longitude);
		mPrefEdit.commit();
	}

	public void sendToServer() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("email_id", PropertyManager.getInstance().getId());
		params.put("latitude", mPref.getString(GPS_LATITUDE, defaultLatlng));
		params.put("longitude", mPref.getString(GPS_LONGITUDE, defaultLatlng));
		new AQuery(this).ajax(Url.SERVER_GPS_TRACE, params, JSONObject.class,
				new AjaxCallback<JSONObject>() {
					@Override
					public void callback(String url, JSONObject json,
							AjaxStatus status) {
						if (json != null) {
							mPrefEdit.putString(GPS_LATITUDE, defaultLatlng);
							mPrefEdit.putString(GPS_LONGITUDE, defaultLatlng);
							mPrefEdit.putBoolean(SEND_TO_SERVER, false);
							mPrefEdit.commit();
						}
					}
				});
	}

	// Latitude, Longitude를 출력
	public void showGPS(Location location) {
		String dtime = "";
		try {
			// data mining
			dtime = Dtime.instance().getGMTDateTime().substring(8, 11);
			if (dtime.charAt(0) == '0') {
				dtime = dtime.substring(1);
				if (dtime.charAt(0) == '0') {
					dtime = dtime.substring(1);
				}
			}
			GPSToPreference(Integer.valueOf(dtime), location);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} finally {
			locationManager.removeUpdates(mListener);

			if ("0".equals(dtime)) {
				mPrefEdit.putBoolean(SEND_TO_SERVER, true);
				mPrefEdit.commit();
			}
		}
	}

	public void stop() {
		synchronized (this) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void awake() {
		synchronized (this) {
			notify();
		}
	}

	public void startWakeUpThread() {
		new Thread(new Runnable() {
			public void run() {
				PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
				while (running) {
					// 체크아웃했을시 running = false;
					try {
						Thread.sleep(1000);
						if (!powerManager.isScreenOn() && !sleep) {
							sleep = !sleep;
//							Log.d(TAG, "isScreenSleep");
							registerRestartAlarm(true);
						} else if (powerManager.isScreenOn() && sleep) {
							sleep = !sleep;
//							Log.d(TAG, "isScreenOn");
							registerRestartAlarm(false);
						} else {
							if (mPref.getBoolean(SEND_TO_SERVER, false)) {
								Log.e(TAG, "sending latlng to server");
								if (!mPref.getBoolean(WAIT_SERVER, false)) {
									mPrefEdit.putBoolean(WAIT_SERVER, true);
									mPrefEdit.commit();
									handler.postDelayed(new Runnable() {
										public void run() {
											mPrefEdit.putBoolean(WAIT_SERVER,
													false);
											mPrefEdit.commit();
										}
									}, 1000 * 10);
									sendToServer();
								}
							} else {
								String dtime = Dtime.instance()
										.getGMTDateTime().substring(8, 11);
								if (!dtime.equals(mPref.getString(
										CHECK_HOUR_TIMER, ""))) {
									mPrefEdit
											.putString(CHECK_HOUR_TIMER, dtime);
									mPrefEdit.commit();
									handler.post(new Runnable() {
										public void run() {
											checkMyLocation();
										}
									});
								}
							}
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	public void registerRestartAlarm(boolean isOn) {
		Log.e(TAG, "registerRestartAlarm");
		Intent intent = new Intent(GPSTraceService.this, RestartReceiver.class);
		intent.setAction(RestartReceiver.ACTION_RESTART_GPSTrace);
		PendingIntent sender = PendingIntent.getBroadcast(
				getApplicationContext(), 0, intent, 0);

		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		if (isOn) {
			am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
					SystemClock.elapsedRealtime() + 1000, 10000, sender);
		} else {
			am.cancel(sender);
		}
	}
}