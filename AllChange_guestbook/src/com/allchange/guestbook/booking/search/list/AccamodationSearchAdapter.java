package com.allchange.guestbook.booking.search.list;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;

import com.allchange.guestbook.booking.search.map.ParsingData;

public class AccamodationSearchAdapter extends BaseAdapter {

	ArrayList<ParsingData> mItems;
	Context mContext;

	public AccamodationSearchAdapter(Context context,
			ArrayList<ParsingData> items) {
		mContext = context;
		mItems = items;
	}

	// public void add(MyData item) {
	// mItems.add(item);
	// notifyDataSetChanged();
	// }

	public void insertListData(ParsingData item) {
		mItems.add(0, item);
	}

	public ArrayList<ParsingData> getListData() {
		return mItems;
	}

	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public ParsingData getItem(int position) {
		return mItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		AccamodationSearchItemView view = null;

		if (convertView == null) {
			view = new AccamodationSearchItemView(mContext);
		} else {
			view = (AccamodationSearchItemView) convertView;
		}

		view.setMyData(mItems.get(position));

		AbsListView.LayoutParams params = new AbsListView.LayoutParams(
				AbsListView.LayoutParams.MATCH_PARENT,
				AbsListView.LayoutParams.MATCH_PARENT);
		view.setLayoutParams(params);

		// view.setMyData(mItems.get(position));

		return view;
	}

}
