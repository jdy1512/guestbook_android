package com.allchange.guestbook.login;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import android.util.Log;

import com.allchange.guestbook.R;
import com.allchange.guestbook.aquery.FBGoogleCallbackListener;
import com.allchange.guestbook.property.MyApplication;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphObject;

public class FacebookLogin {
	private static String TAG = "FacebookLogin";
	private static final List<String> EMAIL_PERMISSIONS = Arrays
			.asList("email");
	private FBGoogleCallbackListener mListener;
	// private UiLifecycleHelper uiHelper;

	private static FacebookLogin instance = null;

	public MainLoginActivity mActivity;

	public static FacebookLogin getInstance(MainLoginActivity activity) {
		if (instance == null) {
			instance = new FacebookLogin(activity);
		}
		return instance;
	}

	public FacebookLogin(MainLoginActivity activity) {
		mActivity = activity;
		mListener = activity;
	}

	public boolean isSubsetOf(Collection<String> subset,
			Collection<String> superset) {
		for (String string : subset) {
			if (!superset.contains(string)) {
				return false;
			}
		}
		return true;
	}

	public Session.StatusCallback statusCallback = new Session.StatusCallback() {
		@Override
		public void call(final Session session, SessionState state,
				Exception exception) {
			// Respond to session state changes, ex: updating the view
			// System.out.println();
			Log.e(TAG, "session : " + state.toString());

			if (session.isOpened()) {

				List<String> permissions = session.getPermissions();
				if (!isSubsetOf(EMAIL_PERMISSIONS, permissions)) {
					System.out.println("isSubsetOf");
					Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(
							mActivity, EMAIL_PERMISSIONS);
					session.requestNewReadPermissions(newPermissionsRequest);

					return;
				}
				new Request(session, "/me", null, HttpMethod.GET,
						new Request.Callback() {
							public void onCompleted(Response response) {
								GraphObject object = response.getGraphObject();
								Map<String, Object> map = object.asMap();
								if (mListener != null) {
									// try {

									if (mListener != null) {
										Log.e(TAG, "gogo token listener");
										mListener
												.FBGoogleCallback(
														MyApplication
																.getContext()
																.getResources()
																.getString(
																		R.string.facebook),
														session.getAccessToken());
									} else {
										Log.e(TAG, "listener null");
									}
								}

							}
						}).executeAsync();

			}
		}
	};

	public void onClickContectFacebook() {
		Session session = Session.getActiveSession();
		if (session != null && !session.isOpened() && !session.isClosed()) {
			session.openForRead(new Session.OpenRequest(mActivity)
					.setPermissions(Arrays.asList("email")).setCallback(
							statusCallback));
		} else {
			Session.openActiveSession(mActivity, true, statusCallback);
		}
	}

	public void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		Log.e(TAG, "onSessionStateChange");
		if (state.isOpened()) {
			Log.i(TAG, "Logged in...");
		} else if (state.isClosed()) {
			Log.i(TAG, "Logged out...");
		} else {
			Log.i(TAG, "state : " + state.toString());
		}
	}

	public Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

}
