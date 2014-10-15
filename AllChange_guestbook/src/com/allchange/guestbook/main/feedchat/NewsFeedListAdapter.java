package com.allchange.guestbook.main.feedchat;

import java.util.ArrayList;

import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class NewsFeedListAdapter extends BaseAdapter {

	private static final String TAG = "NewsFeedListAdapter";
	ArrayList<MessageData> mData = new ArrayList<MessageData>();
	FragmentActivity mContext;
	FeedFragment feedFragment;

	public NewsFeedListAdapter(FragmentActivity c, ArrayList<MessageData> data,
			FeedFragment ff) {
		// 실시간 업데이트중 추가
		data.add(0, null);
		mData = data;
		mContext = c;
		feedFragment = ff;
	}

	public ArrayList<MessageData> getListData() {
		return mData;
	}

	public void setListData(ArrayList<MessageData> data) {
		mData = data;
	}

	public void clearListData() {
		ArrayList<MessageData> data = new ArrayList<MessageData>();
		data.add(0, null);
		mData = data;
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
	public int getItemViewType(int position) {
		if (position == 0) {
			return VIEW_TYPE_UPDATE;
		} else if (mData.get(position).email_id.equals(FeedFragment.LOADINGBAR)) {
			return VIEW_TYPE_LOADING;
		}

		return VIEW_TYPE_FEED;
	}

	public static final int VIEW_TYPE_UPDATE = 0;
	public static final int VIEW_TYPE_FEED = 1;
	public static final int VIEW_TYPE_LOADING = 2;

	@Override
	public int getViewTypeCount() {
		return 3;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (getItemViewType(position) == VIEW_TYPE_UPDATE) {
			NewsFeedListItemUpdate v = null;
			if (convertView == null) {
				v = new NewsFeedListItemUpdate(mContext);

			} else {
				v = (NewsFeedListItemUpdate) convertView;
			}
			v.setOnRefreshFeedListener(feedFragment);
			if (position == mData.size() - 1) {
				v.setVisibleLine(false);
			} else {
				v.setVisibleLine(true);
			}
			return v;
		} else if (getItemViewType(position) == VIEW_TYPE_LOADING) {
			NewsFeedListItemLoading v = null;
			if (convertView == null) {
				v = new NewsFeedListItemLoading(mContext);

			} else {
				v = (NewsFeedListItemLoading) convertView;
			}
			return v;
		} else {
			NewsFeedListItem view = null;

			if (convertView == null) {
				view = new NewsFeedListItem(mContext, mData.get(position));
			} else {
				view = (NewsFeedListItem) convertView;
			}
			if (position == mData.size() - 1) {
				view.setVisibleLine(false);
			} else {
				view.setVisibleLine(true);
			}
			view.setMessageData(mData.get(position));
			return view;
		}

	}
}
