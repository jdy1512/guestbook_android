package com.allchange.guestbook.booking.search.map;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnActionExpandListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;

import com.allchange.guestbook.R;
import com.allchange.guestbook.booking.search.list.OnSetAccamoListener;
import com.allchange.guestbook.parser.ParserAirbnb;
import com.allchange.guestbook.parser.ParserAirbnb.ParsingCallbackListener;
import com.allchange.guestbook.property.PropertyManager;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class TrippalMapFragment extends Fragment implements
		GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener,
		GoogleMap.OnMapClickListener, ParsingCallbackListener,
		OnItemClickListener, OnCameraChangeListener {
	public SearchView mSearchView;
	public MenuItem searchItem;
	public static InputMethodManager imm;
	public String searchQuery = "";

	private ProgressBar indicator;

	private ListView autoSearchList;
	ArrayList<String> searchResult = new ArrayList<String>();

	public static final String TAG = "TrippalMapFragment";
	static final LatLng TutorialsPoint = new LatLng(21, 57);
	private GoogleMap googleMap;
	private boolean flag_catchMapMoving = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		setHasOptionsMenu(true);
		Log.e(TAG, "onCreateView");
		View v = inflater.inflate(R.layout.main_map, container, false);
		setUpMapIfNeed();

		indicator = (ProgressBar) v.findViewById(R.id.main_map_indicator);
		autoSearchList = (ListView) v.findViewById(R.id.main_map_listView);

		SearchMapAdapter adapter = new SearchMapAdapter(getActivity(),
				new ArrayList<String>());
		// 리스트뷰에 어댑터 연결
		autoSearchList.setAdapter(adapter);
		autoSearchList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				autoSearchList.setVisibility(View.GONE);
				RequestData data = new RequestData();
				data.word = searchResult.get(position);

				// set Textfiled to click an item string.
				// mSearchView.setQuery(searchResult.get(position), false);
				// autoSearchList.setVisibility(View.GONE);

				data.byMap = false;
				flag_catchMapMoving = false;
				indicator.setVisibility(View.VISIBLE);
				ParserAirbnb.getInstance().getList(data);
			}
		});

		ParserAirbnb.getInstance().setParsingListener(this);
		try {
			setMarkUp(
					new LatLng(Double.parseDouble(PropertyManager.getInstance()
							.getLatitude()), Double.parseDouble(PropertyManager
							.getInstance().getLongitude())), null);
		} catch (NumberFormatException e) {
		}

		// TODO 으흠 이걸 어찌..
		// TripPalMainActivity activity = (TripPalMainActivity) getActivity();
		// activity.setChangedTripPalTapListener(this);

		return v;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	public void onItemClick(AdapterView<?> adapterView, View view,
			int position, long id) {
		String str = (String) adapterView.getItemAtPosition(position);
		Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
	}

	private void setUpMapIfNeed() {
		if (googleMap == null) {
			SupportMapFragment f = (SupportMapFragment) getActivity()
					.getSupportFragmentManager().findFragmentById(
							R.id.main_Googlemap);
			googleMap = f.getMap();
			if (googleMap != null) {
				googleMap.setOnInfoWindowClickListener(this);
				googleMap.setInfoWindowAdapter(new MyInfoWindow(getActivity()));
				googleMap.setOnCameraChangeListener(this);
			}
		}
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		// MyData d = mValueResolver.get(marker.getId());
		marker.showInfoWindow();
		return true;
	}

	class MyInfoWindow implements InfoWindowAdapter {

		String separator = "////////";
		View infoView;
		TextView titleView;
		TextView snippetView;
		ImageView iv;

		public MyInfoWindow(Context context) {
			infoView = LayoutInflater.from(context).inflate(
					R.layout.info_window_layout, null);
			titleView = (TextView) infoView.findViewById(R.id.map_title);
			snippetView = (TextView) infoView.findViewById(R.id.map_snippet);
			iv = (ImageView) infoView.findViewById(R.id.map_imageView);
		}

		@Override
		public View getInfoContents(Marker marker) {
			return null;
		}

		@Override
		public View getInfoWindow(Marker marker) {
			// MyData data = mValueResolver.get(marker.getId());
			if (marker.getTitle().equals("")) {
				// 자신이 묵고 잇는 곳이면
				return null;
			}
			titleView.setText(marker.getTitle());
			// strArr[0]address strArr[1]image_url
			String[] strArr = marker.getSnippet().split(separator);

			snippetView.setText(strArr[0]);

			try {

				iv.setImageBitmap(MapImageCacheStore.getInstance().get(
						strArr[1].trim()));
			} catch (IndexOutOfBoundsException e) {
				Log.e(TAG, "IndexOutOfBoundsException");
			}

			return infoView;
		}

	}

	@Override
	public void onInfoWindowClick(Marker marker) {
		// ParsingData data = mValueResolver.get(marker.getId());
		// DialogMapCalender calen_dialog = new DialogMapCalender();
		// calen_dialog.setOnConfirmListener((BookingFrameActivity)
		// getActivity());
		// calen_dialog.setInfoData(data);
		// calen_dialog.show(getFragmentManager(), TAG);

		ParsingData data = mValueResolver.get(marker.getId());
		if (mListener != null) {
			mListener.onSetAccamodation(data);
		}
	}

	Circle mCircle, mPreCircle;
	Marker mSelectedMarker;

	private void setMarkUp(LatLng latlng, ParsingData mData) {
		String separator = "////////";
		MarkerOptions options = new MarkerOptions();

		if (mData == null) {

			Bitmap bmp = BitmapFactory.decodeResource(getResources(),
					R.drawable.selected_marker);
			options.icon(BitmapDescriptorFactory.fromBitmap(bmp));
			options.draggable(false);
			options.title("");
			options.snippet("");

			CameraPosition position = new CameraPosition.Builder()
					.target((latlng)).zoom(15).build();
			options.position(position.target);
			options.anchor(0.5f, 1.0f);

			mSelectedMarker = googleMap.addMarker(options);

			CameraPosition.Builder builder = new CameraPosition.Builder();
			builder.target(latlng);
			builder.zoom(15.0f);
			builder.bearing(0);
			builder.tilt(0);
			CameraUpdate update = CameraUpdateFactory.newCameraPosition(builder
					.build());
			googleMap.moveCamera(update);
			googleMap.animateCamera(update);

			mCircle = googleMap
					.addCircle(new CircleOptions()
							.center(latlng)
							.radius(Double.parseDouble(PropertyManager
									.getInstance().getRadiusMeter()))
							.strokeColor(Color.RED)
							.strokeWidth(4.0f)
							.fillColor(
									getResources().getColor(
											R.color.map_circle_fill)));
			if (mPreCircle != null) {
				mPreCircle.remove();
			}
			mPreCircle = mCircle;
		} else {
			try {
				if (Double.parseDouble(PropertyManager.getInstance()
						.getLatitude()) == latlng.latitude
						&& Double.parseDouble(PropertyManager.getInstance()
								.getLongitude()) == latlng.longitude) {
					return;
				}
			} catch (NumberFormatException e) {
			}

			// IconGenerator icnGenerator = new IconGenerator(getActivity());
			// icnGenerator.setTextAppearance(R.style.iconGenText);
			// Bitmap bmp = icnGenerator.makeIcon(mData.title);
			Bitmap bmp = BitmapFactory.decodeResource(getResources(),
					R.drawable.mark);

			// the
			// text
			// color, stroke width, size
			options.icon(BitmapDescriptorFactory.fromBitmap(bmp));
			options.draggable(false);
			options.title(mData.title);
			options.snippet(mData.address + separator + mData.image_url);

			CameraPosition position = new CameraPosition.Builder()
					.target((latlng)).zoom(15).build();
			options.position(position.target);
			options.anchor(0.5f, 1.0f);

			Marker marker = googleMap.addMarker(options);

			// 이미지 미리가져오기
			AjaxCallback<Bitmap> bc = new AjaxCallback<Bitmap>() {
				@Override
				public void callback(String url, Bitmap bitmap,
						AjaxStatus status) {
					if (bitmap != null) {
						MapImageCacheStore.getInstance().put(url, bitmap);
					}
				}
			};

			if (MapImageCacheStore.getInstance().get(mData.image_url.trim()) != null) {
			} else {
				AQuery aquery = new AQuery(getActivity());
				bc.cookies(PropertyManager.getInstance().getCookie());
				aquery.ajax(mData.image_url.trim(), Bitmap.class, bc);
			}

			if (mValueResolver.size() > 100) {
				for (String key : mMarkerResolver.keySet()) {
					mMarkerResolver.get(key).remove();
				}
				mValueResolver.clear();
				mMarkerResolver.clear();
				// googleMap.clear();
			}

			mValueResolver.put(marker.getId(), mData);
			mMarkerResolver.put(mData.title, marker);
		}
		// /////////////////////////////////////////////맵 현재 포지션을 마크업위치로
		// CameraPosition.Builder builder = new CameraPosition.Builder();
		// builder.target(latlng);
		// builder.zoom(10.5f);
		// builder.bearing(0);
		// builder.tilt(0);
		// CameraUpdate update = CameraUpdateFactory.newCameraPosition(builder
		// .build());
		// googleMap.moveCamera(update);
		// googleMap.animateCamera(update);
	}

	HashMap<String, ParsingData> mValueResolver = new HashMap<String, ParsingData>();
	HashMap<String, Marker> mMarkerResolver = new HashMap<String, Marker>();

	@Override
	public void parsingCallback(HashMap<String, ParsingData> data,
			String resultCount) {

		// btn_list.setText("cnt : " + resultCount);
		double lati_max = 0, longi_max = 0, lati_min = 0, longi_min = 0;

		if (data.size() < 1) {
			Toast.makeText(getActivity(),
					getResources().getString(R.string.no_search_data),
					Toast.LENGTH_SHORT).show();
			indicator.setVisibility(View.GONE);
			return;
		}

		for (String key : data.keySet()) {
			if (mMarkerResolver.get(key) == null) {
				// setMarkers(data);
				double lati = Double.parseDouble(data.get(key).latitude);
				double longi = Double.parseDouble(data.get(key).longitude);
				setMarkUp(new LatLng(lati, longi), data.get(key));
				if (!flag_catchMapMoving) {
					if (lati_max < lati || lati_max == 0) {
						lati_max = lati;
					}
					if (longi_max < longi || longi_max == 0) {
						longi_max = longi;
					}
					if (lati_min > lati || lati_min == 0) {
						lati_min = lati;
					}
					if (longi_min > longi || longi_min == 0) {
						longi_min = longi;
					}
				}
				// Log.e(TAG, "key marker : " + key);
			}
		}

		Log.e(TAG, lati_max + ", " + longi_max + ", " + lati_min + ", "
				+ longi_min);

		// 검색 해당위치로 카메라를 이동시킨다.
		if (!flag_catchMapMoving) {
			CameraPosition.Builder builder = new CameraPosition.Builder();
			builder.target(new LatLng((lati_max + lati_min) / 2,
					(longi_max + longi_min) / 2));
			View v = getActivity().getSupportFragmentManager()
					.findFragmentById(R.id.main_Googlemap).getView();
			builder.zoom(getBoundsZoomLevel(new LatLng(lati_max, longi_max),
					new LatLng(lati_min, longi_min), v.getWidth(),
					v.getHeight()));
			// builder.zoom(15);
			// Log.e(TAG, "w : " + v.getWidth() + "h : " + v.getHeight());
			builder.bearing(0);
			builder.tilt(0);
			CameraUpdate update = CameraUpdateFactory.newCameraPosition(builder
					.build());
			googleMap.moveCamera(update);
			googleMap.animateCamera(update);

			Handler mHandler = new Handler();
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					flag_catchMapMoving = true;
				}
			}, 2000);
		}

		indicator.setVisibility(View.GONE);

	}

	public static int getBoundsZoomLevel(LatLng northeast, LatLng southwest,
			int width, int height) {
		final int GLOBE_WIDTH = 256; // a constant in Google's map projection
		final int ZOOM_MAX = 21;
		double latFraction = (latRad(northeast.latitude) - latRad(southwest.latitude))
				/ Math.PI;
		double lngDiff = northeast.longitude - southwest.longitude;
		double lngFraction = ((lngDiff < 0) ? (lngDiff + 360) : lngDiff) / 360;
		double latZoom = zoom(height, GLOBE_WIDTH, latFraction);
		double lngZoom = zoom(width, GLOBE_WIDTH, lngFraction);
		// Log.e(TAG, "latZoom: " + latZoom + ", lngZoom: " + lngZoom);
		double zoom = Math.min(Math.min(latZoom, lngZoom), ZOOM_MAX);
		return (int) (zoom);
	}

	private static double latRad(double lat) {
		double sin = Math.sin(lat * Math.PI / 180);
		double radX2 = Math.log((1 + sin) / (1 - sin)) / 2;
		return Math.max(Math.min(radX2, Math.PI), -Math.PI) / 2;
	}

	private static double zoom(double mapPx, double worldPx, double fraction) {
		// final double LN2 = .693147180559945309417;
		final double LN2 = .753147180559945309417;
		return (Math.log(mapPx / worldPx / fraction) / LN2);
	}

	// private void setMarkers(HashMap<String, ParsingData> data) {
	// for (String key : data.keySet()) {
	// // Log.e(TAG, guesetData.gh_latitude);
	// if (data.get(key).latitude == null
	// || data.get(key).latitude.equals("null")) {
	// Log.e(TAG, "gh_latitude is null");
	// } else {
	// setMarkUp(
	// new LatLng(Double.parseDouble(data.get(key).latitude),
	// Double.parseDouble(data.get(key).longitude)),
	// data.get(key));
	// }
	// }
	// }
	@Override
	public void onMapClick(LatLng latLng) {
	}

	@Override
	public void onCameraChange(CameraPosition pos) {

		if (!flag_catchMapMoving) {
			return;
		}

		// LatLng farLeft =
		// googleMap.getProjection().getVisibleRegion().farLeft;
		LatLng farRight = googleMap.getProjection().getVisibleRegion().farRight;
		LatLng nearLeft = googleMap.getProjection().getVisibleRegion().nearLeft;
		// LatLng nearRight =
		// googleMap.getProjection().getVisibleRegion().nearRight;
		RequestData.getInstance().word = searchQuery;
		RequestData.getInstance().byMap = true;
		RequestData.getInstance().ne_lat = farRight.latitude;
		RequestData.getInstance().ne_lng = farRight.longitude;
		RequestData.getInstance().sw_lat = nearLeft.latitude;
		RequestData.getInstance().sw_lng = nearLeft.longitude;
		RequestData.getInstance().zoom = pos.zoom;

		indicator.setVisibility(View.VISIBLE);
		ParserAirbnb.getInstance().getList(RequestData.getInstance());
		// Log.e(TAG, "zoom : " + pos.zoom + "\nnearRight : " + farLeft.latitude
		// + ", "
		// + farLeft.longitude + "\nfarRight : " + farRight.latitude
		// + ", " + farRight.longitude + "\nnearLeft : "
		// + nearLeft.latitude + ", " + nearLeft.longitude
		// + "\nnearRight : " + nearRight.latitude + ", "
		// + nearRight.longitude);
	}

	@Override
	public void onStop() {
		ParserAirbnb.getInstance().clearWhenStop();
		super.onStop();
	}

	@SuppressLint("NewApi")
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.airbnb, menu);

		searchItem = menu.findItem(R.id.action_search);

		searchItem.setOnActionExpandListener(new OnActionExpandListener() {

			@Override
			public boolean onMenuItemActionExpand(MenuItem item) {
				Log.d(TAG, "onMenuItemActionExpand");
				// list_Result.setVisibility(View.GONE);
				// list_Search.setVisibility(View.VISIBLE);
				return true;
			}

			@Override
			public boolean onMenuItemActionCollapse(MenuItem item) {
				Log.d(TAG, "onMenuItemActionCollapse");
				return true;
			}
		});

		mSearchView = (SearchView) searchItem.getActionView();
		mSearchView.setQueryHint(getResources().getString(
				R.string.search_accomdation_hint));
		mSearchView.setOnQueryTextListener(queryTextListener);
		SearchManager searchManager = (SearchManager) getActivity()
				.getSystemService(Context.SEARCH_SERVICE);
		if (null != searchManager) {
			mSearchView.setSearchableInfo(searchManager
					.getSearchableInfo(getActivity().getComponentName()));
		}
		mSearchView.setIconifiedByDefault(true);
		super.onCreateOptionsMenu(menu, inflater);
	}

	private abstract class QueryListenerFilterable implements
			OnQueryTextListener {
	}

	@SuppressLint("NewApi")
	private QueryListenerFilterable queryTextListener = new QueryListenerFilterable() {
		@Override
		public boolean onQueryTextSubmit(String query) {
			// Log.e("TAG", query);

			RequestData data = new RequestData();
			searchQuery = query;
			data.word = query;
			data.byMap = false;

			indicator.setVisibility(View.VISIBLE);
			ParserAirbnb.getInstance().getList(data);
			// Dialog_Loading.getInstance().show(getFragmentManager(), TAG);

			// addHistoryItem(query);
			imm = (InputMethodManager) getActivity().getSystemService(
					Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(mSearchView.getWindowToken(), 0);
			mSearchView.setQuery("", false);
			mSearchView.setIconified(true);
			searchItem.collapseActionView();
			// mSearchView.setLayoutParams(new ActionBar.LayoutParams(
			// Gravity.RIGHT));
			return false;
		}

		private AutoCompSearch searchTask = null;

		@Override
		public boolean onQueryTextChange(String newText) {
			// AutoCompSearch aa = new AutoCompSearch();
			// aa.execute(newText);

			// mData.remov
			if (searchTask == null) {
				searchTask = new AutoCompSearch();
			} else if (searchTask.getStatus().equals(AsyncTask.Status.RUNNING)) {
				searchTask.cancel(true);
			}
			searchTask = new AutoCompSearch();
			searchTask.execute(newText);

			return false;
		}

	};

	/***
	 * Google auto complete places
	 */

	private static final String LOG_TAG = "AutoCommplete";

	private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
	private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
	private static final String OUT_JSON = "/json";

	private static final String API_KEY = "AIzaSyD4oubTQwMpUl_8kUYCpCwMSGcN4EVlL6A";

	private class AutoCompSearch extends
			AsyncTask<String, ArrayList<String>, ArrayList<String>> {

		@Override
		protected ArrayList<String> doInBackground(String... params) {
			String input = params[0];
			ArrayList<String> resultList = null;

			HttpURLConnection conn = null;
			StringBuilder jsonResults = new StringBuilder();
			try {
				StringBuilder sb = new StringBuilder(PLACES_API_BASE
						+ TYPE_AUTOCOMPLETE + OUT_JSON);
				sb.append("?sensor=false&key=" + API_KEY);
				// sb.append("&components=country:uk");
				sb.append("&input=" + URLEncoder.encode(input, "utf8"));

				// Log.e(TAG, "url : " + sb.toString());
				URL url = new URL(sb.toString());
				conn = (HttpURLConnection) url.openConnection();
				InputStreamReader in = new InputStreamReader(
						conn.getInputStream());

				// Load the results into a StringBuilder
				int read;
				char[] buff = new char[1024];
				while ((read = in.read(buff)) != -1) {
					jsonResults.append(buff, 0, read);
				}
			} catch (MalformedURLException e) {
				Log.e(LOG_TAG, "Error processing Places API URL", e);
				return resultList;
			} catch (IOException e) {
				Log.e(LOG_TAG, "Error connecting to Places API", e);
				return resultList;
			} finally {
				if (conn != null) {
					conn.disconnect();
				}
			}

			try {
				// Create a JSON object hierarchy from the results
				JSONObject jsonObj = new JSONObject(jsonResults.toString());
				JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

				// Extract the Place descriptions from the results
				resultList = new ArrayList<String>(predsJsonArray.length());
				for (int i = 0; i < predsJsonArray.length(); i++) {
					resultList.add(predsJsonArray.getJSONObject(i).getString(
							"description"));
				}
			} catch (JSONException e) {
				Log.e(LOG_TAG, "Cannot process JSON results", e);
			}

			return resultList;
		}

		@Override
		protected void onPostExecute(ArrayList<String> result) {

			if (result == null) {
				Toast.makeText(getActivity(), "Error connecting", 0).show();
				return;
			}

			if (result.size() < 1) {
				autoSearchList.setVisibility(View.GONE);
			} else {
				autoSearchList.setVisibility(View.VISIBLE);
			}

			result.add("");
			searchResult = result;

			SearchMapAdapter adapter = new SearchMapAdapter(getActivity(),
					searchResult);
			// 리스트뷰에 어댑터 연결
			autoSearchList.setAdapter(adapter);

			super.onPostExecute(result);
		}
	}

	OnSetAccamoListener mListener;

	public void setOnSetAccamoListener(OnSetAccamoListener listener) {
		mListener = listener;
	}
	// @Override
	// public void onChangedTripPalTap(int viewPosition) {
	// try {
	// if (mSelectedMarker != null) {
	// mSelectedMarker.remove();
	// }
	// setMarkUp(
	// new LatLng(Double.parseDouble(PropertyManager.getInstance()
	// .getLatitude()), Double.parseDouble(PropertyManager
	// .getInstance().getLongitude())), null);
	// } catch (NumberFormatException e) {
	// }
	//
	// }

}
