package com.allchange.guestbook.main.profiles;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.allchange.guestbook.R;
import com.allchange.guestbook.booking.search.map.MapImageCacheStore;
import com.allchange.guestbook.chat.DBModel;
import com.allchange.guestbook.property.PropertyManager;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

public class ChattingBoxView extends FrameLayout {

	private static final String TAG = "ChattingBoxView";
	private Context mContext;
	private TextView tv_name, tv_new;
	private View nameBar;
	String mData = "";
	ChattingRoomInfoData RoomData = null;

	public String getTableName() {
		return RoomData.tableName;
	}

	public String getTarget() {
		return RoomData.people.get(0).email_id.trim();
	}

	public ChattingBoxView(Context context, String d) {
		super(context);

		mContext = context;
		mData = d;
		init();
	}

	public ChattingBoxView(Context context, ChattingRoomInfoData d) {
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
				// Toast.makeText(mContext, "click", Toast.LENGTH_SHORT).show();
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
					iv.setImageBitmap(bitmap);
					iv.invalidate();
					ChattingBoxView.this.invalidate();
				}
			}
		};

		iv = (ImageView) v.findViewById(R.id.chatting_box_view_iv);
		iv1 = (ImageView) v.findViewById(R.id.chatting_box_view_iv1);
		iv2 = (ImageView) v.findViewById(R.id.chatting_box_view_iv2);
		iv3 = (ImageView) v.findViewById(R.id.chatting_box_view_iv3);
		nameBar = (View) v.findViewById(R.id.chatting_box_view_nameBar);

		ArrayList<ImageView> iv_list = new ArrayList<ImageView>();
		iv_list.add(iv);
		iv_list.add(iv1);
		iv_list.add(iv2);
		iv_list.add(iv3);
		tv_name = (TextView) v.findViewById(R.id.chatting_box_view_tv);
		tv_new = (TextView) v.findViewById(R.id.chatting_box_tv_new);

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
				aquery.id(iv_list.get(i)).image(
						RoomData.people.get(i).profile_img_path.trim(), false,
						false);
				// aquery.ajax(RoomData.people.get(i).trim(), Bitmap.class,
				// bc);
				// }
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

		// UI변경전에 있었던 삭제 버튼
		// ImageButton btn = (ImageButton) v
		// .findViewById(R.id.chatting_box_view_btn_delete);
		// btn.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// DBModel.getInstance().deleteChat(
		// RoomData.people.get(0).email_id.trim());
		// mListener.onDestoryChattingRoom(mView);
		// }
		// });

	}

	final Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			String str = msg.getData().getString("message");
			tv_new.setText(str);
			tv_new.invalidate();
			ChattingBoxView.this.invalidate();
			// Log.e(TAG, "setNewText : " + str);
		}
	};

	public void setNewText(final String str) {
		new Thread() {
			public void run() {
				Message msg = mHandler.obtainMessage();
				Bundle b = new Bundle();
				b.putString("message", str);
				msg.setData(b);
				mHandler.sendMessage(msg);
			}
		}.start();
	}

	public interface OnClickChattingRoomListener {
		public void onClickChattingRoom(String tableName);

		public void onDestoryChattingRoom(ChattingBoxView mView);
	}

	OnClickChattingRoomListener mListener;
	ChattingBoxView mView;

	public void setOnClickChattingRoomListener(
			OnClickChattingRoomListener listener, ChattingBoxView v) {
		mListener = listener;
		mView = v;
	}

}