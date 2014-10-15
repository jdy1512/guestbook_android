package com.allchange.guestbook.bookingsitelist;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class BookingSiteListAdapter extends BaseAdapter {

	ArrayList<BookingSiteListData> mData = new ArrayList<BookingSiteListData>();
	Context mContext;

	public BookingSiteListAdapter(Context c, ArrayList<BookingSiteListData> data) {
		// TODO Auto-generated constructor stub
		mData = data;
		mContext = c;
	}

	public BookingSiteListData getDataWithPostion(int pos) {
		return mData.get(pos);
	}

	public void setListData(ArrayList<BookingSiteListData> data) {
		mData = data;
	}

	public void addListData(BookingSiteListData item) {
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
		BookingSiteListItem view = null;

		if (convertView == null) {
			view = new BookingSiteListItem(mContext, mData.get(position));
		} else {
			view = (BookingSiteListItem) convertView;
		}

		return view;

	}

}
