package com.allchange.guestbook.main.profiles;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.allchange.guestbook.R;
import com.allchange.guestbook.aquery.AndroidQuery;
import com.allchange.guestbook.aquery.get.profiles.AqueryGetProfilesCallbackListener;
import com.allchange.guestbook.aquery.get.profiles.DataForProfiles;
import com.allchange.guestbook.calendar.ItemData;
import com.allchange.guestbook.chat.ChatCacheStore;
import com.allchange.guestbook.chat.DBChatListData;
import com.allchange.guestbook.chat.DBModel;
import com.allchange.guestbook.chat.MessageActivity;
import com.allchange.guestbook.dialog.Dialog_Simple_Profile;
import com.allchange.guestbook.dialog.Dialog_Simple_Profile.OnSimpleProfileListener;
import com.allchange.guestbook.picker.PickerFragment;
import com.allchange.guestbook.picker.PickerFragment.GuestBookPickerListener;
import com.allchange.guestbook.property.PropertyManager;
import com.allchange.guestbook.url.Url;
import com.androidquery.callback.AjaxStatus;

public class TripPalsListFragment extends Fragment implements
		ProfileListAdapter.OnClickProfileListener,
		AqueryGetProfilesCallbackListener, OnSimpleProfileListener,
		GuestBookPickerListener {

	// Scrolling flag
	public static final String TAG = "TripPalsListFragment";
	ListView listView;
	ArrayList<ItemData> mItemdata = new ArrayList<ItemData>();
	ArrayList<ChattingRoomPeopleData> mData;
	DataForProfiles profileData = new DataForProfiles();
	TextView tv_calen;
	private PickerFragment picker;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater
				.inflate(R.layout.main_fragment_list, container, false);
		tv_calen = (TextView) v.findViewById(R.id.main_list_tv_calen);

		mData = new ArrayList<ChattingRoomPeopleData>();

		listView = (ListView) v.findViewById(R.id.main_list_travelers);
		ProfileListAdapter profileListAdapter = new ProfileListAdapter(
				getActivity(), mData);
		profileListAdapter.setOnClickProfileListener(this);
		listView.setAdapter(profileListAdapter);
		if (isSetMyHouse()) {
			// AndroidQuery.getInstance().requestGetRoomList(this,
			// Url.SERVER_GET_ROOM_LIST);
		}

		// TripPalMainActivity activity = (TripPalMainActivity) getActivity();
		// activity.setChangedToListListener(this);

		// PickerFragment 클래스 init세팅(picker전체길이, 시작날짜, 끝날짜)
		picker = new PickerFragment();

		if (isSetMyHouse()) {
			Button btn = (Button) v.findViewById(R.id.main_list_btn_today);
			btn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// 피커 아이템을 오늘 날짜로 이동
					picker.scrollTo("today");
				}
			});
			btn = (Button) v.findViewById(R.id.main_list_btn_checkin);
			btn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// 피커 아이템을 오늘 날짜로 이동
					picker.scrollTo(PropertyManager.getInstance().getCheckin());
				}
			});

			int screen_x = getResources().getDisplayMetrics().widthPixels;
			picker.init(screen_x, PropertyManager.getInstance().getCheckin(),
					PropertyManager.getInstance().getCheckout());
			picker.setOnPickerListener(this);
			getChildFragmentManager().beginTransaction()
					.add(R.id.main_list_picker, picker).commit();
		}

		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		getView().post(new Runnable() {
			@Override
			public void run() {
				// code you want to run when view is visible for the first time
				picker.scrollTo(PropertyManager.getInstance().getCheckin());
			}
		});
		super.onActivityCreated(savedInstanceState);
	}

	private boolean isSetMyHouse() {

		if (PropertyManager.getInstance().getCheckin().equals("")) {
			return false;
		}
		return true;
	}

	private ChattingRoomPeopleData getPeoPleInfo(String email) {
		ChattingRoomPeopleData pData = new ChattingRoomPeopleData();
		pData.email_id = email;
		pData.firstname = ChatCacheStore.getInstance().getFirstNameResovler()
				.get(email);
		pData.lastname = ChatCacheStore.getInstance().getLastNameResovler()
				.get(email);
		pData.profile_img_path = ChatCacheStore.getInstance()
				.getImageResovler().get(email);

		return pData;
	}

	@Override
	public void onClickProfile(int position) {

		Dialog_Simple_Profile dsp = new Dialog_Simple_Profile();
		dsp.setOnSimpleProfileListener(this);
		dsp.setProfileData(mData.get(position));
		dsp.show(getFragmentManager(), TAG);
	}

	@Override
	public void AqueryGetProfilesCallback(String url, JSONObject json,
			AjaxStatus status) {
		String TAG_DETAIL = " AqueryGetProfilesCallback";
		// Dialog_Loading.getInstance().dismiss();

		mData.clear();
		if (json != null) {
			try {
				if (json.getString("result").equals("success")) {
					JSONArray js = json.getJSONArray("trippal");

					for (int i = 0; i < js.length(); i++) {
						ChattingRoomPeopleData data = new ChattingRoomPeopleData();
						data.email_id = js.getJSONObject(i).getString(
								"email_id");
						data.firstname = js.getJSONObject(i).getString(
								"firstname");
						data.lastname = js.getJSONObject(i).getString(
								"lastname");

						data.profile_img_path = js.getJSONObject(i).getString(
								"profile_img_path");
						try {
							data.gb_for = js.getJSONObject(i).getString(
									"gb_for");
						} catch (Exception e) {
						}
						try {
							data.gb_with = js.getJSONObject(i).getString(
									"gb_with");
						} catch (Exception e) {
						}
						try {
							data.gb_seeking = js.getJSONObject(i).getString(
									"gb_seeking");
						} catch (Exception e) {
						}
						try {
							data.gb_latitude = Double.parseDouble(js
									.getJSONObject(i).getString("gb_latitude"));
						} catch (Exception e) {
						}
						try {
							data.gb_longitude = Double
									.parseDouble(js.getJSONObject(i).getString(
											"gb_longitude"));
						} catch (Exception e) {
						}

						// 채팅방에서 쓰기위한 데이터?
						ChatCacheStore.getInstance().putAtResovler(
								data.email_id, data.firstname, data.lastname,
								data.profile_img_path);
						if (!data.email_id.equals(PropertyManager.getInstance()
								.getId())) {
							mData.add(data);
						}

					}
				} else if (json.getString("result").equals("fail")) {
					Log.d(TAG + TAG_DETAIL, json.getString("result_msg"));
				}
			} catch (JSONException e) {

				Log.e(TAG + TAG_DETAIL, "parsing error\n" + json.toString());
				e.printStackTrace();
			}
		} else {
			Log.e(TAG + TAG_DETAIL, "json is null");
			Log.e(TAG + TAG_DETAIL, status.getMessage());
			Log.e(TAG + TAG_DETAIL, status.getRedirect());
		}
		ProfileListAdapter profileListAdapter = new ProfileListAdapter(
				getActivity(), mData);
		profileListAdapter.setOnClickProfileListener(TripPalsListFragment.this);
		listView.setAdapter(profileListAdapter);
	}

	@Override
	public void onStart() {

		super.onStart();
	}

	private void startMessageActivity(String tableName) {

		Intent i = new Intent(TripPalsListFragment.this.getActivity(),
				MessageActivity.class);
		i.putExtra("data", tableName);
		// Bundle bundle = new Bundle();
		// bundle.putParcelable("data", (Parcelable) member.get(0));
		// i.putExtras(bundle);
		startActivity(i);
	}

	@Override
	public void onResume() {
		// picker 다시그림 - 숙소를 변경했을때 날이 바뀌어야됨
		Log.d(TAG, "onChangedTripPalTap");

		// if (mPicker != null) {
		// ll_for_picker.removeView(mPicker);
		// }
		// mPicker = new GuestBookPicker(getActivity(), null);
		// mPicker.setOnPickerListener(this);
		// LinearLayout.LayoutParams lp = new LayoutParams((int) getResources()
		// .getDimension(R.dimen.picker_width), (int) getResources()
		// .getDimension(R.dimen.picker_height));
		// mPicker.setLayoutParams(lp);
		// ll_for_picker.addView(mPicker);

		// 유저 프로필들이 변해야됨
		ProfileListAdapter profileListAdapter = new ProfileListAdapter(
				getActivity(), mData);
		profileListAdapter.setOnClickProfileListener(this);
		listView.setAdapter(profileListAdapter);
		if (isSetMyHouse()) {
			// AndroidQuery.getInstance().requestGetRoomList(this,
			// Url.SERVER_GET_ROOM_LIST);
		}
		super.onResume();

	}

	// dialog_simple_profile에서 chat을 눌렀을때
	@Override
	public void onClickChat(ChattingRoomPeopleData data) {
		if (DBModel.getInstance().isChatCreated(data.email_id)) {
			startMessageActivity(DBModel.getInstance().selectChatTableName(
					data.email_id));
		} else {
			ArrayList<ChattingRoomPeopleData> member = new ArrayList<ChattingRoomPeopleData>();
			member.add(data);
			DBChatListData d = new DBChatListData();
			// 테이블이름 정하기 전에 정해진 테이블 개수 먼저 파악하기
			int size = DBModel.getInstance().getChatListSize();
			d.tableName = DBModel.DB_NAME_CHAT + size;
			d.type = "single";
			d.target = data.email_id;
			// 앱 디비에 채팅방 넣기
			DBModel.getInstance().insert(d);

			// 채팅 액티비티 실행하기
			Intent i = new Intent(getActivity(), MessageActivity.class);
			i.putExtra("data", d.tableName);
			startActivity(i);
			// startMessageActivity(d.tableName);
		}
		// AndroidQuery.getInstance().requestMakeChattingRoom(this,
		// Url.SERVER_MAKE_CHATTING_ROOM, member);
	}

	private String mSelectedDate = "";
	private static final int UPDATE_MONTH = 0;

	final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case UPDATE_MONTH:
				String date = (String) msg.obj;
				String[] ff = getResources()
						.getStringArray(R.array.month_array);
				tv_calen.setText(ff[Integer.parseInt(date.substring(4, 6)) - 1]
						+ " / " + date.substring(0, 4));
				break;

			default:
				break;
			}

			// Toast.makeText(getActivity(), "handler", 0).show();
		}
	};

	@Override
	public void pickerCallback(final String date) {
		// Log.e(TAG, "date : " + date);
		// Log.e(TAG, month_word[Integer.parseInt(date.substring(4, 6))] + " "
		// + date.substring(0, 4));
		String TAG_DETAIL = "pickerCallback";

		Thread tt = new Thread(new Runnable() {
			public void run() {
				Message message = Message.obtain();
				message.what = UPDATE_MONTH;
				message.obj = date;
				handler.sendMessage(message);
			}
		});
		tt.start();
		// Runnable task = ;

		mSelectedDate = date;
		try {
			if (isSetMyHouse()) {
				AndroidQuery.getInstance().requestGetPorfiles(this,
						Url.SERVER_GET_PROFILES, mSelectedDate);
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		}

	}

}
