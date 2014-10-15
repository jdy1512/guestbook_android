package com.allchange.guestbook.splash;

/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.allchange.guestbook.property.PropertyManager;
import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;

/**
 * {@link IntentService} responsible for handling GCM messages.
 */
public class GCMIntentService extends GCMBaseIntentService {

	private static final String TAG = "GCMIntentService";

	public GCMIntentService() {
		super(CommonUtilities.SENDER_ID);
	}

	@Override
	protected void onRegistered(Context context, String registrationId) {
		// Log.e(TAG, "Device registered: regId = " + registrationId);
		PropertyManager.getInstance().setRegistId(registrationId);
		// ServerUtilities.register(context, registrationId);
	}

	@Override
	protected void onUnregistered(Context context, String registrationId) {
		Log.e(TAG, "Device unregistered");
		if (GCMRegistrar.isRegisteredOnServer(context)) {
			// ServerUtilities.unregister(context, registrationId);
		} else {
			// This callback results from the call to unregister made on
			// ServerUtilities when the registration to the server failed.
			Log.e(TAG, "Ignoring unregister callback");
		}
	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		Log.e(TAG, "Received message");
		Bundle bundle = intent.getExtras();
		Set<String> setKey = bundle.keySet();
		Iterator<String> iterKey = setKey.iterator();
		HashMap<String, String> mapMessage = new HashMap<String, String>();

		// title == type ,message ==values
		while (iterKey.hasNext()) {
			String key = iterKey.next();
			String value = bundle.getString(key);
			mapMessage.put(key, value);
			Log.e(TAG, "key = " + key + ", value = " + value);
		}
		// notifies user
		if (PropertyManager.getInstance().getGCMSetting()) {
			if (mapMessage.get("title").equals("cococo")) {

			} else if (mapMessage.get("title").equals("notice")) {
				String[] arrStr = mapMessage.get("message").split("/&&/");
				String year = arrStr[5].substring(0, 4);
				String month = arrStr[5].substring(4, 6);
				String day = arrStr[5].substring(6, 8);
				String hour = arrStr[5].substring(8, 10);
				String min = arrStr[5].substring(10, 12);
				generateNotification(context, month + "월 " + day + "일 " + hour
						+ "시 " + min + "분에 " + arrStr[4] + "에서 " + arrStr[2]
						+ arrStr[3] + "가 있습니다.", mapMessage.get("message"));
			} else if (mapMessage.get("title").equals("체크인 알림")) {
				generateNotification(context, mapMessage.get("message"));
			} else {
				generateNotification(context, mapMessage.get("message"));
			}
		}
	}

	@Override
	protected void onDeletedMessages(Context context, int total) {
		Log.e(TAG, "Received deleted messages notification");
		// notifies user
		generateNotification(context, "deleted");
	}

	@Override
	public void onError(Context context, String errorId) {
		Log.e(TAG, "Received error: " + errorId);
	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		// log message
		Log.e(TAG, "Received recoverable error: " + errorId);
		return super.onRecoverableError(context, errorId);
	}

	private void generateNotification(Context context, String message,
			String msg) {
		Log.e(TAG, "generateNotification : " + message);
		// int icon = R.drawable.ic_lancher_v1;
		// long when = System.currentTimeMillis();
		// NotificationManager notificationManager = (NotificationManager)
		// context
		// .getSystemService(Context.NOTIFICATION_SERVICE);
		// Notification notification = new Notification(icon, message, when);
		// String title = context.getString(R.string.app_name);
		// Intent notificationIntent = new Intent(context,
		// StartMenuActivity.class);
		// notificationIntent.putExtra(SplashActivity.NOTI_MESSAGE, msg);
		// // set intent so it does not start a new activity
		// notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
		// | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		// PendingIntent intent = PendingIntent.getActivity(context, 0,
		// notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		// notification.setLatestEventInfo(context, title, message, intent);
		// notification.flags |= Notification.FLAG_AUTO_CANCEL;
		// notificationManager.notify(0, notification);
	}

	/**
	 * Issues a notification to inform the user that server has sent a message.
	 */
	private static void generateNotification(Context context, String message) {
		Log.e(TAG, "generateNotification : " + message);
		// int icon = R.drawable.ic_lancher_v1;
		// long when = System.currentTimeMillis();
		// NotificationManager notificationManager = (NotificationManager)
		// context
		// .getSystemService(Context.NOTIFICATION_SERVICE);
		// Notification notification = new Notification(icon, message, when);
		// String title = context.getString(R.string.app_name);
		// Intent notificationIntent = new Intent(context,
		// StartMenuActivity.class);
		// notificationIntent.putExtra(SplashActivity.NOTI_MESSAGE, message);
		// // set intent so it does not start a new activity
		// notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
		// | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		// PendingIntent intent = PendingIntent.getActivity(context, 0,
		// notificationIntent, 0);
		// notification.setLatestEventInfo(context, title, message, intent);
		// notification.flags |= Notification.FLAG_AUTO_CANCEL;
		// notificationManager.notify(0, notification);
	}

}
