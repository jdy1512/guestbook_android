package com.allchange.guestbook.booking.search.map;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class SearchMapAdapter extends BaseAdapter {

	private final int TYPE_SEARCH_RESULT = 0;
	private final int TYPE_POWERED_GOOGLE = 1;
	ArrayList<String> mData = new ArrayList<String>();
	Context mContext;

	public SearchMapAdapter(Context c, ArrayList<String> data) {
		// TODO Auto-generated constructor stub
		mData = data;
		mContext = c;
	}

	public void addListData(String item) {
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
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 2;
	}

	@Override
	public int getItemViewType(int position) {

		if ((mData.size() - 1) == position) {
			return TYPE_POWERED_GOOGLE;
		}
		return TYPE_SEARCH_RESULT;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		switch (getItemViewType(position)) {
		case TYPE_SEARCH_RESULT:
			SearchItem view = null;

			if (convertView == null) {
				view = new SearchItem(mContext, mData.get(position));
			} else {
				view = (SearchItem) convertView;
			}
			return view;
		case TYPE_POWERED_GOOGLE:
			SearchGoogleItem viewG = null;

			if (convertView == null) {
				viewG = new SearchGoogleItem(mContext);
			} else {
				viewG = (SearchGoogleItem) convertView;
			}
			return viewG;
		default:
			break;
		}
		return null;

	}
}
