package com.gps.trace;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class RestartReceiver extends BroadcastReceiver {
	public static final String TAG = "RestartReceiver";
	public static final String ACTION_RESTART_GPSTrace = "ACTION.Restart";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "onReceive");
		if (intent.getAction().equals(ACTION_RESTART_GPSTrace)) {
			Intent i = new Intent(context, GPSTraceService.class);
			context.startService(i);
		}
	}
}