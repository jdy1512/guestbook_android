package com.allchange.guestbook.chatroom.list;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class ChatRoomListAdapter extends BaseAdapter {

	ArrayList<ChatRoomListData> mData = new ArrayList<ChatRoomListData>();
	Context mContext;
	ChatRoomListFragment mParent;

	public ChatRoomListAdapter(Context c, ChatRoomListFragment f,
			ArrayList<ChatRoomListData> data) {
		// TODO Auto-generated constructor stub
		mData = data;
		mParent = f;
		mContext = c;
	}

	public ChatRoomListData getDataWithPostion(int pos) {
		return mData.get(pos);
	}

	public void setListData(ArrayList<ChatRoomListData> data) {
		mData = data;
	}

	public void addListData(ChatRoomListData item) {
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
		ChatRoomListItem view = null;

		if (convertView == null) {
			view = new ChatRoomListItem(mContext, mData.get(position), mParent);
		} else {
			view = (ChatRoomListItem) convertView;
		}
		view.setOnClickChattingRoomListener(mParent, view);

		return view;

	}

}
