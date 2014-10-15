package com.allchange.guestbook.splash;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.allchange.guestbook.R;
import com.allchange.guestbook.aquery.AndroidQuery;
import com.allchange.guestbook.login.MainLoginActivity;
import com.allchange.guestbook.main.TripPalMainActivity;
import com.allchange.guestbook.property.PropertyManager;

public class SplashActivity extends Activity {

	private static String TAG = "SplashActivity";
	public static String NOTI_MESSAGE = "noti_message";
	private String userID;
	private String userPW;
	// how long until we go to the next activity
	protected int _splashTime = 500;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// status bar 숨기기
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);

		// new Thread(new Runnable() {
		//
		// @Override
		// public void run() {
		// while (true) {
		// try {
		// Thread.sleep(200);
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// Log.e("TAG",
		// "==============================================");
		// android.util.Log
		// .e("TAG",
		// "TOTAL MEMORY : "
		// + (Runtime.getRuntime()
		// .totalMemory() / (1024 * 1024))
		// + "MB");
		// android.util.Log
		// .e("TAG",
		// "MAX MEMORY : "
		// + (Runtime.getRuntime().maxMemory() / (1024 * 1024))
		// + "MB");
		// android.util.Log
		// .e("TAG",
		// "FREE MEMORY : "
		// + (Runtime.getRuntime()
		// .freeMemory() / (1024 * 1024))
		// + "MB");
		// android.util.Log
		// .e("TAG",
		// "ALLOCATION MEMORY : "
		// + ((Runtime.getRuntime()
		// .totalMemory() - Runtime
		// .getRuntime().freeMemory()) / (1024 * 1024))
		// + "MB");
		// Log.e(TAG, "Thread : " + Thread.activeCount());
		//
		// Log.e("TAG",
		// "==============================================");
		// }
		// }
		// }).start();

		AndroidQuery.setInstance(this);
		// // checkNotNull(CommonUtilities.SERVER_URL, "SERVER_URL");
		// checkNotNull(CommonUtilities.SENDER_ID, "SENDER_ID");
		// // Make sure the device has the proper dependencies.
		// GCMRegistrar.checkDevice(this);
		// // Make sure the manifest was properly set - comment out this line
		// // while developing the app, then uncomment it when it's ready.
		// GCMRegistrar.checkManifest(this);
		//
		// setContentView(R.layout.splash);
		//
		// registerReceiver(mHandleMessageReceiver, new IntentFilter(
		// CommonUtilities.DISPLAY_MESSAGE_ACTION));
		//
		// final String regId = GCMRegistrar.getRegistrationId(this);
		// if (regId.equals("")) {
		// // Automatically registers application on startup.
		// Log.e(TAG, "regId is empty");
		// GCMRegistrar.register(this, CommonUtilities.SENDER_ID);
		// } else {
		// // Device is already registered on GCM, needs to check if it is
		// // registered on our server as well.
		//
		// if (GCMRegistrar.isRegisteredOnServer(this)) {
		// // Skips registration.
		// Log.e(TAG, "already_registered");
		//
		// // GCMRegistrar.unregister(this);
		// // GCMRegistrar.register(this, CommonUtilities.SENDER_ID);
		// } else {
		// Log.e(TAG, "registe id is not received");
		// PropertyManager.getInstance().setRegistId(regId);
		// // GCMRegistrar.unregister(this);
		// // GCMRegistrar.register(this, CommonUtilities.SENDER_ID);
		// // Try to register again, but not in the UI thread.
		// // It's also necessary to cancel the thread onDestroy(),
		// // hence the use of AsyncTask instead of a raw thread.
		// }
		// }

		goNextToLogin();

	}

	@Override
	protected void onStop() {
		AndroidQuery.getInstance().clearAQuery();
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		// unregisterReceiver(mHandleMessageReceiver);
		// GCMRegistrar.onDestroy(this);
		super.onDestroy();
	}

	public boolean getMyID() {
		userID = PropertyManager.getInstance().getId();
		userPW = PropertyManager.getInstance().getPassword();
		boolean isAccessble = false;
		if (!userID.equals("")) {
			isAccessble = true;
		}
		return isAccessble;
	}

	private void checkNotNull(Object reference, String name) {
		if (reference == null) {
			throw new NullPointerException(getString(R.string.error_config,
					name));
		}
	}

	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String newMessage = intent.getExtras().getString(
					CommonUtilities.EXTRA_MESSAGE);
			Log.i(TAG, newMessage);
			// mDisplay.append(newMessage + "\n");
		}
	};

	private void goNextToLogin() {
		Intent i = new Intent();
		i.setClass(SplashActivity.this, MainLoginActivity.class);
		startActivity(i);
		finish();
	}

	private void goNextToMain() {
		Intent intent = new Intent(this, TripPalMainActivity.class);
		startActivity(intent);
		finish();
	}

}
