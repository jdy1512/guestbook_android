package com.allchange.guestbook.chat.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class RestartReceiverMainSerivce extends BroadcastReceiver {
	public static final String TAG = "RestartReceiverChatSerivce";
	public static final String ACTION_RESTART_MAINSERVICE = "ACTION.ChatService.Restart";

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(ACTION_RESTART_MAINSERVICE)) {
			Intent i = new Intent(context, ChatService.class);
			context.startService(i);
		}
	}
}