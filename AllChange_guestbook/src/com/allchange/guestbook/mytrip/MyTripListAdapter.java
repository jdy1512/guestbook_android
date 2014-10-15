package com.allchange.guestbook.mytrip;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.allchange.guestbook.chatroom.list.ChatRoomListData;

public class MyTripListAdapter extends BaseAdapter {

	ArrayList<MyTripListData> mData = new ArrayList<MyTripListData>();
	Context mContext;
	MyTripFragment mParent;

	public MyTripListAdapter(Context c, MyTripFragment f,
			ArrayList<MyTripListData> data) {
		// TODO Auto-generated constructor stub
		mData = data;
		mParent = f;
		mContext = c;
	}

	public MyTripListData getDataWithPostion(int pos) {
		return mData.get(pos);
	}

	public void setListData(ArrayList<MyTripListData> data) {
		mData = data;
	}

	public void addListData(MyTripListData item) {
		mData.add(item);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		MyTripListItem view = null;

		if (convertView == null) {
			view = new MyTripListItem(mContext, mData.get(position));
		} else {
			view = (MyTripListItem) convertView;
		}

		return view;

	}

}
