package com.allchange.guestbook.chat.push;

import java.util.HashMap;

import org.jivesoftware.smack.Chat;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.Process;
import android.util.Log;

import com.allchange.guestbook.chat.XMPPManager;
import com.allchange.guestbook.chat.XMPPManager.OnMessageReceiveListener;

public class MainService extends Service {
	public final static String TAG = "MainService";
	private static MainService sIntance = null;

	private final static int NOTIFICATION_STOP_RINGING = 2;

	// The following actions are documented and registered in our manifest
	public final static String ACTION_CONNECT = "com.allchange.guestbook.action.CONNECT";
	public final static String ACTION_DISCONNECT = "com.allchange.guestbook.action.DISCONNECT";
	public final static String ACTION_TOGGLE = "com.allchange.guestbook.action.TOGGLE";
	public final static String ACTION_SEND = "com.allchange.guestbook.action.SEND";
	public final static String ACTION_COMMAND = "com.allchange.guestbook.action.COMMAND";

	// The following actions are undocumented and internal to our
	// implementation.
	public final static String ACTION_NETWORK_STATUS_CHANGED = "com.allchange.guestbook.action.NETWORK_STATUS_CHANGED";

	// A list of intent actions that the XmppManager broadcasts.
	public static final String ACTION_XMPP_MESSAGE_RECEIVED = "com.allchange.guestbook.action.XMPP.MESSAGE_RECEIVED";
	public static final String ACTION_XMPP_PRESENCE_CHANGED = "com.allchange.guestbook.action.XMPP.PRESENCE_CHANGED";
	public static final String ACTION_XMPP_CONNECTION_CHANGED = "com.allchange.guestbook.action.XMPP.CONNECTION_CHANGED";
	private static final String PREF_IS_RUNNING = "mainservice_running";

	// A bit of a hack to allow global receivers to know whether or not
	// the service is running, and therefore whether to tell the service
	// about some events
	public static boolean IsRunning = false;

	private static boolean sListenersActive = false;

	private static NotificationManager sNotificationManager;
	private static BroadcastReceiver sXmppConChangedReceiver;
	private static BroadcastReceiver sStorageLowReceiver;
	// private static KeyboardInputMethodService sKeyboardInputMethodService;
	private static PowerManager sPm;
	private static PowerManager.WakeLock sWl;
	private static PendingIntent sPendingIntentLaunchApplication = null;
	private static PendingIntent sPendingIntentStopRinging = null;

	// This is the object that receives interactions from clients. See
	// RemoteService for a more complete example.
	// private final IBinder mBinder = new LocalBinder();

	private long mHandlerThreadId;

	private static Context sUiContext;

	private static volatile Handler sHandler = new Handler();
	private static Handler sDelayedDisconnectHandler;

	// some stuff for the async service implementation - borrowed heavily from
	// the standard IntentService, but that class doesn't offer fine enough
	// control for "foreground" services.
	private static volatile Looper mServiceLooper;
	private static volatile Handler mServiceHandler;

	private final class ServiceHandler extends Handler {
		public ServiceHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			Log.e(TAG, "handleMessage");

			Intent intent = (Intent) msg.obj;
			String action = "";
			if (!(intent == null)) {
				action = intent.getAction();
			} else {
				if (XMPPManager.getInstance().isConnectedXMPP()) {
					// loginXmpp();
				}
			}
			if (action.equals(ACTION_CONNECT)
					|| action.equals(ACTION_DISCONNECT)
					|| action.equals(ACTION_TOGGLE)
					|| action.equals(ACTION_NETWORK_STATUS_CHANGED)) {
				// onHandleIntentTransportConnection(intent);
				if (XMPPManager.getInstance().isConnectedXMPP()) {
					// loginXmpp();
				}
			} else if (!action.equals(ACTION_XMPP_CONNECTION_CHANGED)) {
				// onHandleIntentMessage(intent);
			}

			// Stop the service using the startId, so that we don't stop
			// the service in the middle of handling another job
			stopSelf(msg.arg1);
		}
	}

	public static Intent newSvcIntent(final Context ctx, final String action,
			final String message, final String to) {
		final Intent i = new Intent(action, null, ctx, MainService.class);
		if (message != null) {
			i.putExtra("message", message);
		}
		if (to != null) {
			i.putExtra("to", to);
		}
		return i;
	}

	public static void startSvcIntent(final Context ctx, final String action) {
		final Intent i = newSvcIntent(ctx, action, null, null);
		ctx.startService(i);
	}

	// private static volatile ServiceHandler sServiceHandler;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		sIntance = this;

		XMPPManager.getInstance().setMainService(this);

		// Start up the thread running the service. Note that we create a
		// separate thread because the service normally runs in the process's
		// main thread, which we don't want to block. We also make it
		// background priority so CPU-intensive work will not disrupt our UI.
		HandlerThread thread = new HandlerThread("GuestBook",
				Process.THREAD_PRIORITY_BACKGROUND);
		thread.start();

		// Get the HandlerThread's Looper and use it for our Handler
		mServiceLooper = thread.getLooper();
		mServiceHandler = new ServiceHandler(mServiceLooper);

		sNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		// sPm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		// sWl = sPm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "guestbook"
		// + " WakeLock");
		//
		// sUiContext = this;
		//
		// sPendingIntentLaunchApplication = PendingIntent.getActivity(this, 0,
		// new Intent(this, MainActivity.class), 0);
		// sNotificationManager = (NotificationManager)
		// getSystemService(Context.NOTIFICATION_SERVICE);
		// // mCommandManager = new CommandManager();

		Log.e(TAG, "service thread created - IsRunning is set to true");
		IsRunning = true;
		// PropertyManager.getInstance().setMainServiceIsRunning(true);

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		int res = super.onStartCommand(intent, flags, startId);
		// Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

		// For each start request, send a message to start a job and deliver the
		// start ID so we know which request we're stopping when we finish the
		// job
		Message msg = mServiceHandler.obtainMessage();
		msg.arg1 = startId;
		mServiceHandler.sendMessage(msg);

		// If we get killed, after returning from here, restart
		return START_STICKY;

	}

	@Override
	public void onDestroy() {
		// PublicIntentReceiver.onServiceStop();
		// PropertyManager.getInstance().setMainServiceIsRunning(false);
		IsRunning = false;
		sIntance = null;
		XMPPManager.getInstance().setMainService(null);
		super.onDestroy();
		Log.e(TAG, "MainService onDestroy(): service destroyed");
		mServiceLooper.quit();

		// Intent serviceIntent = new Intent(MyApplication.getContext(),
		// MainService.class);
		// MyApplication.getContext().startService(serviceIntent);
	}

	public void onMessageReceived(
			final HashMap<String, OnMessageReceiveListener> listners,
			final Chat chat, final org.jivesoftware.smack.packet.Message message) {
		sHandler.post(new Runnable() {
			@Override
			public void run() {
				// method stub
				for (String key : listners.keySet()) {
					// Log.e(TAG, "chat : " + chat.getParticipant()
					//	+ ", message : " + message.getBody());
					listners.get(key).onMessageReceived(chat, message);
				}
			}
		});
	}

}

//
// public class ChatPushService extends Service {
//
// public static final String ACTION_MESSAGE =
// "com.allchange.guestbook.chat.push.message";
// public static final String ACTION_RESTART_SERVICE =
// "com.allchange.guestbook.chat.push.restart";
//
// private static final String TAG = "ChatPushService";
// private static final int sleepTime = 10 * 1000;
// private XMPPConnection connection = null;
// private Thread thread = null;
//
// // private static String googleAppEngineJID = "momo-time@appspot.com";
// private static String resourceId = "push";
// private static boolean isNetworkAvailable = false;
//
// private MessageListener messageListener = new MessageListener() {
// @Override
// public void processMessage(Chat chat, Message message) {
// String messageString = message.getBody();
// Log.d(TAG, "Message - " + messageString);
// Intent intent = new Intent();
// intent.setAction(ACTION_MESSAGE);
// intent.putExtra("message", messageString);
// sendBroadcast(intent);
// }
// };
//
// private BroadcastReceiver networkBroadcastReceiver = new BroadcastReceiver()
// {
// @Override
// public void onReceive(Context context, Intent intent) {
// String action = intent.getAction();
// if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
// Log.e(TAG, "NetworkBroadcastReceiver...");
// if (isNetworkAvailable = isNetworkAvailbe()) {
// if (connection != null) {
// connection.disconnect();
// connection = null;
// }
// } else {
// Log.e(TAG, "Network Unavailable...");
// }
// }
// }
// };
//
// private boolean isNetworkAvailbe() {
// boolean isNetworkAvailbe = false;
// ConnectivityManager connectivityManager = (ConnectivityManager)
// getSystemService(Context.CONNECTIVITY_SERVICE);
// NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
// if (networkInfo != null && networkInfo.isConnected()) {
// isNetworkAvailbe = true;
// }
// return isNetworkAvailbe;
// }
//
// public void disconnect() throws XMPPException {
// if (connection != null && connection.isConnected()) {
// connection.disconnect();
// }
// }
//
// public void connectLogin() throws XMPPException {
// Log.e(TAG, "connectLogin");
// final String CHAT_ID_TAIL = "_chat_";
// XMPPManager.getInstance().login(
// PropertyManager.getInstance().getId().split("@")[0]
// + CHAT_ID_TAIL
// + PropertyManager.getInstance().getId().split("@")[1],
// "allchange", new OnLoginListener() {
//
// @Override
// public void onLoginSuccess(String username) {
// // XMPPManager.getInstance().createRoom("testaa",
// // "allchange");
// Toast.makeText(MyApplication.getContext(),
// "login success", Toast.LENGTH_SHORT).show();
// }
//
// @Override
// public void onLoginFail(String username) {
// Toast.makeText(MyApplication.getContext(),
// "login failed", Toast.LENGTH_SHORT).show();
// }
// });
//
// }
//
// public void sendPresenceAvailable() {
// Presence presence = new Presence(Presence.Type.available);
// connection.sendPacket(presence);
// }
//
// @Override
// public IBinder onBind(Intent intent) {
// return null;
// }
//
// @Override
// public void onCreate() {
// super.onCreate();
// // PushServiceRestarter.unregisterRestartAlram(ChatPushService.this);
// Log.e(TAG, "onCreate");
// try {
// connectLogin();
// } catch (XMPPException e) {
// e.printStackTrace();
// }
// // registerNetworkReciver();
// isNetworkAvailable = isNetworkAvailbe();
// }
//
// private void registerNetworkReciver() {
// IntentFilter filter = new IntentFilter();
// filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
// registerReceiver(networkBroadcastReceiver, filter);
// }
//
// private void unregisterNetworkReciver() {
// unregisterReceiver(networkBroadcastReceiver);
// }
//
// @Override
// public void onDestroy() {
// super.onCreate();
// // 서비스가 죽었을 때 다시 살리는 알람 코드
// // PushServiceRestarter.registerRestartAlram(ChatPushService.this);
// // Log.e(TAG, "onDestroy");
// // unregisterNetworkReciver();
// destroyThread();
// }
//
// private void destroyThread() {
// Log.e(TAG, "Try destory thread.");
// if (thread != null) {
// thread.destroy();
// Log.e(TAG, "Destroyed thread.");
// }
// }
// }
