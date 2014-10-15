package com.allchange.guestbook.main.feedchat;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class ProfileListAdapter extends BaseAdapter {

	ArrayList<ProfileData> mData = new ArrayList<ProfileData>();
	Context mContext;

	public ProfileListAdapter(Context c, ArrayList<ProfileData> data) {
		// TODO Auto-generated constructor stub
		mData = data;
		mContext = c;
	}

	public void setListData(ArrayList<ProfileData> data) {
		mData = data;
	}

	public void addListData(ProfileData item) {
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
		ProfileListItem view = null;

		if (convertView == null) {
			view = new ProfileListItem(mContext, mData.get(position));
		} else {
			view = (ProfileListItem) convertView;
		}

		return view;

	}

}
