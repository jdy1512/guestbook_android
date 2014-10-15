package com.allchange.guestbook.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.allchange.guestbook.chat.push.MainService;

public class PowerReceiver extends BroadcastReceiver {
	private static String TAG = "PowerReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.e(TAG, "PowerReceiver");
		// Handle the preference for "start when charging" and
		// "stop when not charging"
		boolean connected = intent.getAction().equals(
				"android.intent.action.ACTION_POWER_CONNECTED");
		//
		// Handler DisconnectHandler =
		// MainService.getDelayedDisconnectHandler();
		// if (DisconnectHandler != null) {
		// DisconnectHandler.removeCallbacksAndMessages(null);
		// }
		//
		if (connected) {
			// Prepare and send a connect intent, which will also start the
			// service
			Intent serviceIntent = new Intent(MainService.ACTION_CONNECT);
			context.startService(serviceIntent);
		}
		// else if (DisconnectHandler != null &&
		// settings.stopOnPowerDisconnected) {
		// long delayMillis = settings.stopOnPowerDelay * 60 * 1000;
		// Log.d("Posting delayed disconnect in " + settings.stopOnPowerDelay +
		// " minutes");
		// Runnable r = new DisconnectDelayed(context);
		// DisconnectHandler.postDelayed(r, delayMillis);
		// }
	}

	class DisconnectDelayed implements Runnable {
		private final Context mContext;

		public DisconnectDelayed(Context ctx) {
			this.mContext = ctx;
		}

		@Override
		public void run() {
			// Intent serviceIntent = new Intent(MainService.ACTION_CONNECT);
			// serviceIntent.putExtra("disconnect", true);
			Log.d(TAG,
					"Issueing disconnect intent because of delayed disconnect");
			// mContext.startService(serviceIntent);
		}

	}
}
