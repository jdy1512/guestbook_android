package com.allchange.guestbook.marektmap;

import java.util.HashMap;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.allchange.guestbook.R;
import com.allchange.guestbook.property.MyApplication;
import com.allchange.guestbook.property.PropertyManager;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

public class TourInfoDetailFragment extends Fragment {
	private static final String TAG = "MarketMapFragment";

	private AQuery aquery;

	private ImageView iv_main;
	private TextView tv_title, tv_tel, tv_telname, tv_overview, tv_address,
			tv_intro;

	private ImageView iv_item;
	private TextView tv_item_name, tv_item_address;

	private FrameLayout frame_preView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.market_map_detail, container, false);
		init(v);
		// getActionBaCr().setDisplayHomeAsUpEnabled(true);
		return v;
	}

	public void init(View v) {
		// tv_temp = (TextView) v.findViewById(R.id.marketMap_tv_temperature);
		iv_main = (ImageView) v.findViewById(R.id.market_map_detail_image);
		tv_title = (TextView) v.findViewById(R.id.market_map_detail_tv_name);
		tv_tel = (TextView) v.findViewById(R.id.market_map_detail_tv_phone);
		// tv_telname = (TextView)
		// v.findViewById(R.id.market_map_detail_tv_name);
		tv_overview = (TextView) v
				.findViewById(R.id.market_map_detail_tv_overview);
		tv_intro = (TextView) v.findViewById(R.id.market_map_detail_tv_info);
		tv_address = (TextView) v
				.findViewById(R.id.market_map_detail_tv_address);

		// item ..
		frame_preView = (FrameLayout) v
				.findViewById(R.id.market_map_detail_preview);
		tv_item_name = (TextView) v.findViewById(R.id.market_map_item_name);
		tv_item_address = (TextView) v
				.findViewById(R.id.market_map_item_address);
		iv_item = (ImageView) v.findViewById(R.id.market_map_item_iv);
	}

	public void showDetail(TourData data) {

		// 아래서 append로 하기 때문에 초기화한다.
		tv_intro.setText("");
		// iv_main = (ImageView) v.findViewById(R.id.market_map_detail_image);
		tv_title.setText(Html.fromHtml(data.title));
		tv_tel.setText(data.tel);
		// tv_telname = (TextView)
		// v.findViewById(R.id.market_map_detail_tv_name);
		tv_overview.setText(Html.fromHtml(data.overview));
		tv_address.setText(data.addr);

		tv_intro.append(Html.fromHtml(getResources().getString(
				R.string.market_map_agelimit)
				+ data.agelimit));

		tv_intro.append("\n");
		tv_intro.append(Html.fromHtml(getResources().getString(
				R.string.market_map_bookingplace)
				+ data.bookingplace));

		tv_intro.append("\n");
		tv_intro.append(Html.fromHtml(getResources().getString(
				R.string.market_map_discountinfofestival)
				+ data.discountinfofestival));

		tv_intro.append("\n");
		tv_intro.append(Html.fromHtml(getResources().getString(
				R.string.market_map_eventstartdate)
				+ data.eventstartdate));

		tv_intro.append("\n");
		tv_intro.append(Html.fromHtml(getResources().getString(
				R.string.market_map_eventenddate)
				+ data.eventenddate));

		// 홈페이지가 대부분 null임
		// tv_intro.append("\n");
		// tv_intro.append(Html.fromHtml(getResources().getString(
		// R.string.market_map_eventhomepage)
		// + data.eventhomepage));

		tv_intro.append("\n");
		tv_intro.append(Html.fromHtml(getResources().getString(
				R.string.market_map_eventplace)
				+ data.eventplace));

		tv_intro.append("\n");
		tv_intro.append(Html.fromHtml(getResources().getString(
				R.string.market_map_placeinfo)
				+ data.placeinfo));

		tv_intro.append("\n");
		tv_intro.append(Html.fromHtml(getResources().getString(
				R.string.market_map_playtime)
				+ data.playtime));

		tv_intro.append("\n");
		tv_intro.append(Html.fromHtml(getResources().getString(
				R.string.market_map_program)
				+ data.program));

		tv_intro.append("\n");
		tv_intro.append(Html.fromHtml(getResources().getString(
				R.string.market_map_subevent)
				+ data.subevent));

		tv_intro.append("\n");
		tv_intro.append(Html.fromHtml(getResources().getString(
				R.string.market_map_usetimefestival)
				+ data.usetimefestival));

		tv_intro.append("\n");
		tv_intro.append(Html.fromHtml(getResources().getString(
				R.string.market_map_spendtimefestival)
				+ data.spendtimefestival));

		tv_intro.append("\n");

		AjaxCallback<Bitmap> ac = new AjaxCallback<Bitmap>() {
			@Override
			public void callback(String url, Bitmap bitmap, AjaxStatus status) {
				try {
					iv_main.setImageBitmap(bitmap);
					iv_main.invalidate();
				} catch (OutOfMemoryError oome) {
					Log.e(TAG, "OutOfMemory Error!");
				} finally {
					// bitmap.recycle();
				}
			}
		};
		if (!data.image.equals("empty")) {
			Bitmap bitmap = null;
			if ((bitmap = aquery.getCachedImage(data.image)) == null) {
				aquery.ajax(data.image, Bitmap.class, ac).cache(data.image, 0);
			} else {
				try {
					iv_main.setImageBitmap(bitmap);
					// iv_main.setScaleX(1.5f);
					// iv_main.setScaleY(1.5f);
					iv_main.invalidate();
				} catch (OutOfMemoryError oome) {
					Log.e(TAG, "OutOfMemory Error!");
				} finally {
					// bitmap.recycle();
				}
			}
		}
	}

	public void setGonePreview(boolean b) {
		if (b) {
			frame_preView.setVisibility(View.GONE);
		} else {
			frame_preView.setVisibility(View.VISIBLE);
		}
	}

	public void setItemView(MarkerOverlay marker, AQuery a) {
		frame_preView.setVisibility(View.VISIBLE);
		tv_item_name.setText(marker.getTitle());
		tv_item_address.setText(marker.getAddr());
		// 이미지는 aquery로 가져온다
		AjaxCallback<Bitmap> ac = new AjaxCallback<Bitmap>() {
			@Override
			public void callback(String url, Bitmap bitmap, AjaxStatus status) {
				try {
					iv_item.setImageBitmap(MyApplication.getCircledBitmap(
							bitmap, url));
					iv_item.invalidate();
				} catch (OutOfMemoryError oome) {
					Log.e("debug", "OutOfMemory Error!");
				} finally {
					// bitmap.recycle();
				}
			}
		};
		if (!marker.getImageUrl().equals("empty")) {
			Bitmap bitmap = null;
			if ((bitmap = a.getCachedImage(marker.getImageUrl())) == null) {
				a.ajax(marker.getImageUrl(), Bitmap.class, ac).cache(
						marker.getImageUrl(), 0);
			} else {
				try {
					iv_item.setImageBitmap(MyApplication.getCircledBitmap(
							bitmap, marker.getImageUrl()));
					iv_item.setScaleX(1.5f);
					iv_item.setScaleY(1.5f);
					iv_item.invalidate();
				} catch (OutOfMemoryError oome) {
					Log.e(TAG, "OutOfMemory Error!");
				} finally {
					// bitmap.recycle();
				}
			}
		}
	}

	public void getTourInfo(MarkerOverlay marker_clicked, Context c) {
		if (mapTourData.containsKey(marker_clicked.getContentid())) {
			showDetail(mapTourData.get(marker_clicked.getContentid()));
		} else {
			if (Locale.getDefault().getLanguage()
					.equals(Locale.KOREAN.toString())) {
				koreaTourism("AND", "GuestBook", marker_clicked, c,
						MarketMapFragment.API_KOR);
			} else if (Locale.getDefault().getLanguage()
					.equals(Locale.ENGLISH.toString())) {
				koreaTourism("AND", "GuestBook", marker_clicked, c,
						MarketMapFragment.API_ENG);
			}
		}

	}

	HashMap<String, TourData> mapTourData = new HashMap<String, TourData>();

	// tourAPI에서 정보를 가져온다.
	public void koreaTourism(final String MobileOS, final String MobileApp,
			final MarkerOverlay marker_clicked, final Context c,
			final String lng) {
		aquery = new AQuery(c);
		String Url = "http://api.visitkorea.or.kr/openapi/service/rest/" + lng
				+ "/detailIntro" + "?ServiceKey="
				+ MarketMapFragment.serviceKey;
		Url += "&MobileOS=" + MobileOS + "&contentId="
				+ marker_clicked.getContentid() + "&introYN=Y" + "&MobileApp="
				+ MobileApp + "&contentTypeId="
				+ marker_clicked.getContentTypeId() + "&defaultYN=" + "Y"
				+ "&firstImageYN=" + "Y" + "&overviewYN=" + "Y"
				+ "&addrinfoYN=" + "Y" + "&_type=json";
		final TourData data = new TourData();
		aquery.ajax(Url, JSONObject.class, new AjaxCallback<JSONObject>() {
			@Override
			public void callback(String url, JSONObject json, AjaxStatus status) {
				if (json != null) {
					try {
						Log.d(TAG, "url : " + url);
						JSONObject obj = json.getJSONObject("response")
								.getJSONObject("body").getJSONObject("items")
								.getJSONObject("item");

						try {
							// 관람가능연령
							try {
								data.agelimit = obj.getString("agelimit");
							} catch (JSONException e2) {
								data.agelimit = "empty";
							}
							// 예매처
							try {
								data.bookingplace = obj
										.getString("bookingplace");
							} catch (JSONException e2) {
								data.bookingplace = "empty";
							}
							// 할인정보
							try {
								data.discountinfofestival = obj
										.getString("discountinfofestival");
							} catch (JSONException e2) {
								data.discountinfofestival = "empty";
							}
							// 행사종료일
							try {
								data.eventenddate = obj
										.getString("eventenddate");
							} catch (JSONException e2) {
								data.eventenddate = "empty";
							}
							// 행사홈페이지
							try {
								data.eventhomepage = obj
										.getString("eventhomepage");
							} catch (JSONException e2) {
								data.eventhomepage = "empty";
							}
							// 행사장소
							try {
								data.eventplace = obj.getString("eventplace");
							} catch (JSONException e2) {
								data.eventplace = "empty";
							}
							// 행사시작일
							try {
								data.eventstartdate = obj
										.getString("eventstartdate");
							} catch (JSONException e2) {
								data.eventstartdate = "empty";
							}
							// 행사장위치안내
							try {
								data.placeinfo = obj.getString("placeinfo");
							} catch (JSONException e2) {
								data.placeinfo = "empty";
							}
							// 공연시간
							try {
								data.playtime = obj.getString("playtime");
							} catch (JSONException e2) {
								data.playtime = "empty";
							}
							// 행사프로그램
							try {
								data.program = obj.getString("program");
							} catch (JSONException e2) {
								data.program = "empty";
							}
							// 부대행사
							try {
								data.subevent = obj.getString("subevent");
							} catch (JSONException e2) {
								data.subevent = "empty";
							}
							// 이용요금
							try {
								data.usetimefestival = obj
										.getString("usetimefestival");
							} catch (JSONException e2) {
								data.usetimefestival = "empty";
							}
							// 관람소요시간
							try {
								data.spendtimefestival = obj
										.getString("spendtimefestival");
							} catch (JSONException e2) {
								data.spendtimefestival = "empty";
							}

							// mapTourData.put(marker_clicked.getContentid(),
							// data);
						} catch (NumberFormatException nfe) {
							Log.e(TAG, "can not parse");
						}
					} catch (JSONException e) {
						e.printStackTrace();
					} finally {
						koreaTourismOverView(MobileOS, MobileApp,
								marker_clicked.getContentid(), c, data, lng);
					}
				}
			}
		});
	}

	// tourAPI에서 정보를 가져온다.
	public void koreaTourismOverView(String MobileOS, String MobileApp,
			final String contentId, Context c, final TourData data, String lng) {
		aquery = new AQuery(c);
		String Url = "http://api.visitkorea.or.kr/openapi/service/rest/" + lng
				+ "/detailCommon" + "?ServiceKey="
				+ MarketMapFragment.serviceKey;
		Url += "&MobileOS=" + MobileOS + "&contentId=" + contentId
				+ "&MobileApp=" + MobileApp + "&defaultYN=" + "Y"
				+ "&firstImageYN=" + "Y" + "&overviewYN=" + "Y"
				+ "&addrinfoYN=" + "Y" + "&_type=json";
		aquery.ajax(Url, JSONObject.class, new AjaxCallback<JSONObject>() {
			@Override
			public void callback(String url, JSONObject json, AjaxStatus status) {
				if (json != null) {
					try {
						Log.d(TAG, "url : " + url);
						JSONObject obj = json.getJSONObject("response")
								.getJSONObject("body").getJSONObject("items")
								.getJSONObject("item");

						try {
							// 이미지
							try {
								data.image = obj.getString("firstimage");
							} catch (JSONException e2) {
								data.image = "empty";
							}
							// 주소
							try {
								data.addr = obj.getString("addr1");
							} catch (JSONException e2) {
								data.addr = "empty";
							}
							// 개요
							try {
								data.overview = obj.getString("overview");
							} catch (JSONException e2) {
								data.overview = "empty";
							}
							// 전화번호
							try {
								data.tel = obj.getString("tel");
							} catch (JSONException e2) {
								data.tel = "empty";
							}
							// 전화번호 이름
							try {
								data.telname = obj.getString("telname");
							} catch (JSONException e2) {
								data.telname = "empty";
							}
							// 이름
							try {
								data.title = obj.getString("title");
							} catch (JSONException e2) {
								data.title = "empty";
							}

							mapTourData.put(contentId, data);
							showDetail(data);
						} catch (NumberFormatException nfe) {
							Log.e("debug", "can not parse");
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}
}
