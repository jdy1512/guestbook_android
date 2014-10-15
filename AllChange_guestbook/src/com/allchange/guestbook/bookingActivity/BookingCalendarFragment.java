package com.allchange.guestbook.bookingActivity;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.allchange.guestbook.R;
import com.allchange.guestbook.aquery.AndroidQuery;
import com.allchange.guestbook.aquery.AqueryCallbackListener;
import com.allchange.guestbook.booking.search.map.ParsingData;
import com.allchange.guestbook.bookingActivity.BookingPurposeFragment.OnSetPurposeListener;
import com.allchange.guestbook.calendar.CalendarAdapter;
import com.allchange.guestbook.calendar.CalendarData;
import com.allchange.guestbook.calendar.CalendarItem;
import com.allchange.guestbook.calendar.CalendarManager;
import com.allchange.guestbook.calendar.ItemData;
import com.allchange.guestbook.calendar.CalendarManager.NoComparableObjectException;
import com.allchange.guestbook.dialog.Dialog_Loading;
import com.allchange.guestbook.property.PropertyManager;
import com.allchange.guestbook.url.Url;
import com.androidquery.callback.AjaxStatus;

public class BookingCalendarFragment extends Fragment implements
		OnClickListener, OnSetPurposeListener, AqueryCallbackListener {

	public static final String TAG = "BookingCalendarFragment";
	private ParsingData mData;
	/** calendar **/
	GridView gridView;
	CalendarAdapter mAdapter;
	TextView tv_helper;
	Button btn_done;
	ArrayList<ItemData> mItemdata = new ArrayList<ItemData>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.booking_fragment_calendar,
				container, false);

		tv_helper = (TextView) v.findViewById(R.id.booking_calendar_tv_helper);

		// ll_next = (LinearLayout)
		// v.findViewById(R.id.dilaog_map_calen_layout);

		// ll_next.setOnClickListener(this);

		btn_done = (Button) v.findViewById(R.id.booking_calendar_btn_apply);
		btn_done.setEnabled(false);
		btn_done.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Dialog_Loading.getInstance().show(getFragmentManager(), TAG);

				// gb_for : 방문목적 – for (business,festival,sightseeing)
				// gb_with : 방문목적 – with (myself,friends,family)
				// gb_seeking

				AndroidQuery.getInstance().requestMakeAccomodation(
						BookingCalendarFragment.this,
						Url.SERVER_CREATE_ACCOMMODATION, mData.title,
						mData.address, mData.gb_visit[0], mData.gb_visit[1],
						mData.gb_visit[2], mData.check_indate,
						mData.check_outdate, mData.latitude, mData.longitude,
						"airbnb");
			}
		});

		/***************************/
		/** calender View setting **/
		/***************************/

		final TextView titleView = (TextView) v
				.findViewById(R.id.booking_confirm_title);
		CalendarData data = CalendarManager.getInstance().getCalendarData();
		titleView.setText("" + data.year + "." + (data.month + 1) + ".");
		mAdapter = new CalendarAdapter(getActivity(), data);

		mAdapter.setCalendarData(CalendarManager.getInstance()
				.getCalendarData());

		mItemdata.add(new ItemData(2012, 4, 10, "A"));
		mItemdata.add(new ItemData(2012, 4, 11, "B"));
		mItemdata.add(new ItemData(2012, 4, 12, "C"));
		mItemdata.add(new ItemData(2012, 4, 15, "D"));
		mItemdata.add(new ItemData(2012, 4, 21, "E"));
		mItemdata.add(new ItemData(2012, 4, 21, "F"));

		ImageView btn = (ImageView) v
				.findViewById(R.id.booking_confirm_nextMonth);
		btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CalendarData data = CalendarManager.getInstance()
						.getNextMonthCalendarData();
				titleView
						.setText("" + data.year + "." + (data.month + 1) + ".");
				mAdapter.setCalendarData(data);
			}
		});

		btn = (ImageView) v.findViewById(R.id.booking_confirm_lastMonth);
		btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.e(TAG, "onClick lastMonth");
				CalendarData data = CalendarManager.getInstance()
						.getLastMonthCalendarData();
				titleView
						.setText("" + data.year + "." + (data.month + 1) + ".");
				mAdapter.setCalendarData(data);
			}
		});
		gridView = (GridView) v.findViewById(R.id.booking_confirm_gridView);
		try {
			CalendarManager.getInstance().setDataObject(mItemdata);
		} catch (NoComparableObjectException e) {
			e.printStackTrace();
		}

		gridView.setAdapter(mAdapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,
					long id) {
				// v.setBackgroundColor(Color.BLACK);
				CalendarItem item = (CalendarItem) mAdapter.getItem(position);

				if (item.typeOfDay != CalendarItem.TYPE_OCCUPIED_DAY) {
					if (CalendarManager.getInstance().arrayClickItems.size() == 0) {
						Log.e(TAG, "size() == 0");
						// 스타트지점이 없고 예약가능한 날이면
						if (item.typeOfDay == CalendarItem.TYPE_UNOCCUPIED_DAY) {
							Log.e(TAG, "size() == 0 TYPE_UNOCCUPIED_DAY");
							// 예약 시작일로 저장한다
							item.typeOfDay = CalendarItem.TYPE_RESERVE_DAY_START;
							CalendarManager.getInstance().arrayClickItems
									.add(item);
							// textView_Departs.setText(item.year + "-"
							// + item.getMonthString() + "-"
							// + item.getDayString());

							mData.check_indate = "" + item.year
									+ item.getMonthString()
									+ item.getDayString();
							// DiscoverSearchData.getInstance().user_check_indate
							// = ""
							// + item.year
							// + item.getMonthString()
							// + item.getDayString();
						} else if (item.typeOfDay == CalendarItem.TYPE_RESERVE_DAY_START) {
							Log.e(TAG, "size() == 0 TYPE_RESERVE_DAY_START");
							item.typeOfDay = CalendarItem.TYPE_UNOCCUPIED_DAY;
							CalendarManager.getInstance().arrayClickItems
									.remove(item);
							// textView_Departs.setText("Select date");

							mData.check_indate = "";
							// DiscoverSearchData.getInstance().user_check_indate
							// = "";
						}
						// 만약 스타트 지점이 있으면
					} else if (CalendarManager.getInstance().arrayClickItems
							.size() == 1) {
						if (item.typeOfDay == CalendarItem.TYPE_UNOCCUPIED_DAY) {
							// 비어있는 날이라면 마지막날로 추가한다
							item.typeOfDay = CalendarItem.TYPE_RESERVE_DAY_END;
							CalendarManager.getInstance().arrayClickItems
									.add(item);
							// textView_Arrives.setText(item.year + "-"
							// + item.getMonthString() + "-"
							// + item.getDayString());
							// calenView.setVisibility(View.GONE);

							mData.check_outdate = "" + item.year
									+ item.getMonthString()
									+ item.getDayString();

							mData.check_outdate = "" + item.year
									+ item.getMonthString()
									+ item.getDayString();
							// DiscoverSearchData.getInstance().user_check_outdate
							// = ""
							// + item.year
							// + item.getMonthString()
							// + item.getDayString();
							// setMountPrice();
						} else if (item.typeOfDay == CalendarItem.TYPE_RESERVE_DAY_START) {
							// 출발 날이라면
							CalendarManager.getInstance().arrayClickItems
									.remove(item);
							// textView_Departs.setText("Select date");

							mData.check_indate = "";
							// DiscoverSearchData.getInstance().user_check_indate
							// = "";
						}
					} else if (CalendarManager.getInstance().arrayClickItems
							.size() == 2) {
						if (item.typeOfDay == CalendarItem.TYPE_RESERVE_DAY_START) {

						} else if (item.typeOfDay == CalendarItem.TYPE_RESERVE_DAY_END) {
							// 예약 마지막일을 터치하면 끝나는 지점을 배열에서 뺀다
							CalendarManager.getInstance().arrayClickItems
									.remove(1);
							// textView_Arrives.setText("-");
							mData.check_outdate = "";
							// DiscoverSearchData.getInstance().user_check_outdate
							// = "";
						} else {
							// 만약 끝나는 지점이 이미 있으면
							// 끝나는 지점을 배열에서 뺀다
							CalendarManager.getInstance().arrayClickItems
									.remove(1);
							// 다시 설정되는 예약 마지막 날을 추가한다
							item.typeOfDay = CalendarItem.TYPE_RESERVE_DAY_END;
							CalendarManager.getInstance().arrayClickItems
									.add(item);
							// textView_Arrives.setText(item.year + "-"
							// + item.getMonthString() + "-"
							// + item.getDayString());
							// calenView.setVisibility(View.GONE);

							mData.check_outdate = "" + item.year
									+ item.getMonthString()
									+ item.getDayString();
							// DiscoverSearchData.getInstance().user_check_outdate
							// = ""
							// + item.year
							// + item.getMonthString()
							// + item.getDayString();
							// setMountPrice();
						}
					}
				}
				if (CalendarManager.getInstance().arrayClickItems.size() != 2) {
					tv_helper.setText(getResources().getString(
							R.string.calendar_bad));
					btn_done.setEnabled(false);
				} else {
					tv_helper.setText(getResources().getString(
							R.string.calendar_fine));
					btn_done.setEnabled(true);
				}
				CalendarData data = CalendarManager.getInstance()
						.getCalendarData();
				mAdapter.setCalendarData(data);
			}
		});
		return v;
	}

	public void setInfoData(ParsingData data) {
		mData = data;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	public interface OnConfirmAccamodateListener {
		public void onConfirmAccamodate();
	}

	OnConfirmAccamodateListener mListener;

	public void setOnConfirmAccamodateListener(
			OnConfirmAccamodateListener listener) {
		mListener = listener;
	}

	@Override
	public void onSetPurpose(ParsingData data) {
		mData = data;
	}

	@Override
	public void AqueryCallback(String url, JSONObject json, AjaxStatus status) {
		// TODO Auto-generated method stub

		if (json != null) {
			try {
				if (json.getString("result").equals("success")) {
					// Intent intent = new Intent(getActivity(),
					// RoomActivity.class);
					// intent.putExtra(RoomActivity.EXTRA_ROOM_NUMBER,
					// mAdapter.getDataWithPostion(position).room_no);
					// startActivity(intent);
					PropertyManager.getInstance().setLatitude(mData.latitude);
					PropertyManager.getInstance().setLongitude(mData.longitude);
					PropertyManager.getInstance()
							.setCheckin(mData.check_indate);
					PropertyManager.getInstance().setCheckout(
							mData.check_outdate);
					// 달력클래스에 저장된 체크인아웃 데이터를 지워준다.
					CalendarManager.getInstance().arrayClickItems.clear();
					Dialog_Loading.getInstance().dismiss();
					NavUtils.navigateUpFromSameTask(getActivity());

					// TODO 메인 페이지로 이동시키고 슬라이딩 메뉴도 닫고 새로고침도 한번 해줘야됨
					// if (mListener != null) {
					// mListener.onConfirm();
					// }

					// this.dismiss();
				} else if (json.getString("result").equals("fail")) {
					Log.d(TAG, "fail : " + json.getString("result_msg"));
				}
			} catch (JSONException e) {
				Log.e(TAG, "parsing error");
				e.printStackTrace();
			}
		} else {
			Log.e(TAG, "json is null");
			Log.e(TAG, status.getMessage());
			Log.e(TAG, status.getRedirect());
		}
	}
}
