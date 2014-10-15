package com.allchange.guestbook.login;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.allchange.guestbook.google.auth.AbstractGetNameTask;
import com.allchange.guestbook.google.auth.GetNameInForeground;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class GoogleLogin {
	public static final String TAG = "GoogleLogin";
	public static final String SCOPE = "oauth2:https://www.googleapis.com/auth/userinfo.profile";
	public static final String EXTRA_ACCOUNTNAME = "extra_accountname";

	private static GoogleLogin instance = null;

	// public TextView mOut;

	static final int REQUEST_CODE_PICK_ACCOUNT = 1000;
	static final int REQUEST_CODE_RECOVER_FROM_AUTH_ERROR = 1001;
	static final int REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR = 1002;

	public String mEmail;

	public MainLoginActivity mActivity;

	public static String TYPE_KEY = "type_key";

	public static enum Type {
		FOREGROUND, BACKGROUND, BACKGROUND_WITH_SYNC
	}

	public static GoogleLogin getInstance(MainLoginActivity activity) {
		if (instance == null) {
			instance = new GoogleLogin(activity);
		}
		return instance;
	}

	public GoogleLogin(MainLoginActivity activity) {
		// TODO Auto-generated constructor stub
		mActivity = activity;
	}

	public void handleAuthorizeResult(int resultCode, Intent data) {
		if (data == null) {
			show("Unknown error, click the button again");
			return;
		}
		if (resultCode == mActivity.RESULT_OK) {
			Log.i(TAG, "Retrying");
			getTask(mActivity, mEmail, SCOPE).execute();
			return;
		}
		if (resultCode == mActivity.RESULT_CANCELED) {
			show("User rejected authorization.");
			return;
		}
		show("Unknown error, click the button again");
	}

	/** Called by button in the layout */
	public void greetTheUser(View view) {
		getUsername();
	}

	/**
	 * Attempt to get the user name. If the email address isn't known yet, then
	 * call pickUserAccount() method so the user can pick an account.
	 */
	public void getUsername() {
		if (mEmail == null) {
			pickUserAccount();
		} else {
			if (isDeviceOnline()) {
				getTask(mActivity, mEmail, SCOPE).execute();
			} else {
				Toast.makeText(mActivity, "No network connection available",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	/**
	 * Starts an activity in Google Play Services so the user can pick an
	 * account
	 */
	public void pickUserAccount() {
		String[] accountTypes = new String[] { "com.google" };
		Intent intent = AccountPicker.newChooseAccountIntent(null, null,
				accountTypes, false, null, null, null, null);
		mActivity.startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT);
	}

	/** Checks whether the device currently has a network connection */
	public boolean isDeviceOnline() {
		ConnectivityManager connMgr = (ConnectivityManager) mActivity
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			return true;
		}
		return false;
	}

	/**
	 * This method is a hook for background threads and async tasks that need to
	 * update the UI. It does this by launching a runnable under the UI thread.
	 */
	public void show(final String message) {
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Log.e(TAG, "message : " + message);
				// mOut.setText(message);
			}
		});
	}

	/**
	 * This method is a hook for background threads and async tasks that need to
	 * provide the user a response UI when an exception occurs.
	 */
	public void handleException(final Exception e) {
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (e instanceof GooglePlayServicesAvailabilityException) {
					// The Google Play services APK is old, disabled, or not
					// present.
					// Show a dialog created by Google Play services that allows
					// the user to update the APK
					int statusCode = ((GooglePlayServicesAvailabilityException) e)
							.getConnectionStatusCode();
					Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
							statusCode, mActivity,
							REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
					dialog.show();
				} else if (e instanceof UserRecoverableAuthException) {
					// Unable to authenticate, such as when the user has not yet
					// granted
					// the app access to the account, but the user can fix this.
					// Forward the user to an activity in Google Play services.
					Intent intent = ((UserRecoverableAuthException) e)
							.getIntent();
					mActivity.startActivityForResult(intent,
							REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
				}
			}
		});
	}

	/**
	 * Note: This approach is for demo purposes only. Clients would normally not
	 * get tokens in the background from a Foreground activity.
	 */
	public AbstractGetNameTask getTask(MainLoginActivity activity,
			String email, String scope) {
		// switch (requestType) {
		// case FOREGROUND:
		// return new GetNameInForeground(activity, email, scope);
		// default:
		return new GetNameInForeground(activity, email, scope);
		// }
	}
}
