package com.allchange.guestbook.setting.map;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.allchange.guestbook.R;
import com.allchange.guestbook.aquery.AndroidQuery;
import com.allchange.guestbook.aquery.get.map.members.AqueryGetMapMembersCallbackListener;
import com.allchange.guestbook.aquery.update.map.bounds.AqueryUpdateBoundsCallbackListener;
import com.allchange.guestbook.booking.search.map.ParsingData;
import com.allchange.guestbook.main.feedandsearch.FragmentFeedAndSearch;
import com.allchange.guestbook.property.PropertyManager;
import com.allchange.guestbook.setting.map.PinchGestureView.OnChangedRangeListener;
import com.allchange.guestbook.url.Url;
import com.androidquery.callback.AjaxStatus;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class SettingMapActivity extends FragmentActivity implements
		AqueryGetMapMembersCallbackListener,
		AqueryUpdateBoundsCallbackListener, OnChangedRangeListener {

	ArrayList<String> searchResult = new ArrayList<String>();

	public static final String TAG = "SettingMapActivity";
	private GoogleMap googleMap;
	private LatLng notEditing_LatLng;
	private PinchGestureView iv_Circle;
	private TextView tv_Radius;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setTitle(
				getResources().getString(R.string.actionbar_title_setting_map));
		setContentView(R.layout.setting_map);
		setUpMapIfNeed();
		notEditing_LatLng = new LatLng(
				Double.parseDouble(PropertyManager.getInstance().getLatitude()),
				Double.parseDouble(PropertyManager.getInstance().getLongitude()));
		// mLatLng = new LatLng(Double.parseDouble(PropertyManager.getInstance()
		// .getLatitude()), Double.parseDouble(PropertyManager
		// .getInstance().getLongitude()));
		setMarkUp(notEditing_LatLng, null);
		iv_Circle = (PinchGestureView) findViewById(R.id.setting_circle_image);
		iv_Circle.setGoogleMap(googleMap, notEditing_LatLng, Double
				.parseDouble(PropertyManager.getInstance().getRadiusMeter()));
		iv_Circle.setOnChangedRangeListener(this);

		tv_Radius = (TextView) findViewById(R.id.setting_map_tv_radius);
		tv_Radius
				.setText((((float) Math.round((Double
						.parseDouble(PropertyManager.getInstance()
								.getRadiusMeter()) / 1000)) * 100) / 100 + "km"));

		Button btn = (Button) findViewById(R.id.setting_map_btn);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				request_flag = true;
				SettingMapActivity.this.finish();
			}
		});
		onStartSettingMap();

	}

	/**
	 * if there is editing, update to server
	 */
	@Override
	protected void onDestroy() {
		try {

			if (request_flag) {
				// 참이면 변경사항 적용하기
				float max_location = 0;

				for (float distance : circleResolver.keySet()) {
					if (max_location < distance
							|| (circleResolver.get(distance).getCenter().longitude > notEditing_LatLng.longitude)) {
//						Log.e(TAG, "변경하기 : " + "longi distance : " + distance);
						max_location = distance;
					}
				}
				double maxLongitude = maxGapLongitude(max_location);

				for (float distance : circleResolver.keySet()) {
					if (max_location < distance
							|| (circleResolver.get(distance).getCenter().latitude > notEditing_LatLng.latitude)) {
//						Log.e(TAG, "변경하기 : " + "lati distance : " + distance);
						max_location = distance;
					}
				}
				double maxLatitude = maxGapLatitude(max_location);

				double gb_lat_start = notEditing_LatLng.latitude - maxLatitude;
				double gb_lon_start = notEditing_LatLng.longitude
						- maxLongitude;
				double gb_lat_end = notEditing_LatLng.latitude + maxLatitude;
				double gb_lon_end = notEditing_LatLng.longitude + maxLongitude;
				Log.e(TAG, gb_lat_start + ", " + gb_lon_start + ", "
						+ gb_lat_end + ", " + gb_lon_end);

				PropertyManager.getInstance().setStartEndLatLng(
						"" + gb_lat_start + "/" + gb_lon_start + "/"
								+ gb_lat_end + "/" + gb_lon_end);
				AndroidQuery.getInstance().requestUpdateBoudns(
						SettingMapActivity.this, Url.SERVER_UPDATE_MY_BOUNDS,
						gb_lat_start + "", gb_lon_start + "", gb_lat_end + "",
						gb_lon_end + "");
				PropertyManager.getInstance().setRadiusMeter(
						"" + mRadiusForSaving);
				Log.e(TAG, "dis : "
						+ PropertyManager.getInstance().getRadiusMeter());

				FragmentFeedAndSearch.tv_bounds
						.setText(((float) Math.round((Double
								.parseDouble(PropertyManager.getInstance()
										.getRadiusMeter()) / 10)) / 100 + "km"));
			} else {
				// 아니면 원래 설정값으로 변경
				String[] startEndLatLng = PropertyManager.getInstance()
						.getStartEndLatLng().split("/");

				AndroidQuery.getInstance().requestUpdateBoudns(
						SettingMapActivity.this, Url.SERVER_UPDATE_MY_BOUNDS,
						startEndLatLng[0], startEndLatLng[1],
						startEndLatLng[2], startEndLatLng[3]);

				// Double.parseDouble(PropertyManager.getInstance().getDistance());
			}
		} catch (Exception e) {
		}
		setResult(0);
		this.finish();
		super.onDestroy();
	}

	/***
	 * 
	 * @param max_location
	 *            Based on the latitude with the most distant point.
	 * @return
	 * @throws Exception
	 */
	private double maxGapLatitude(float max_location) throws Exception {
		String TAG = this.TAG + " maxGapLatitude";
		Log.e(TAG, "maxGapLatitude non : " + max_location);
		Circle circle = circleResolver.get(max_location);
		LatLng centerLatLng = new LatLng(circle.getCenter().latitude,
				circle.getCenter().longitude);
		LatLng latLng = new LatLng(centerLatLng.latitude,
				notEditing_LatLng.longitude);
		float[] result = new float[1];
		Location.distanceBetween(notEditing_LatLng.latitude,
				notEditing_LatLng.longitude, latLng.latitude, latLng.longitude,
				result);

		Log.e(TAG, "maxGapLatitude result : " + result[0]);

		double dLatitude = latLng.latitude - notEditing_LatLng.latitude;

		double maxLatitude = (mRadiusForSaving / result[0]) * dLatitude;
		latLng = new LatLng((notEditing_LatLng.latitude + maxLatitude),
				notEditing_LatLng.longitude);
		Log.e(TAG, "new latLng : " + (notEditing_LatLng.latitude + maxLatitude)
				+ "," + notEditing_LatLng.longitude);
		Log.e(TAG, "maxGapLatitude result : " + dLatitude + ", " + maxLatitude);
		Location.distanceBetween(notEditing_LatLng.latitude,
				notEditing_LatLng.longitude, latLng.latitude, latLng.longitude,
				result);
		Log.e(TAG, "maxGapLatitude new result : " + result[0]);
		if (maxLatitude < 0) {
			maxLatitude *= -1;
		}
		return maxLatitude;
	}

	/***
	 * 
	 * @param max_location
	 *            Based on the longitude with the most distant point.
	 * @return
	 * @throws Exception
	 */
	private double maxGapLongitude(float max_location) throws Exception {
		String TAG = this.TAG + " maxGapLongitude";
		Log.e(TAG, "non latLng : " + notEditing_LatLng.latitude + ","
				+ notEditing_LatLng.longitude);
		Log.e(TAG, "non : " + max_location);
		Circle circle = circleResolver.get(max_location);
		Log.e(TAG,
				"circle : " + circle.getCenter().latitude + ","
						+ circle.getCenter().longitude);
		LatLng centerLatLng = new LatLng(circle.getCenter().latitude,
				circle.getCenter().longitude);

		Log.e(TAG,
				"latLng : " + notEditing_LatLng.latitude + ","
						+ circle.getCenter().longitude);
		LatLng latLng = new LatLng(notEditing_LatLng.latitude,
				centerLatLng.longitude);
		float[] result = new float[1];
		Location.distanceBetween(notEditing_LatLng.latitude,
				notEditing_LatLng.longitude, latLng.latitude, latLng.longitude,
				result);

		Log.e(TAG, "result : " + result[0]);

		double dLongitude = latLng.longitude - notEditing_LatLng.longitude;

		double maxLongitude = (mRadiusForSaving / result[0]) * dLongitude;

		latLng = new LatLng(notEditing_LatLng.latitude,
				(notEditing_LatLng.longitude + maxLongitude));
		Log.e(TAG, "new latLng : " + notEditing_LatLng.latitude + ","
				+ (notEditing_LatLng.longitude + maxLongitude));
		Log.e(TAG, "result : " + dLongitude + ", " + maxLongitude);
		Location.distanceBetween(notEditing_LatLng.latitude,
				notEditing_LatLng.longitude, latLng.latitude, latLng.longitude,
				result);
		Log.e(TAG, "new result : " + result[0]);

		Log.e(TAG, "maxLongitude - " + maxLongitude);
		if (maxLongitude < 0) {
			maxLongitude *= -1;
		}

		return maxLongitude;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	private void setUpMapIfNeed() {
		if (googleMap == null) {
			SupportMapFragment f = (SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.setting_Googlemap);
			googleMap = f.getMap();
			if (googleMap != null) {
				googleMap.getUiSettings().setZoomControlsEnabled(false);
				// googleMap.setOnInfoWindowClickListener(this);
				// googleMap.setInfoWindowAdapter(new MyInfoWindow(this));
				// googleMap.setOnCameraChangeListener(this);
			}
		}
	}

	/***
	 * 
	 * @param latlng
	 *            to draw a point that is other person
	 * @param mData
	 */
	private void setMarkUp(LatLng latlng, ParsingData mData) {
		MarkerOptions options = new MarkerOptions();

		if (mData == null) {

			// googleMap.addCircle(new CircleOptions().center(latlng).radius(60)
			// .fillColor(getResources().getColor(R.color.app_color)));

			googleMap.addCircle(new CircleOptions().center(latlng).radius(40)
					.strokeColor(getResources().getColor(R.color.app_color))
					.strokeWidth(4.0f)
					.fillColor(getResources().getColor(R.color.app_color)));

			// Bitmap bmp = BitmapFactory.decodeResource(getResources(),
			// R.drawable.selected_marker);
			// options.icon(BitmapDescriptorFactory.fromBitmap(bmp));
			// options.draggable(false);
			// options.title("");
			// options.snippet("");
			//
			// CameraPosition position = new CameraPosition.Builder()
			// .target((latlng)).zoom(15).build();
			// options.position(position.target);
			// options.anchor(0.5f, 1.0f);
			//
			// Marker marker = googleMap.addMarker(options);

			CameraPosition.Builder builder = new CameraPosition.Builder();
			builder.target(latlng);
			builder.zoom(12.8f);
			builder.bearing(0);
			builder.tilt(0);
			CameraUpdate update = CameraUpdateFactory.newCameraPosition(builder
					.build());
			googleMap.moveCamera(update);
			googleMap.animateCamera(update);

		}
	}

	public interface OnSuccessUpdateRageListener {
		public void onSuccessUpdateRage();
	}

	static OnSuccessUpdateRageListener mListener;

	public static void setOnSuccessUpdateRageListener(
			OnSuccessUpdateRageListener listener) {
		mListener = listener;
	}

	// LatLng mLatLng;
	boolean request_flag = false;
	int cnt_request = 0;

	@Override
	public void AqueryUpdateBoundsCallback(String url, JSONObject json,
			AjaxStatus status) {
		if (json != null) {
			try {
				if (json.getString("result").equals("success")) {
					if (cnt_request == 0) {
						AndroidQuery.getInstance().requestGetMapMembers(this,
								Url.SERVER_GET_MAP_MEMBERS);
						// mRadiusForSaving = Float.parseFloat(PropertyManager
						// .getInstance().getDistance()) / 1000;
						cnt_request++;
					} else {
						if (mListener != null) {
							mListener.onSuccessUpdateRage();
						}
					}
				} else if (json.getString("result").equals("fail")) {
					Log.d(TAG + " UpdateBounds", json.getString("result_msg"));
				}
			} catch (JSONException e) {
				Log.e(TAG + " UpdateBounds", "parsing error");
				e.printStackTrace();
			}
		} else {
			Log.e(TAG + " UpdateBounds", "json is null");
			Log.e(TAG + " UpdateBounds", status.getMessage());
			Log.e(TAG + " UpdateBounds", status.getRedirect());
		}
	}

	private ArrayList<LocationData> total_LocationData = new ArrayList<LocationData>();
	private HashMap<Float, Circle> circleResolver = new HashMap<Float, Circle>();

	// private ArrayList<circleData> circleResolver = new
	// ArrayList<circleData>();

	/**
	 * The pointer goes out of scope, not drawn.
	 */
	private void compareLocations(double radius) {
		if (total_LocationData == null) {
			return;
		}

		for (Float key : circleResolver.keySet()) {
			circleResolver.get(key).remove();
		}
		circleResolver.clear();
		Log.e(TAG, "total_LocationData : " + total_LocationData.size());
		for (int i = 0; i < total_LocationData.size(); i++) {
			float[] result = new float[1];
			Location.distanceBetween(notEditing_LatLng.latitude,
					notEditing_LatLng.longitude,
					Double.parseDouble(total_LocationData.get(i).latitude),
					Double.parseDouble(total_LocationData.get(i).longitude),
					result);

			if (result[0] == 0) {
				// Log.e(TAG, "result[0] == 0");
				setMarkUp(notEditing_LatLng, null);
			} else if (result[0] < radius) {
				// Log.e(TAG, "result[0] == 0 else");

				// Log.e(TAG, "cal : " + result[0] + "  mRadiusForSaving : "
				// + (mRadiusForSaving * 1000));
				Circle mCircle = googleMap
						.addCircle(new CircleOptions()
								.center(new LatLng(
										Double.parseDouble(total_LocationData
												.get(i).latitude), Double
												.parseDouble(total_LocationData
														.get(i).longitude)))
								.radius(30)
								.strokeColor(
										getResources().getColor(
												R.color.map_circle_fill))
								.strokeWidth(4.0f)
								.fillColor(
										getResources().getColor(
												R.color.map_circle_fill)));

				circleResolver.put(result[0], mCircle);
			}
		}
	}

	@Override
	public void AqueryGetMapMembersCallback(String url, JSONObject json,
			AjaxStatus status) {
		if (json != null) {
			try {
				if (json.getString("result").equals("success")) {

					JSONArray ja = json.getJSONArray("location");
					for (int i = 0; i < ja.length(); i++) {
						LocationData data = new LocationData();
						data.email_id = ja.getJSONObject(i).getString(
								"email_id");
						data.latitude = ja.getJSONObject(i).getString(
								"latitude");
						data.longitude = ja.getJSONObject(i).getString(
								"longitude");

						// Log.e(TAG, + data.latitude + ", "
						// + data.longitude);
						total_LocationData.add(data);

					}
					compareLocations(3000);
					setMarkUp(notEditing_LatLng, null);
					// Toast.makeText(this,
					// "get members successly , " + ja.length(),
					// Toast.LENGTH_SHORT).show();
				} else if (json.getString("result").equals("fail")) {
					Log.d(TAG + " GetMapMembers", json.getString("result_msg"));
				}
			} catch (JSONException e) {
				Log.e(TAG + " GetMapMembers", "parsing error");
				e.printStackTrace();
			}
		} else {
			Log.e(TAG + " GetMapMembers", "json is null");
			Log.e(TAG + " GetMapMembers", status.getMessage());
			Log.e(TAG + " GetMapMembers", status.getRedirect());
		}
	}

	private double mRadiusForSaving = 0;
	Circle mCircle;

	/***
	 * @param range
	 */
	@Override
	public void onChangedTripPalTap(double range) {
		// TODO Auto-generated method stub
		// mRadiusForSaving = (float) Math.round(range * (2.4f) * 100) /s 100;
		// tv_Radius.setText((mRadiusForSaving * 2) + "km");
		mRadiusForSaving = range;
		tv_Radius
				.setText((float) Math.round((range / 1000) * 100) / 100 + "km");

	}

	/***
	 * default setup range
	 */
	public void onStartSettingMap() {
		double distan = 0.003524459795033278;
		double gb_lat_start = notEditing_LatLng.latitude - (distan * 3);
		double gb_lon_start = notEditing_LatLng.longitude - (distan * 3);
		double gb_lat_end = notEditing_LatLng.latitude + (distan * 3);
		double gb_lon_end = notEditing_LatLng.longitude + (distan * 3);
		// Log.e(TAG, gb_lat_start + ", " + gb_lon_start + ", " + gb_lat_end
		// + ", " + gb_lon_end);
		AndroidQuery.getInstance().requestUpdateBoudns(SettingMapActivity.this,
				Url.SERVER_UPDATE_MY_BOUNDS, gb_lat_start + "",
				gb_lon_start + "", gb_lat_end + "", gb_lon_end + "");

		// mRadiusForSaving = Float.parseFloat(PropertyManager.getInstance()
		// .getDistance()) / 1000;
		// Log.e(TAG, "mRadiusForSaving " + mRadiusForSaving);
	}

	/**
	 * after pinch
	 */
	@Override
	public void onFinishRange(double range) {
		compareLocations(range);
	}

}
