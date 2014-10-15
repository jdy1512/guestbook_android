package com.allchange.guestbook.main;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.allchange.guestbook.R;
import com.allchange.guestbook.chat.push.ChatService;
import com.allchange.guestbook.chat.push.MainService;
import com.allchange.guestbook.condition.TermOfConditionFragment;
import com.allchange.guestbook.config.ConfigurationFragment;
import com.allchange.guestbook.marektmap.MarketMapFragment;
import com.allchange.guestbook.mytrip.MyTripFragment;
import com.allchange.guestbook.property.PropertyManager;
import com.gps.trace.GPSTraceService;

public class TripPalMainActivity extends FragmentActivity implements
		MainLeftMenuView.LeftMenuItemListener {
	public static final String TAG = "TripPalMainActivity";
	public static final int ID_MENU_MAIN = 0;
	public static final int ID_MENU_MYTRIPS = 1;
	public static final int ID_MENU_CONFIG = 2;
	public static final int ID_MENU_MARKET_PLACE = 3;
	public static final int ID_MENU_TERM_CONDITION = 4;

	/**
	 * DrawerLayout widget in the Android support library.
	 */
	private DrawerLayout mDrawerLayout;
	private MainLeftMenuView mDrawerView;
	private ActionBarDrawerToggle mDrawerToggle;

	private CharSequence mDrawerTitle;
	private CharSequence mTitle;

	private MainService mMainService;

	private final ServiceConnection mMainServiceConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			Log.d(TAG, "MainActivity: MainService connected");
			// LocalBinder binder = (LocalBinder) service;
			// MainService mainService = binder.getService();
			// mMainService = mainService;
			// mMainService.updateBuddies();
			// updateStatus(mMainService.getConnectionStatus(),
			// mMainService.getConnectionStatusAction());
			// mConnectionStatusTabFragment.setMainService(mainService);
		}

		public void onServiceDisconnected(ComponentName className) {
			Log.d(TAG, "mainActivity: MainService disconnected");
			mMainService = null;
			// mConnectionStatusTabFragment.unsetMainService();
		}
	};

	protected void onResume() {
		// final Intent i = XMPPManager.newSvcIntent(this,
		// MainService.ACTION_CONNECT, null, null);
		// startService(i);

		PropertyManager.getInstance().setMainServiceIsRunning(true);
		super.onResume();
	};

	@Override
	public void onStart() {
		super.onStart();

		Log.d(TAG, "MainActivity: onSart()");
		// IntentFilter intentFilter = new IntentFilter(
		// MainService.ACTION_XMPP_PRESENCE_CHANGED);
		// intentFilter.addAction(MainService.ACTION_XMPP_CONNECTION_CHANGED);
		// registerReceiver(mXmppreceiver, intentFilter);
		Intent intent = new Intent(this, MainService.class);
		bindService(intent, mMainServiceConnection, BIND_AUTO_CREATE);
		// startService(intent);

	}

	@Override
	protected void onDestroy() {
		Log.d(TAG, "onDestroy()");
		unbindService(mMainServiceConnection);
		super.onDestroy();
	}

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trippal_main);

		/**
		 * navigation drawer
		 */

		// mTitle = mDrawerTitle = getTitle();
		mTitle = mDrawerTitle = getResources().getString(R.string.menu);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerView = (MainLeftMenuView) findViewById(R.id.left_drawer);
		mDrawerView.setOnClickleftMenuItem(this);
		mDrawerView.setTripPalMainActivity(this);
		// set a custom shadow that overlays the main content when the drawer
		// opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);

		// enable ActionBar app icon to behave as action to toggle nav drawer
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
		R.string.drawer_open, /* "open drawer" description for accessibility */
		R.string.drawer_close /* "close drawer" description for accessibility */
		) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		mDrawerView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// Log.e(TAG, "onclick");
			}
		});

		if (savedInstanceState == null) {
			// selectItem(0);
		}
		// start GPSTraceService
		if (!PropertyManager.getInstance().getCheckin().equals("")) {
			startService(new Intent(this, GPSTraceService.class));
		}
		startService(new Intent(this, ChatService.class));

		selectItem(ID_MENU_MAIN, getResources().getString(R.string.main));
	}

	private long curTime = 0;
	private long lastTime = 0;
	private long exitTime = 2000;

	@Override
	public void onBackPressed() {
		curTime = System.currentTimeMillis();
		long exitChkTime = curTime - lastTime;
		if (exitChkTime <= exitTime) {
			super.onBackPressed();
			this.finish();
		}
		Toast.makeText(this, "다시 눌러 종료하십시오", Toast.LENGTH_SHORT).show();
		lastTime = System.currentTimeMillis();
	}

	@Override
	public void onClickleftMenuItem(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.leftMenu_profile:
			selectItem(ID_MENU_MAIN, getResources().getString(R.string.main));
			break;
		case R.id.leftMenu_mytrips:
			selectItem(ID_MENU_MYTRIPS,
					getResources().getString(R.string.mytrips));
			break;
		case R.id.leftMenu_configuration:
			selectItem(ID_MENU_CONFIG,
					getResources().getString(R.string.configuration));
			break;
		case R.id.leftMenu_marketPlace:
			selectItem(ID_MENU_MARKET_PLACE,
					getResources().getString(R.string.market_map));
			break;
		case R.id.leftMenu_term_and_condition:
			Log.e(TAG, "leftMenu_term_and_condition");
			selectItem(ID_MENU_TERM_CONDITION,
					getResources().getString(R.string.term_and_condition));
			break;
		case R.id.leftMenu_btn_logOut:
			// selectItem(ID_MENU_MAIN, "logout");
			break;

		default:
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		if (mDrawerLayout.isDrawerOpen(mDrawerView)) {
			return super.onCreateOptionsMenu(menu);
		}

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.

		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		return super.onOptionsItemSelected(item);
		// return false;
	}

	/* The click listner for ListView in the navigation drawer */
	// private class DrawerItemClickListener implements
	// ListView.OnItemClickListener {
	// @Override
	// public void onItemClick(AdapterView<?> parent, View view, int position,
	// long id) {
	// selectItem(position);
	// }
	// }

	private void selectItem(int position, CharSequence title) {
		// update the main content by replacing fragments
		// Fragment fragment = new PlanetFragment();
		// Bundle args = new Bundle();
		// args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
		// fragment.setArguments(args);
		//
		// android.app.FragmentManager fragmentManager = getFragmentManager();
		// fragmentManager.beginTransaction()
		// .replace(R.id.content_frame, fragment).commit();

		FragmentManager supportfm = getSupportFragmentManager();
		Fragment fragment = null;
		switch (position) {
		case ID_MENU_MAIN:
			// supportfm.popBackStack(supportfm.POP_BACK_STACK_INCLUSIVE, 0);
			fragment = new MainFragment();
			break;
		case ID_MENU_MYTRIPS:
			fragment = new MyTripFragment();
			break;
		case ID_MENU_CONFIG:
			fragment = new ConfigurationFragment();
			break;
		case ID_MENU_MARKET_PLACE:
			fragment = new MarketMapFragment();
			break;
		case ID_MENU_TERM_CONDITION:
			Log.e(TAG, "leftMenu_term_and_condition");
			fragment = new TermOfConditionFragment();
			break;
		default:
			break;
		}
		supportfm.beginTransaction().replace(R.id.main_frame, fragment)
				.commit();

		// update selected item and title, then close the drawer
		// mDrawerList.setItemChecked(position, true);
		setTitle(title);
		mDrawerLayout.closeDrawer(mDrawerView);
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		PropertyManager.getInstance().setMainServiceIsRunning(false);
		super.onStop();
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

}
