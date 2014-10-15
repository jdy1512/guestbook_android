package com.allchange.guestbook.main.profiles;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class ProfileListAdapter extends BaseAdapter {

	Context mContext = null;
	ArrayList<ChattingRoomPeopleData> mData = new ArrayList<ChattingRoomPeopleData>();

	// -----------------------------------------------------------
	// imageIDs는 이미지 파일들의 리소스 ID들을 담는 배열입니다.
	// 이 배열의 원소들은 자식 뷰들인 ImageView 뷰들이 무엇을 보여주는지를
	// 결정하는데 활용될 것입니다.

	public ProfileListAdapter(Context context,
			ArrayList<ChattingRoomPeopleData> data) {
		this.mContext = context;
		this.mData = data;
	}

	public int getCount() {
		return (null != mData) ? mData.size() : 0;
	}

	public Object getItem(int position) {
		return (null != mData) ? mData.get(position) : 0;
	}

	public long getItemId(int position) {
		return position;
	}

	public interface ProfileClickListener {
		public void profileClick(int position);
	}

	public View getView(final int position, View convertView, ViewGroup parent) {

		UserProfileView view = null;

		if (convertView == null) {
			view = new UserProfileView(mContext, mData.get(position));
		} else {
			view = (UserProfileView) convertView;
		}
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mListener != null) {
					mListener.onClickProfile(position);
				}
			}
		});
		// view.setReplyData(mData.info.get(position).reply.get(childPosition));
		return view;

	}

	public interface OnClickProfileListener {
		public void onClickProfile(int position);
	}

	OnClickProfileListener mListener;

	public void setOnClickProfileListener(OnClickProfileListener listener) {
		mListener = listener;
	}
}
