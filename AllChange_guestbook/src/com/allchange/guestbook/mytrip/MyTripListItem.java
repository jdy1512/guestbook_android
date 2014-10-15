package com.allchange.guestbook.mytrip;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.allchange.guestbook.R;
import com.allchange.guestbook.booking.search.map.MapImageCacheStore;
import com.allchange.guestbook.chat.DBConstants;
import com.allchange.guestbook.chat.DBModel;
import com.allchange.guestbook.property.MyApplication;
import com.allchange.guestbook.property.PropertyManager;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

public class MyTripListItem extends FrameLayout {

	private static final String TAG = "MyTripListItem";
	private Context mContext;
	private TextView tv_name, tv_new, tv_time;
	String mData = "";
	MyTripListData RoomData = null;

	public String getTableName() {
		return RoomData.tableName;
	}

	public String getTarget() {
		return RoomData.people.get(0).email_id.trim();
	}

	public MyTripListItem(Context context, String d) {
		super(context);

		mContext = context;
		mData = d;
		init();
	}

	public MyTripListItem(Context context, MyTripListData d) {
		super(context);

		mContext = context;
		RoomData = d;
		init();
	}

	ImageView iv, iv1, iv2, iv3;

	private void init() {
		View v = LayoutInflater.from(getContext()).inflate(
				R.layout.chatting_box_view, this);

		v.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mListener != null) {
					mListener.onClickChattingRoom(RoomData.tableName);
				}
			}
		});

		AjaxCallback<Bitmap> bc = new AjaxCallback<Bitmap>() {
			@Override
			public void callback(String url, Bitmap bitmap, AjaxStatus status) {
				if (bitmap != null) {
					MapImageCacheStore.getInstance().put(url, bitmap);
					iv.setImageBitmap(MyApplication.getCircledBitmap(bitmap, url));
					iv.invalidate();
					MyTripListItem.this.invalidate();
				}
			}
		};

		iv = (ImageView) v.findViewById(R.id.chatting_box_view_iv);
		iv1 = (ImageView) v.findViewById(R.id.chatting_box_view_iv1);
		iv2 = (ImageView) v.findViewById(R.id.chatting_box_view_iv2);
		iv3 = (ImageView) v.findViewById(R.id.chatting_box_view_iv3);

		ArrayList<ImageView> iv_list = new ArrayList<ImageView>();
		iv_list.add(iv);
		iv_list.add(iv1);
		iv_list.add(iv2);
		iv_list.add(iv3);
		tv_name = (TextView) v.findViewById(R.id.chatting_box_view_tv);
		tv_new = (TextView) v.findViewById(R.id.chatting_box_tv_new);
		tv_time = (TextView) v.findViewById(R.id.chatting_box_tv_date);

		StringBuilder sb = new StringBuilder();
		if (RoomData != null) {
			for (int i = 0; i < RoomData.people.size(); i++) {
				// if (MapImageCacheStore.getInstance().get(
				// RoomData.people.get(i).profile_img_path.trim()) != null) {
				// iv_list.get(i).setImageBitmap(
				// MapImageCacheStore.getInstance().get(
				// RoomData.people.get(i).profile_img_path
				// .trim()));
				// } else {
				sb.append(RoomData.people.get(i).lastname
						+ RoomData.people.get(i).firstname);
				if (i < RoomData.people.size() - 1) {
					sb.append(", ");
				}
				if (i == 1) {
					LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
							LayoutParams.MATCH_PARENT,
							LayoutParams.MATCH_PARENT);
					lp.weight = 1;
					iv_list.get(0).setLayoutParams(lp);
				} else if (i == 3) {
					LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
							LayoutParams.MATCH_PARENT,
							LayoutParams.MATCH_PARENT);
					lp.weight = 1;
					iv_list.get(2).setLayoutParams(lp);
				}
				AQuery aquery = new AQuery(mContext);
				bc.cookies(PropertyManager.getInstance().getCookie());
				// aquery.id(iv_list.get(i)).image(
				// RoomData.people.get(i).profile_img_path.trim(), false,
				// false);
				aquery.ajax(RoomData.people.get(i).profile_img_path.trim(),
						Bitmap.class, bc);
			}

			tv_name.setText(sb.toString());
			// iv.setImageBitmap(MapImageCacheStore.getInstance().get(
			// ProfileData.profile_img_path.trim()));
			// tv_name.setText(ProfileData.email_id);
		} else {
			tv_name.setText(mData);
		}
		// if
		// (RoomData.people.get(0).email_id.trim().equals("kl4684@gmail.com")) {
		// nameBar.setBackgroundColor(Color.argb(55, 00, 00, 0xff));
		// }

		getLastMessageInDB();
	}

	private void getLastMessageInDB() {
		Cursor c = DBModel.getInstance().queryLastChat(RoomData.tableName);
		int nameIndex = c.getColumnIndex(DBConstants.ChatTable.COLUMN_EMAIL);
		int descripIndex = c
				.getColumnIndex(DBConstants.ChatTable.COLUMN_MESSAGE);
		int dateIndex = c
				.getColumnIndex(DBConstants.ChatTable.COLUMN_MESSAGE_TIME);
		while (c.moveToNext()) {
			String email = c.getString(nameIndex);
			String descrip = c.getString(descripIndex);
			String date = c.getString(dateIndex);
			// MyData md = new MyData();
			// md.email = email;
			// md.name = ChatCacheStore.getInstance().getFirstNameResovler()
			// .get(email)
			// + ChatCacheStore.getInstance().getLastNameResovler()
			// .get(email);
			// md.imageUrl = ChatCacheStore.getInstance().getImageResovler()
			// .get(email);
			// md.description = descrip;
			// md.date = date;
			setNewText(descrip, date);
		}

	}

	final Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			String str = msg.getData().getString("message");
			String time = msg.getData().getString("time");
			tv_new.setText(str);
			tv_new.invalidate();
			tv_time.setText(time);
			tv_time.invalidate();
			MyTripListItem.this.invalidate();
			// Log.e(TAG, "setNewText : " + str);
		}
	};

	public void setNewText(final String str, final String time) {
		new Thread() {
			public void run() {
				Message msg = mHandler.obtainMessage();
				Bundle b = new Bundle();
				b.putString("message", str);
				b.putString("time", time);
				msg.setData(b);
				mHandler.sendMessage(msg);
			}
		}.start();
	}

	public interface OnClickChattingRoomListener {
		public void onClickChattingRoom(String tableName);

		public void onDestoryChattingRoom(MyTripListItem mView);
	}

	OnClickChattingRoomListener mListener;
	MyTripListItem mView;

	public void setOnClickChattingRoomListener(
			OnClickChattingRoomListener listener, MyTripListItem v) {
		mListener = listener;
		mView = v;
	}

}