package com.allchange.guestbook.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.allchange.guestbook.chat.push.ChatService;

/** Allows the application to start at boot */
public class BootReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {

		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
			Intent i = new Intent(context, ChatService.class);
			context.startService(i);
		}
	}
}
