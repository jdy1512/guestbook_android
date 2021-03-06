package com.allchange.guestbook.chat;

import java.util.ArrayList;

import com.allchange.guestbook.property.PropertyManager;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout.LayoutParams;

public class MyAdapter extends BaseAdapter implements
		ItemView.OnImageClickListener {

	ArrayList<MyData> mItems;
	Context mContext;
	public static final int VIEW_TYPE_COUNT = 2;
	public static final int VIEW_TYPE_LEFT = 0;
	public static final int VIEW_TYPE_RIGHT = 1;

	public interface OnMyAdapterListener {
		public void onItemImageClick(MyAdapter adapter, View v, MyData d);
	}

	OnMyAdapterListener mListener;

	public void setOnMyAdapterListener(OnMyAdapterListener listener) {
		mListener = listener;
	}

	public MyAdapter(Context context, ArrayList<MyData> items) {
		mContext = context;
		mItems = items;
	}

	public void add(MyData item) {
		mItems.add(item);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public MyData getItem(int position) {
		return mItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getViewTypeCount() {
		return VIEW_TYPE_COUNT;
	}

	@Override
	public int getItemViewType(int position) {
		if (mItems.get(position).email.equals(PropertyManager.getInstance()
				.getId())) {
			return VIEW_TYPE_RIGHT;
		}
		return VIEW_TYPE_LEFT;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		switch (getItemViewType(position)) {
		case VIEW_TYPE_LEFT:
			ItemView view = null;

			if (convertView == null) {
				view = new ItemView(mContext);
				view.setOnImageClickListener(this);
			} else {
				view = (ItemView) convertView;
			}

			AbsListView.LayoutParams params = new AbsListView.LayoutParams(
					AbsListView.LayoutParams.MATCH_PARENT,
					AbsListView.LayoutParams.MATCH_PARENT);
			view.setLayoutParams(params);

			view.setMyData(mItems.get(position));

			return view;
		case VIEW_TYPE_RIGHT:
			ItemRightView viewr = null;

			if (convertView == null) {
				viewr = new ItemRightView(mContext);
			} else {
				viewr = (ItemRightView) convertView;
			}
			viewr.setMyData(mItems.get(position));

			AbsListView.LayoutParams params1 = new AbsListView.LayoutParams(
					AbsListView.LayoutParams.MATCH_PARENT,
					AbsListView.LayoutParams.MATCH_PARENT);
			viewr.setLayoutParams(params1);

			viewr.setMyData(mItems.get(position));

			return viewr;
		}
		return null;
	}

	@Override
	public void onImageClick(View v, MyData d) {
		if (mListener != null) {
			mListener.onItemImageClick(this, v, d);
		}
	}

}
