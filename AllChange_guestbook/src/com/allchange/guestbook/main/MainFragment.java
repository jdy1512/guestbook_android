package com.allchange.guestbook.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;

import com.allchange.guestbook.R;
import com.allchange.guestbook.booking.search.map.CustomViewPager;
import com.allchange.guestbook.chatroom.list.ChatRoomListFragment;
import com.allchange.guestbook.main.feedandsearch.FragmentFeedAndSearch;
import com.allchange.guestbook.property.MyApplication;
import com.allchange.guestbook.property.PropertyManager;

public class MainFragment extends Fragment {
	private static final String TAG = "MainFragment";
	TabHost tabHost;
	TabsAdapter mAdapter;
	CustomViewPager pager;
	public static final String KEY_CURRENT_TAB = "currentTab";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.fragment_main, container, false);
		setHasOptionsMenu(true);
		// Toast.makeText(getActivity(), "new", 0).show();
		tabHost = (TabHost) v.findViewById(R.id.tabhost);
		tabHost.setup();
		pager = (CustomViewPager) v.findViewById(R.id.pager);
		mAdapter = new TabsAdapter(getActivity(), getChildFragmentManager(),
				tabHost, pager);
		// mAdapter.addTab(
		// tabHost.newTabSpec("MapFragment").setIndicator("",
		// getResources().getDrawable(R.drawable.map_ic)),
		// TrippalMapFragment.class, null);

		// 어차피 안보여지는탭이라서 이미지 삽입x
		mAdapter.addTab(tabHost.newTabSpec("one").setIndicator("", null),
				FragmentFeedAndSearch.class, null);

		mAdapter.addTab(tabHost.newTabSpec("two").setIndicator("", null),
				ChatRoomListFragment.class, null);

		Log.e(TAG, "tab count : " + mAdapter.getCount());

		mAdapter.setOnTabChangedListener(new OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {
				if (tabId.equals("MapFragment")) {
					if (mMapListener != null) {
						mMapListener.onChangedTripPalTap(0);
					}
				} else {
					if (mChatRoomListener != null) {
						mChatRoomListener.onChangedTripPalTap(1);
					}
				}
			}
		});

		if (savedInstanceState != null) {
			mAdapter.onRestoreInstanceState(savedInstanceState);
			tabHost.setCurrentTabByTag(savedInstanceState
					.getString(KEY_CURRENT_TAB));
		}
		return v;
	}

	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(KEY_CURRENT_TAB, tabHost.getCurrentTabTag());
		mAdapter.onSaveInstanceState(outState);
	};

	// @Override
	// protected void onSaveInstanceState(Bundle outState) {
	// super.onSaveInstanceState(outState);
	// outState.putString(KEY_CURRENT_TAB, tabHost.getCurrentTabTag());
	// mAdapter.onSaveInstanceState(outState);
	// }

	MenuItem item_search, item_chat;

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// 액션바 item 색깔 변하게 하기위해서
		if (tabHost == null || tabHost.getCurrentTab() == 0) {
			inflater.inflate(R.menu.main1, menu);
		} else {
			inflater.inflate(R.menu.main2, menu);
		}
		item_search = menu.findItem(R.id.action_tipper);
		item_chat = menu.findItem(R.id.action_chat);

		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_tipper:
			getActivity().invalidateOptionsMenu();
			tabHost.setCurrentTab(0);
			return true;
		case R.id.action_chat:
			getActivity().invalidateOptionsMenu();
			tabHost.setCurrentTab(1);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public interface OnChangedToListListener {
		public void onChangedTripPalTap(int viewPosition);
	}

	OnChangedToListListener mListListener;

	public void setChangedToListListener(OnChangedToListListener listener) {
		mListListener = listener;
	}

	public interface OnChangedTripPalTapListener {
		public void onChangedTripPalTap(int viewPosition);
	}

	OnChangedTripPalTapListener mMapListener, mChatRoomListener;

	public void setChangedTripPalTapListener(
			OnChangedTripPalTapListener listener) {
		mMapListener = listener;
	}

	public void setonChangeTabListener(OnChangedTripPalTapListener listener) {
		mChatRoomListener = listener;
	}

	private boolean isSetMyHouse() {
		if (PropertyManager.getInstance().getCheckin().equals("")
				|| PropertyManager.getInstance().getCheckin().length() < 5) {
			return false;
		}
		return true;
	}

}
