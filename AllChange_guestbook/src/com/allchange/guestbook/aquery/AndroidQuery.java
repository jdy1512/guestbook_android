package com.allchange.guestbook.aquery;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.util.Log;
import android.widget.Toast;

import com.allchange.guestbook.R;
import com.allchange.guestbook.aquery.delete.room.AqueryDeleteRoom;
import com.allchange.guestbook.aquery.delete.room.AqueryDeleteRoomCallbackListener;
import com.allchange.guestbook.aquery.facebook.login.AqueryFBLogin;
import com.allchange.guestbook.aquery.facebook.login.FBLoginCallbackListener;
import com.allchange.guestbook.aquery.get.feed.AqueryGetFeed;
import com.allchange.guestbook.aquery.get.feed.AqueryGetFeedCallbackListener;
import com.allchange.guestbook.aquery.get.map.members.AqueryGetMapMembers;
import com.allchange.guestbook.aquery.get.map.members.AqueryGetMapMembersCallbackListener;
import com.allchange.guestbook.aquery.get.profiles.AqueryGetProfiles;
import com.allchange.guestbook.aquery.get.profiles.AqueryGetProfilesCallbackListener;
import com.allchange.guestbook.aquery.get.properties.AqueryGetProperties;
import com.allchange.guestbook.aquery.get.properties.AqueryGetPropertiesCallbackListener;
import com.allchange.guestbook.aquery.get.roomlist.AqueryGetRoomList;
import com.allchange.guestbook.aquery.get.visit.AqueryGetVisit;
import com.allchange.guestbook.aquery.get.visit.AqueryGetVisitCallbackListener;
import com.allchange.guestbook.aquery.google.login.AqueryGoogleLogin;
import com.allchange.guestbook.aquery.google.login.GoogleLoginCallbackListener;
import com.allchange.guestbook.aquery.login.AqueryLogin;
import com.allchange.guestbook.aquery.login.AquerySessionCheck;
import com.allchange.guestbook.aquery.make.accomodation.AqueryMakeAccomodation;
import com.allchange.guestbook.aquery.make.chattingroom.AqueryMakeChattingRoom;
import com.allchange.guestbook.aquery.make.room.AqueryMakeRoom;
import com.allchange.guestbook.aquery.searchlist.AquerySearchList;
import com.allchange.guestbook.aquery.signup.AquerySignUp;
import com.allchange.guestbook.aquery.update.map.bounds.AqueryUpdateBounds;
import com.allchange.guestbook.aquery.update.map.bounds.AqueryUpdateBoundsCallbackListener;
import com.allchange.guestbook.aquery.write.text.AqueryWriteText;
import com.allchange.guestbook.main.profiles.ChattingRoomPeopleData;
import com.allchange.guestbook.property.MyApplication;
import com.allchange.guestbook.property.PropertyManager;
import com.allchange.guestbook.url.Url;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;

public class AndroidQuery implements AqueryFailCallbackListener {
	private static AndroidQuery aQuery;
	private static Context aContext;
	private AQuery aquery;
	private static final String TAG = "AndroidQuery";
	private static AqueryRootRequest mPreRequest;

	public static AndroidQuery setInstance(Context context) {
		if (aQuery == null) {
			aQuery = new AndroidQuery(context);
		}
		return aQuery;
	}

	public void clearAQuery() {
		if (aquery != null) {
			aquery.ajaxCancel();
			aquery.clear();
			aquery = null;
			aquery = new AQuery(aContext);
		}
	}

	public static AndroidQuery getInstance() {
		if (!isOnline(aContext))
			try {
				Toast.makeText(
						aContext,
						MyApplication.getContext().getResources()
								.getString(R.string.network_check),
						Toast.LENGTH_SHORT).show();
			} catch (Exception e) {
				// TODO: handle exception
			}

		return aQuery;
	}

	public AndroidQuery(Context context) {
		aContext = context;
		aquery = new AQuery(context);
	}

	public AQuery getAquery() {
		return aquery;
	}

	public Context getContext() {
		return aContext;
	}

	public void handleNetworkError(String message,
			FBLoginCallbackListener listener) {
		if (message.equals("network error")) {
			Toast.makeText(
					aContext,
					MyApplication.getContext().getResources()
							.getString(R.string.network_error),
					Toast.LENGTH_SHORT).show();
		} else if (message.equals("session null")) {
			// facebook login user
			if (PropertyManager
					.getInstance()
					.getLoginMethod()
					.equals(MyApplication.getContext().getResources()
							.getString(R.string.facebook))) {
				AndroidQuery.getInstance().requestFBLogin(listener,
						Url.SERVER_FACEBOOK_LOGIN,
						PropertyManager.getInstance().getToken());
			} else {
				// another login user

			}

		}
	}

	public static boolean isOnline(Context context) { // network 연결 상태 확인
		try {
			ConnectivityManager conMan = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);

			State wifi = conMan.getNetworkInfo(1).getState(); // wifi
			if (wifi == NetworkInfo.State.CONNECTED
					|| wifi == NetworkInfo.State.CONNECTING) {
				return true;
			}

			State mobile = conMan.getNetworkInfo(0).getState(); // mobile
																// ConnectivityManager.TYPE_MOBILE
			if (mobile == NetworkInfo.State.CONNECTED
					|| mobile == NetworkInfo.State.CONNECTING) {
				return true;
			}

		} catch (NullPointerException e) {
			return false;
		}

		return false;
	}

	// Session check request
	public void requestSessionCheck(AqueryCallbackListener listener, String url) {
		new AquerySessionCheck(listener, aquery, url);
	}

	// 로그인 요청
	public void requestLogin(AqueryCallbackListener listener, String url,
			String id, String pwd, String reg_id) {
		new AqueryLogin(listener, aquery, url, id, pwd, reg_id);
	}

	// 페이스북 로그인 요청
	public void requestFBLogin(FBLoginCallbackListener listener, String url,
			String token) {
		new AqueryFBLogin(listener, aquery, url, token);
	}

	// 구글 로그인 요청
	public void requestGoogleLogin(GoogleLoginCallbackListener listener,
			String url, String email_id, String firstname, String lastname,
			int sex, String profile_img_path, String code) {
		new AqueryGoogleLogin(listener, aquery, url, email_id, firstname,
				lastname, sex, profile_img_path, code);
	}

	// 검색 리스트 요청
	public void requestSearchList(AqueryCallbackListener listener, String url,
			String text) {

		mPreRequest = new AquerySearchList(listener, aquery, url, text, this);
		mPreRequest.childTag = AqueryWriteText.TAG;

	}

	// 검색 리스트 요청
	public void requestGetRoomList(AqueryGetRoomListCallbackListener listener,
			String url) {

		mPreRequest = new AqueryGetRoomList(listener, aquery, url, this);
		mPreRequest.childTag = AqueryGetRoomList.TAG;

	}

	// 검색 리스트 요청
	public void requestMakeRoom(AqueryCallbackListener listener, String url,
			String check_indate, String check_outdate, String gh_no) {

		mPreRequest = new AqueryMakeRoom(listener, aquery, url, check_indate,
				check_outdate, gh_no, this);
		mPreRequest.childTag = AqueryMakeRoom.TAG;
	}

	// 검색 리스트 요청
	public void requestMakeAccomodation(AqueryCallbackListener listener,
			String url, String gb_name, String gb_address, String gb_for,
			String gb_with, String gb_seeking, String check_indate,
			String check_outdate, String gb_latitude, String gb_longitude,
			String gb_channel) {

		mPreRequest = new AqueryMakeAccomodation(listener, aquery, url,
				gb_name, gb_address, gb_for, gb_with, gb_seeking, check_indate,
				check_outdate, gb_latitude, gb_longitude, gb_channel, this);
		mPreRequest.childTag = AqueryMakeAccomodation.TAG;
	}

	// 댓글 가져요기
	public void requestGetFeed(AqueryGetFeedCallbackListener listener,
			String url, String message_dtime) {
		mPreRequest = new AqueryGetFeed(listener, aquery, url, message_dtime,
				this);
		mPreRequest.childTag = AqueryGetFeed.TAG;
	}

	// 내 범위 설정
	public void requestUpdateBoudns(
			AqueryUpdateBoundsCallbackListener listener, String url,
			String gb_lat_start, String gb_lon_start, String gb_lat_end,
			String gb_lon_end) {
		mPreRequest = new AqueryUpdateBounds(listener, aquery, url,
				gb_lat_start, gb_lon_start, gb_lat_end, gb_lon_end, this);
		mPreRequest.childTag = AqueryUpdateBounds.TAG;
	}

	// 내 범위 안에 사람들 가져오기
	public void requestGetMapMembers(
			AqueryGetMapMembersCallbackListener listener, String url) {
		mPreRequest = new AqueryGetMapMembers(listener, aquery, url, this);
		mPreRequest.childTag = AqueryGetMapMembers.TAG;
	}

	// 프로필 리스트 요청
	public void requestGetPorfiles(AqueryGetProfilesCallbackListener listener,
			String url, String date) {
		mPreRequest = new AqueryGetProfiles(listener, aquery, url, date, this);
		mPreRequest.childTag = AqueryGetProfiles.TAG;

	}

	public void requestGetVisit(AqueryGetVisitCallbackListener listener,
			String url, String user_email_id) {
		mPreRequest = new AqueryGetVisit(listener, aquery, url, user_email_id,
				this);
		mPreRequest.childTag = AqueryGetVisit.TAG;

	}

	// 채팅방 만들기
	public void requestMakeChattingRoom(
			AqueryMakeChattingRoomCallbackListener listener, String url,
			ArrayList<ChattingRoomPeopleData> member) {
		mPreRequest = new AqueryMakeChattingRoom(listener, aquery, url, member,
				this);
		mPreRequest.childTag = AqueryMakeChattingRoom.TAG;

	}

	// 글쓰기
	public void requestWriteText(AqueryWriteTextCallbackListener listener,
			String url, String message) {

		mPreRequest = new AqueryWriteText(listener, aquery, url, message, this);
		mPreRequest.childTag = AqueryWriteText.TAG;
	}

	// 채팅방 삭제
	public void requestDeleteRoom(AqueryDeleteRoomCallbackListener listener,
			String url, String room_no) {

		mPreRequest = new AqueryDeleteRoom(listener, aquery, url, room_no);
		mPreRequest.childTag = AqueryWriteText.TAG;
	}

	// 회원가입 요청
	public void requestSignUp(AqueryCallbackListener listener, String url,
			String id, String pwd, String reg_id, String phoneNum,
			String firtName, String LastName, String sex, String location,
			String facebookId) {

		new AquerySignUp(listener, aquery, url, id, pwd, reg_id, phoneNum,
				firtName, LastName, sex, location, facebookId);
	}

	// 글쓰기
	public void requestGetProperties(
			AqueryGetPropertiesCallbackListener listener, String url) {
		new AqueryGetProperties(listener, aquery, url);
	}

	@Override
	public void AqueryFailCallback(String url, JSONObject json,
			AjaxStatus status, String tag) {

		if (json != null) {
			try {
				if (json.getString("result").equals("success")) {

				} else if (json.getString("result").equals("fail")) {
					if (json.getString("result_msg").equals("session null")) {
						// session null 처리
						AndroidQuery.getInstance().handleNetworkError(
								json.getString("result_msg"),
								new FBLoginCallbackListener() {
									@Override
									public void FBLoginCallback(String url,
											JSONObject json, AjaxStatus status) {
										if (json != null) {
											try {
												if (json.getString("result")
														.equals("success")) {
													// success login
													PropertyManager
															.getInstance()
															.setCookie(
																	status.getCookies()
																			.get(0)
																			.getName(),
																	status.getCookies()
																			.get(0)
																			.getValue());
													Log.e(TAG,
															"AqueryFailCallback reRequest");
													mPreRequest.reRequest();
												} else if (json.getString(
														"result")
														.equals("fail")) {
													// fail login
												}
											} catch (JSONException e) {
												e.printStackTrace();
											}
										}
									}
								});
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {

		}
	}

}
