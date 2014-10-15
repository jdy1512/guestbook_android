package com.allchange.guestbook.marektmap;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.allchange.guestbook.R;
import com.allchange.guestbook.marektmap.MarkerOverlay.OnClickMarkerListener;
import com.allchange.guestbook.marektmap.TWeather.OnGetWetherListener;
import com.allchange.guestbook.picker.TimePickerFragment;
import com.allchange.guestbook.picker.TimePickerFragment.TimePickerListener;
import com.allchange.guestbook.property.MyApplication;
import com.allchange.guestbook.property.PropertyManager;
import com.allchange.guestbook.url.Url;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.google.android.gms.maps.model.LatLng;
import com.skp.Tmap.TMapCircle;
import com.skp.Tmap.TMapMarkerItem2;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener;

public class MarketMapFragment extends Fragment implements TimePickerListener {
	public static final String API_ENG = "EngService";
	public static final String API_KOR = "KorService";

	public static final String SAVED_STATE_ACTION_BAR_HIDDEN = "saved_state_action_bar_hidden";
	private static final String TAG = "MarketMapFragment";
	public static final String tourAPIAppKey = "eh8DxfqhZxae77erI7KXIIosTo8LNR7S7cQ8CXvIKTVJnhnntPJQp9MS4M1xnaYJaqE22q3VXUxWcL/rsgJWvg==";
	public static String serviceKey;
	public static final String appKey = "76f268cc-80c2-37e9-bfcc-cc6e61271f0e";
	private List<TMapCircle> circles;
	private List<TMapMarkerItem2> markers;
	private AQuery aquery;
	// private GoogleMap googleMap;
	private TMapView tmapview;
	private TextView tv_temp, tv_location;
	private ImageView iv_weather;
	private int[] arrImage = { R.drawable.wethear_01, R.drawable.wethear_01,
			R.drawable.wethear_02, R.drawable.wethear_03,
			R.drawable.wethear_04, R.drawable.wethear_05,
			R.drawable.wethear_06, R.drawable.wethear_07,
			R.drawable.wethear_08, R.drawable.wethear_09,
			R.drawable.wethear_10, R.drawable.wethear_11,
			R.drawable.wethear_12, R.drawable.wethear_13,
			R.drawable.wethear_14, R.drawable.wethear_15,
			R.drawable.wethear_16, R.drawable.wethear_17,
			R.drawable.wethear_18, R.drawable.wethear_19,
			R.drawable.wethear_20, R.drawable.wethear_21,
			R.drawable.wethear_22, R.drawable.wethear_23,
			R.drawable.wethear_24, R.drawable.wethear_25,
			R.drawable.wethear_26, R.drawable.wethear_27,
			R.drawable.wethear_28, R.drawable.wethear_29,
			R.drawable.wethear_30, R.drawable.wethear_31,
			R.drawable.wethear_32, R.drawable.wethear_33,
			R.drawable.wethear_34, R.drawable.wethear_35,
			R.drawable.wethear_36, R.drawable.wethear_37,
			R.drawable.wethear_38, R.drawable.wethear_39,
			R.drawable.wethear_40, R.drawable.wethear_41, R.drawable.wethear_42 };

	HashMap<String, Integer> map_weather = new HashMap<String, Integer>();
	private String[] key_weather = { "SKY_A00", "SKY_A01", "SKY_A02",
			"SKY_A03", "SKY_A04", "SKY_A05", "SKY_A06", "SKY_A07", "SKY_A08",
			"SKY_A09", "SKY_A10", "SKY_A11", "SKY_A12", "SKY_A13", "SKY_A14" };

	private int[] weather_sun = { 38, 1, 2, 3, 12, 13, 14, 18, 21, 32, 4, 29,
			26, 27, 28 };
	private int[] weather_moon = { 38, 8, 9, 10, 40, 41, 42, 18, 21, 32, 4, 29,
			26, 27, 28 };

	SlidingUpPanelLayout slidinglayout;

	private LinearLayout market_map_drawer_linear;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.permitNetwork().build());
		// setTitle("market map");
		View v = inflater.inflate(R.layout.map_hotspot, container, false);
		// getActionBaCr().setDisplayHomeAsUpEnabled(true);

		try {
			serviceKey = serviceKey = URLEncoder.encode(tourAPIAppKey, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		aquery = new AQuery(getActivity());

		init(v);

		// 슬라이딩 업
		slidinglayout = (SlidingUpPanelLayout) v
				.findViewById(R.id.sliding_layout);
		slidinglayout.setOverlayed(true);
		// slidinglayout.setAnchorPoint(0.5f);
		slidinglayout.setPanelSlideListener(new PanelSlideListener() {

			@Override
			public void onPanelSlide(View panel, float slideOffset) {
			}

			@Override
			public void onPanelHidden(View panel) {
			}

			@SuppressLint("NewApi")
			@Override
			public void onPanelExpanded(View panel) {
				fragment_TourInfoDetail.getTourInfo(marker_clicked,
						MarketMapFragment.this.getActivity());
				market_map_drawer_linear.setBackground(MarketMapFragment.this
						.getResources().getDrawable(
								R.drawable.market_map_info_detail_close));
				market_map_drawer_linear.setPadding(0, 0, 0, 0);
				fragment_TourInfoDetail.setGonePreview(true);
			}

			@SuppressLint("NewApi")
			@Override
			public void onPanelCollapsed(View panel) {
				fragment_TourInfoDetail.setGonePreview(false);
				market_map_drawer_linear.setBackground(MarketMapFragment.this
						.getResources().getDrawable(R.drawable.market_map_bg));
				market_map_drawer_linear.setPadding(0, 0, 0, 0);
			}

			@Override
			public void onPanelAnchored(View panel) {
			}
		});
		boolean actionBarHidden = savedInstanceState != null ? savedInstanceState
				.getBoolean(SAVED_STATE_ACTION_BAR_HIDDEN, false) : false;
		if (actionBarHidden) {
			// getActivity().getActionBar().hide();
		}

		initWeather();
		return v;
	}

	TWeather tWeather;

	// 액션바 아래 잇는 날씨정보 표시함
	private void initWeather() {
		for (int i = 0; i < key_weather.length; i++) {
			map_weather.put(key_weather[i], weather_sun[i]);
		}

		tWeather = new TWeather();
		tWeather.setOnGetWetherListener(new OnGetWetherListener() {
			@Override
			public void onGetWeather(JSONObject result) {
				try {

					JSONObject weather = result.getJSONObject("weather")
							.getJSONArray("minutely").getJSONObject(0);

					Log.e(TAG, "weather: " + weather.toString());
					tv_temp.setText(weather.getJSONObject("temperature")
							.getString("tc"));
					tv_location.setText(weather.getJSONObject("station")
							.getString("name"));
					iv_weather.setImageDrawable(getResources().getDrawable(
							arrImage[map_weather.get(weather.getJSONObject(
									"sky").getString("code"))]));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
		tWeather.requestASync(PropertyManager.getInstance().getLatitude(),
				PropertyManager.getInstance().getLongitude());
	}

	TourInfoDetailFragment fragment_TourInfoDetail;

	public void init(View v) {
		try {
			// weather
			tv_temp = (TextView) v.findViewById(R.id.marketMap_tv_temperature);
			tv_location = (TextView) v.findViewById(R.id.marketMap_tv_location);
			iv_weather = (ImageView) v
					.findViewById(R.id.marketMap_image_weather);

			market_map_drawer_linear = (LinearLayout) v
					.findViewById(R.id.market_map_drawer_linear);
			market_map_drawer_linear.setVisibility(View.INVISIBLE);

			// drawer deatil fragment
			fragment_TourInfoDetail = new TourInfoDetailFragment();
			getChildFragmentManager()
					.beginTransaction()
					.replace(R.id.market_map_info_detail,
							fragment_TourInfoDetail).commit();

			// tmap 설정
			tmapview = new TMapView(getActivity());

			if (Locale.getDefault().getLanguage()
					.equals(Locale.KOREAN.toString())) {
				tmapview.setLanguage(tmapview.LANGUAGE_KOREAN);
			} else if (Locale.getDefault().getLanguage()
					.equals(Locale.ENGLISH.toString())) {
				tmapview.setLanguage(tmapview.LANGUAGE_ENGLISH);
			}
			tmapview.setSKPMapApiKey(appKey);
			// tmapview.setZoomLevel(5);// 11 - 시
			((RelativeLayout) v.findViewById(R.id.tmap)).addView(tmapview);

			// circle list 생성
			circles = new ArrayList<TMapCircle>();
			// marker list 생성
			markers = new ArrayList<TMapMarkerItem2>();

			// accommodation 좌표
			// 화면 이동
			goToLocation(new LatLng(Double.parseDouble(PropertyManager
					.getInstance().getLatitude()),
					Double.parseDouble(PropertyManager.getInstance()
							.getLongitude())));

			TimePickerFragment picker;
			// TimePickerFragment 클래스 init세팅(picker전체길이, 시작날짜, 끝날짜)
			picker = new TimePickerFragment();
			int screen_x = getResources().getDisplayMetrics().widthPixels;
			picker.init(screen_x);
			picker.setOnPickerListener(this);
			getChildFragmentManager().beginTransaction()
					.add(R.id.market_map_picker, picker).commit();

		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(getActivity(),
					getResources().getString(R.string.network_error),
					Toast.LENGTH_LONG).show();
		}
	}

	public void goToLocation(LatLng latlng) {
		// 화면 이동
		tmapview.setCenterPoint(latlng.longitude, latlng.latitude);
		tmapview.setZoomLevel(13);// 11 - 시
	}

	public void addMarker(LatLng latlng) {
		// 마커 accommodation latitude, longitude
		// TMapMarkerItem markeritem = new TMapMarkerItem();
		// markeritem
		// .setTMapPoint(new TMapPoint(latlng.latitude, latlng.longitude));
		// markeritem.setID("" + markers.size());
		// tmapview.addMarkerItem(markeritem.getID(), markeritem);
		// markers.add(markeritem);

	}

	public void addCircle(LatLng latlng) {
		// drawCircle
		TMapCircle tcircle = new TMapCircle();
		tcircle.setCenterPoint(new TMapPoint(latlng.latitude, latlng.longitude));
		tcircle.setLineAlpha(0);
		tcircle.setRadius(100);
		tcircle.setAreaAlpha(70);
		tcircle.setAreaColor(Color.RED);
		tcircle.setID("" + circles.size());
		tmapview.addTMapCircle(tcircle.getID(), tcircle);

		circles.add(tcircle);
	}

	public void mapClear() {
		for (TMapCircle tcircle : circles) {
			tmapview.removeTMapCircle(tcircle.getID());
		}
		for (TMapMarkerItem2 markeritem : markers) {
			tmapview.removeMarkerItem(markeritem.getID());
		}
		markers.clear();
		circles.clear();
	}

	MarkerOverlay marker_clicked = null;

	// TourAPI Item을 맵에 하나 그려줌
	public void showTourInfo(LatLng latlng, String addr1, String dist,
			String image, String tel1, String title1, String contentid,
			String contentTypeId) {
		MarkerOverlay marker1 = new MarkerOverlay(getActivity(), tmapview);

		String strID = String.format("%02d", markers.size());

		marker1.setID(strID);
		marker1.setIcon(BitmapFactory.decodeResource(getResources(),
				R.drawable.market_map_pin_1_normal));

		marker1.setLatitude(latlng.latitude);
		marker1.setLongitude(latlng.longitude);
		marker1.setTitle(title1);
		marker1.setAddr(addr1);
		marker1.setTel(tel1);
		marker1.setImageUrl(image);
		marker1.setContentid(contentid);
		marker1.setContentTypeId(contentTypeId);

		markers.add(marker1);
		marker1.setTMapPoint(new TMapPoint(marker1.getLatitude(), marker1
				.getLongitude()));

		marker1.setOnClickMarkerListener(new OnClickMarkerListener() {
			@Override
			public void onClickMarker(MarkerOverlay marker) {
				if (marker_clicked != null) {
					marker_clicked.setIcon(BitmapFactory.decodeResource(
							getResources(), R.drawable.market_map_pin_1_normal));
				}
				market_map_drawer_linear.setVisibility(View.VISIBLE);
				marker_clicked = marker;

				fragment_TourInfoDetail.setItemView(marker, aquery);

			}
		});
		tmapview.addMarkerItem2(strID, marker1);

	}

	// 지대영 마커 이미지 네모에 사진 등등
	// public synchronized Bitmap createBitmapFromtourAPI(Bitmap bitmap,
	// String title, String addr, String tel) {
	// Bitmap bm = null;
	// try {
	// int len = (title.length() > addr.length() ? title.length() : addr
	// .length()) * 14;
	// bm = Bitmap.createBitmap(len, 70, Config.ARGB_4444);
	// Canvas c = new Canvas(bm);
	// c.drawARGB(0xc0, 0xff, 0xff, 0xff);
	// c.drawBitmap(bitmap, 10, 10, null);
	// Paint p = new Paint();
	// p.setColor(Color.BLACK);
	// p.setTextAlign(Align.LEFT);
	// p.setTextSize(14);
	// c.drawText(title, 70, 20, p);
	// c.drawText(addr, 70, 40, p);
	// c.drawText(tel, 70, 60, p);
	// } catch (OutOfMemoryError oome) {
	// Log.e("debug", "OutOfMemory Error!");
	// } finally {
	// bitmap.recycle();
	// }
	// return bm;
	// }

	public void getGPSTrace(String hour) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("selected_time", hour);
		aquery.ajax(Url.SERVER_TRACE_SHOW, params, JSONObject.class,
				new AjaxCallback<JSONObject>() {
					@Override
					public void callback(String url, JSONObject json,
							AjaxStatus status) {
						if (json != null) {
							// Log.e("debug", json.toString());
							try {
								JSONArray trace = json.getJSONArray("trace");
								for (int i = 0; i < trace.length(); i++) {
									try {
										JSONObject obj = trace.getJSONObject(i);
										for (int j = 0; j < 6; j++) {
											String lat_s = obj.getString(
													"latitude").split(",")[j];
											String lon_s = obj.getString(
													"longitude").split(",")[j];
											if (!lat_s.equals("0")
													&& !lon_s.equals("0")) {
												double latitude = Double
														.parseDouble(lat_s);
												double longitude = Double
														.parseDouble(lon_s);
												LatLng latlng = new LatLng(
														latitude, longitude);
												addCircle(latlng);

											}
										}
									} catch (NumberFormatException nfe) {
										Log.e("debug", "can not parse");
										continue;
									}
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}
				});
	}

	// tourAPI에서 정보를 가져온다.
	public void koreaTourism(int numOfRows, int pageNo, String arrange,
			String listYN, String MobileOS, String MobileApp,
			final int contentTypeId, double mapX, double mapY, int radius,
			String lng) {
		/*
		 * numOfRows : 한페이지 결과 수 , 기본값 10 pageNo : 페이지 번호 , 기본값 1 arrange : 정렬
		 * 구분 , 기본값 A , (A=제목순, B=조회순, C=수정일순, D=생성일순, E=거리순) listYN : 목록 구분 ,
		 * 기본값 Y , 목록 구분(Y=목록, N=개수) MobileOS : OS 구분 , 기본값 ETC , (IOS=아이폰,
		 * AND=안드로이드, WIN=윈도폰, ETC) MobileApp : 서비스명 , , 서비스명=어플명 contentTypeId
		 * : 관광타입 ID , , 관광타입(관광지, 숙박 등) ID mapX : X좌표 , , GPS X좌표(WGS84 경도 좌표)
		 * mapY : Y좌표 , , GPS Y좌표(WGS84 위도 좌표) radius : 거리 반경 , , 거리 반경(단위:m),
		 * Max값 20000m=20Km
		 */

		String Url = "http://api.visitkorea.or.kr/openapi/service/rest/" + lng
				+ "/locationBasedList" + "?ServiceKey=" + serviceKey;
		if (contentTypeId != -1)
			Url += "&contentTypeId=" + contentTypeId;
		Url += "&mapX=" + mapX + "&mapY=" + mapY + "&radius=" + radius
				+ "&pageNo=" + pageNo + "&numOfRows=" + numOfRows + "&arrange="
				+ arrange + "&listYN=" + listYN + "&MobileOS=" + MobileOS
				+ "&MobileApp=" + MobileApp + "&_type=json";
		aquery.ajax(Url, JSONObject.class, new AjaxCallback<JSONObject>() {
			@Override
			public void callback(String url, JSONObject json, AjaxStatus status) {
				if (json != null) {
					// Log.e(TAG, json.toString());
					try {
						JSONArray item = json.getJSONObject("response")
								.getJSONObject("body").getJSONObject("items")
								.getJSONArray("item");
						for (int i = 0; i < item.length(); i++) {
							try {
								JSONObject obj = item.getJSONObject(i);
								double latitude = Double.parseDouble(obj
										.getString("mapy"));
								double longitude = Double.parseDouble(obj
										.getString("mapx"));
								// 이미지
								String image = null;
								try {
									image = obj.getString("firstimage2");
								} catch (JSONException e2) {
									try {
										image = obj.getString("firstimage");
									} catch (JSONException e3) {
										image = "empty";
									}
								}
								// 주소
								String addr = null;
								try {
									addr = obj.getString("addr1");
								} catch (JSONException e2) {
									addr = "empty";
								}
								// 거리
								String dist = null;
								try {
									dist = obj.getString("dist");
								} catch (JSONException e2) {
									dist = "empty";
								}
								// 전화번호
								String tel = null;
								try {
									tel = obj.getString("tel");
								} catch (JSONException e2) {
									tel = "empty";
								}
								// 이름
								String title = null;
								try {
									title = obj.getString("title");
								} catch (JSONException e2) {
									title = "empty";
								}
								String contentid = null;
								try {
									contentid = obj.getString("contentid");
								} catch (JSONException e2) {
									contentid = "empty";
								}

								// 좌표
								LatLng latlng = new LatLng(latitude, longitude);

								// draw on the tmap
								showTourInfo(latlng, addr, dist, image, tel,
										title, contentid, contentTypeId + "");
							} catch (NumberFormatException nfe) {
								Log.e(TAG, "can not parse");
								continue;
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	@Override
	public void pickerCallback(String hour) {
		// TODO Auto-generated method stub

		// accommodation 좌표
		// 화면 이동
		mapClear();
		try {
			goToLocation(new LatLng(Double.parseDouble(PropertyManager
					.getInstance().getLatitude()),
					Double.parseDouble(PropertyManager.getInstance()
							.getLongitude())));
		} catch (NullPointerException e) {
			// TODO:
		}

		// circle
		getGPSTrace(hour);
		// tour
		// 영문기준
		// 관광지 76
		// 문화시설 78
		// 행사/공연/축제 85
		// 여행코스 국문만 서비스 레포츠 75
		// 숙박 80
		// 쇼핑 79
		// 음식점 82
		// 교통 (다국어만 서비스) 77

		// 국문
		// 행사/공연/축제 15

		if (Locale.getDefault().getLanguage().equals(Locale.KOREAN.toString())) {
			koreaTourism(10, 1, "S", "Y", "AND", "GuestBook", 15,
					Double.parseDouble(PropertyManager.getInstance()
							.getLongitude()),
					Double.parseDouble(PropertyManager.getInstance()
							.getLatitude()), 10000, API_KOR);
		} else if (Locale.getDefault().getLanguage()
				.equals(Locale.ENGLISH.toString())) {
			koreaTourism(10, 1, "S", "Y", "AND", "GuestBook", 85,
					Double.parseDouble(PropertyManager.getInstance()
							.getLongitude()),
					Double.parseDouble(PropertyManager.getInstance()
							.getLatitude()), 10000, API_ENG);
		}

		// (A=제목순 , B=조회순 , C= 수정일순 수정일순 , D=생성일순 , E= 거리순 )
		// 대표이미지 대표이미지 정렬 추가 (O=제목순 , P=조회순 , Q=수정일순 , R= 생성일순 , S= 거리순 )

		// 마커
		addMarker(new LatLng(Double.parseDouble(PropertyManager.getInstance()
				.getLatitude()), Double.parseDouble(PropertyManager
				.getInstance().getLongitude())));
	}
}
