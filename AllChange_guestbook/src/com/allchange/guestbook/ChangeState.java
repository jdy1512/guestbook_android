package com.allchange.guestbook;

import android.content.Context;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.BackStackEntry;
import android.util.Log;

import com.allchange.guestbook.login.MainLoginActivity;

public class ChangeState {
	private static ChangeState state;
	private static MainLoginActivity mContext;
	private BackStackEntry entry;
	private FragmentManager manager;
	// private NotificationFragment notificationFragment;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			// TODO Auto-generated method stub
			String state = msg.getData().getString("state");
			// // server req 전송
			// if (state.equals("calendar")) {
			// mContext.sendRequestCalendar();
			// } else if (state.equals("home")) {
			// ChangeState.instance().changeHomeFragment();

			if (state.equals(MainLoginActivity.TAG)) {
				// ChangeState.getInstance().
			} else {
				Log.e("debug", "---------------------");
				Log.e("debug", "state mismatch");
				Log.e("debug", "state : " + state);
				Log.e("debug", "---------------------");
			}
		};
	};

	protected static ChangeState setInstance(FragmentManager fManager,
			Context context) {
		if (state == null) {
			state = new ChangeState(fManager);
			mContext = (MainLoginActivity) context;
		}
		return state;
	}

	public static ChangeState getInstance() {
		return state;
	}

	public FragmentManager getFragmentManager() {
		return manager;
	}

	public Handler getHandler() {
		return handler;
	}

	public ChangeState(FragmentManager fManager) {
		manager = fManager;
	}

	public void setBackStackEntry(int index) {
		entry = manager.getBackStackEntryAt(index);
	}

	public void changeIndexFragment(int index) {
		setBackStackEntry(index);
		manager.popBackStackImmediate(entry.getId(), 0);
	}

	public void changeIndexFragmentInclusive(int index) {
		setBackStackEntry(index);
		manager.popBackStackImmediate(entry.getId(),
				manager.POP_BACK_STACK_INCLUSIVE);
	}

	public void changeFragment(int containerViewId, Fragment fragment,
			String tag) {
		manager.beginTransaction().replace(containerViewId, fragment, tag)
				.addToBackStack(tag).commit();
	}

	public void refreshFragment(int containerViewId, Fragment fragment,
			String tag) {
		manager.beginTransaction().replace(containerViewId, fragment, tag)
				.commit();
	}
}
