package com.allchange.guestbook.chatroom.list;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.allchange.guestbook.R;
import com.allchange.guestbook.chat.ChatCacheStore;
import com.allchange.guestbook.chat.DBChatListData;
import com.allchange.guestbook.chat.DBModel;
import com.allchange.guestbook.chat.MessageActivity;
import com.allchange.guestbook.chat.XMPPManager;
import com.allchange.guestbook.chat.DBModel.OnCreateNewChatListener;
import com.allchange.guestbook.chat.push.ChatService.OnGetChatListener;
import com.allchange.guestbook.chatroom.list.ChatRoomListItem.OnClickChattingRoomListener;
import com.allchange.guestbook.main.MainFragment.OnChangedTripPalTapListener;
import com.allchange.guestbook.main.profiles.ChattingRoomPeopleData;
import com.allchange.guestbook.property.MyApplication;

public class ChatRoomListFragment extends Fragment implements
		OnChangedTripPalTapListener, OnClickChattingRoomListener {
	private static final String TAG = "ChatRoomListFragment";
	private static final int REQUEST_MAKE_ROOM = 100;
	ListView listChatRoom;
	ChatRoomListAdapter mAdapter;
	FrameLayout fl_edit;
	TextView tv_editchatroom;
	boolean b_onScreen = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		setHasOptionsMenu(true);
		getActivity().getActionBar().setTitle(
				getResources().getString(R.string.actionbar_title_chat));
		View v = inflater.inflate(R.layout.activity_chatting_room_list,
				container, false);
		tv_editchatroom = (TextView) v
				.findViewById(R.id.chattingRoomList_editchatroom_tv);
		listChatRoom = (ListView) v
				.findViewById(R.id.chattingRoomList_listView);
		fl_edit = (FrameLayout) v
				.findViewById(R.id.chattingRoomList_editchatroom);
		fl_edit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (tv_editchatroom.getText().equals(
						getResources().getString(R.string.edit_chatroom_done))) {
					tv_editchatroom.setText(getResources().getString(
							R.string.edit_chatroom));
					updateChatViews(false);
				} else {
					tv_editchatroom.setText(getResources().getString(
							R.string.edit_chatroom_done));
					updateChatViews(true);
				}
			}

		});

		// listChatRoom.setOnItemClickListener(new OnItemClickListener() {
		//
		// @Override
		// public void onItemClick(AdapterView<?> parent, View view,
		// int position, long id) {
		// Toast.makeText(getActivity(), "ddd", 0).show();
		// startMessageActivity(mAdapter.getDataWithPostion(position).tableName);
		// }
		// });

		// TripPalMainActivity tpa = (TripPalMainActivity) getActivity();
		// tpa.setonChangeTabListener(this);

		updateChatViews(false);

		init();

		DBModel.getInstance().setOnCreateNewChatListener(
				new OnCreateNewChatListener() {

					@Override
					public void onCreateNewChat() {
						Log.e(TAG, "onCreateNewChat");
					}
				});

		return v;
	}

	final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
		}
	};

	private void init() {
		b_onScreen = true;
		// 메시지가 날라왔을때 채팅 방item에 최신 글 표시해주기

		XMPPManager.getInstance().getChatService()
				.setOnGetChatListener(new OnGetChatListener() {
					@Override
					public void onGetChat(String tableName, String message) {
						try {
							// if (b_onScreen) {
							// Log.e(TAG, "onGetChat");
							// updateChatViews(false);
							// }

						} catch (Exception e) {
						}

					}
				});

	}

	@Override
	public void onResume() {
		updateChatViews(false);
		b_onScreen = true;
		super.onResume();
	}

	@Override
	public void onStart() {
		b_onScreen = true;
		super.onStart();
	}

	@Override
	public void onPause() {
		b_onScreen = false;
		XMPPManager.getInstance().getChatService().setOnGetChatListener(null);
		super.onPause();
	}

	@Override
	public void onStop() {
		b_onScreen = false;
		XMPPManager.getInstance().getChatService().setOnGetChatListener(null);
		super.onStop();
	}

	private void updateChatViews(boolean edit) {
		ArrayList<DBChatListData> arrChatList = new ArrayList<DBChatListData>();
		arrChatList = DBModel.getInstance().queryChatList();
		ArrayList<ChatRoomListData> dataArray = new ArrayList<ChatRoomListData>();
		for (int i = 0; i < arrChatList.size(); i++) {

			ChatRoomListData data = new ChatRoomListData();
			data.tableName = arrChatList.get(i).tableName;
			data.status_edit = edit;
			data.people.add(getPeoPleInfo(arrChatList.get(i).target));
			dataArray.add(data);
		}
		mAdapter = new ChatRoomListAdapter(getActivity(), this, dataArray);
		listChatRoom.setAdapter(mAdapter);

	}

	private ChattingRoomPeopleData getPeoPleInfo(String email) {
		ChattingRoomPeopleData pData = new ChattingRoomPeopleData();
		pData.email_id = email;
		pData.firstname = ChatCacheStore.getInstance().getFirstNameResovler()
				.get(email);
		pData.lastname = ChatCacheStore.getInstance().getLastNameResovler()
				.get(email);
		pData.profile_img_path = ChatCacheStore.getInstance()
				.getImageResovler().get(email);

		return pData;
	}

	private void startMessageActivity(String tableName) {

		Intent i = new Intent(getActivity(), MessageActivity.class);
		i.putExtra("data", tableName);
		// Bundle bundle = new Bundle();
		// bundle.putParcelable("data", (Parcelable) member.get(0));
		// i.putExtras(bundle);
		startActivity(i);
	}

	@Override
	public void onChangedTripPalTap(int viewPosition) {
		if (viewPosition == 1) {
			getActivity().getActionBar().setTitle(
					getResources().getString(R.string.actionbar_title_chat));
			// updateChatViews(false);
		}
	}

	@Override
	public void onClickChattingRoom(String tableName) {
		startMessageActivity(tableName);
	}

	@Override
	public void onDestoryChattingRoom(ChatRoomListItem mView) {
		updateChatViews(false);
	}

}
