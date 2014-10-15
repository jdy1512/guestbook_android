package com.allchange.guestbook.chatroom.list;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import android.annotation.SuppressLint;
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
import com.allchange.guestbook.dialog.Dialog_Chat_Delete;
import com.allchange.guestbook.dialog.Dialog_Chat_Delete.OnDeleteListener;
import com.allchange.guestbook.picker.Dtime;
import com.allchange.guestbook.property.MyApplication;
import com.allchange.guestbook.property.PropertyManager;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

public class ChatRoomListItem extends FrameLayout {

	private static final String TAG = "ChatRoomListItem";
	private Context mContext;
	private TextView tv_name, tv_new, tv_time;
	String mData = "";
	ChatRoomListData RoomData = null;
	ImageView iv_delete;
	ChatRoomListFragment mParent;

	public String getTableName() {
		return RoomData.tableName;
	}

	public String getTarget() {
		return RoomData.people.get(0).email_id.trim();
	}

	// public ChatRoomListItem(Context context, String d) {
	// super(context);
	// mContext = context;
	// mData = d;
	// init();
	// }

	public ChatRoomListItem(Context context, ChatRoomListData d,
			ChatRoomListFragment p) {
		super(context);
		mParent = p;
		mContext = context;
		RoomData = d;
		init();
	}

	ImageView iv, iv1, iv2, iv3;

	@SuppressLint("NewApi")
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
					ChatRoomListItem.this.invalidate();
				}
			}
		};

		iv_delete = (ImageView) v.findViewById(R.id.chatting_box_iv_delete);
		if (RoomData.status_edit) {
			// 편집상태일때
		} else {
			// 기본상태일때
			iv_delete.setVisibility(View.GONE);
		}
		iv_delete.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (RoomData.status_edit) {

					Dialog_Chat_Delete dcd = new Dialog_Chat_Delete();
					dcd.setOnDeleteListener(new OnDeleteListener() {
						@Override
						public void onDelete() {
							DBModel.getInstance().deleteChat(
									RoomData.people.get(0).email_id.trim());
							if (mListener != null) {
								mListener.onDestoryChattingRoom(mView);
							}
						}
					});
					dcd.show(mParent.getFragmentManager(), TAG);
				}
			}
		});

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
				if (RoomData.people.get(i).lastname == null
						|| RoomData.people.get(i).lastname.equals("null")) {
					sb.append("");
				}

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

				try {
					aquery.ajax(RoomData.people.get(i).profile_img_path.trim(),
							Bitmap.class, bc);
				} catch (Exception e) {
				}

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

		// UI 변경전에 있었던 삭제 버튼
		// ImageButton btn = (ImageButton) v
		// .findViewById(R.id.chatting_box_view_btn_delete);
		// btn.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// DBModel.getInstance().deleteChat(
		// RoomData.people.get(0).email_id.trim());
		// if (mListener != null) {
		// mListener.onDestoryChattingRoom(mView);
		// }
		// }
		// });
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

			// 계산하기 위해서String millisecond를 calendar형으로 바꿔준다.
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(Long.parseLong(time));

			// 하루가 지났는지 확인하고 결과에 따라 형태를 바꿔준다
			if (Dtime.instance().getGMTCalendar().compareTo(calendar) > 1) {
				DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
				tv_time.setText(df.format(calendar.getTime()));
			} else {
				DateFormat df = new SimpleDateFormat("aaa hh:mm");
				tv_time.setText(df.format(calendar.getTime()));
			}

			tv_time.invalidate();
			ChatRoomListItem.this.invalidate();
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

		public void onDestoryChattingRoom(ChatRoomListItem mView);
	}

	OnClickChattingRoomListener mListener;
	ChatRoomListItem mView;

	public void setOnClickChattingRoomListener(
			OnClickChattingRoomListener listener, ChatRoomListItem v) {
		mListener = listener;
		mView = v;
	}

}