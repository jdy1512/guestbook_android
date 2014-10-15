package com.allchange.guestbook.chat.push;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smackx.delay.packet.DelayInformation;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.allchange.guestbook.R;
import com.allchange.guestbook.chat.DBChatData;
import com.allchange.guestbook.chat.DBModel;
import com.allchange.guestbook.chat.MyData;
import com.allchange.guestbook.chat.XMPPManager;
import com.allchange.guestbook.chat.XMPPManager.OnLoginListener;
import com.allchange.guestbook.chat.XMPPManager.OnMessageReceiveListener;
import com.allchange.guestbook.property.PropertyManager;
import com.allchange.guestbook.splash.SplashActivity;

public class ChatService extends Service {
	public final static String ACTION_BOOT = "com.allchange.guestbook.action.BOOT";
	public final static String TAG = "ChatService";

	private final static int NOTIFICATION_STOP_RINGING = 2;

	private MainService mMainService = null;
	private ChatManagerListener mChatManagerListener;;

	@Override
	public void onCreate() {
		super.onCreate();
		running = true;
		loginXmpp();

		XMPPManager.getInstance().setChatService(this);
		startWakeUpThread();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		// If we get killed, after returning from here, restart
		return START_STICKY;

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	/**
	 * Displays an notification in the status bar to send the command ring:stop
	 */
	public void displayRingingNotification(DBChatData data) {
		// ActivityManager activityManager = (ActivityManager) this
		// .getSystemService(ACTIVITY_SERVICE);
		// List<RunningAppProcessInfo> procInfos = activityManager
		// .getRunningAppProcesses();
		// for (int i = 0; i < procInfos.size(); i++) {
		// // Log.e(TAG, procInfos.get(i).processName);
		// if (procInfos.get(i).processName.equals("com.allchange.guestbook")) {
		// if (!procInfos.get(i).processName
		// .equals("com.allchange.guestbook.chat.push")) {
		// return;
		// }
		//
		// }
		// }

		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		Intent intent = new Intent(getApplicationContext(),
				SplashActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("msg", data.message);

		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				intent, 0);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this)
				.setSmallIcon(R.drawable.launcher_icon)
				.setContentTitle(data.email)
				.setStyle(
						new NotificationCompat.BigTextStyle()
								.bigText(data.message))
				.setContentText(data.message).setAutoCancel(true)
				.setVibrate(new long[] { 0, 500 });

		mBuilder.setContentIntent(contentIntent);
		mNotificationManager
				.notify(NOTIFICATION_STOP_RINGING, mBuilder.build());
	}

	public void requestHandler(final Chat chat, final String message) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (!XMPPManager.getInstance().isConnectedXMPP()) {
					Log.e(TAG, "requestHandler");
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (XMPPManager.getInstance().isConnectedXMPP()) {
						XMPPManager.getInstance().sendMessage(chat, message,
								null);
					}
				}
			}
		}).start();
	}

	/**
	 * Hides the stop ringing notification
	 */
	public void hideRingingNotification() {
		// sNotificationManager.cancel(NOTIFICATION_STOP_RINGING);
	}

	public void loginXmpp() {
		// Toast.makeText(MyApplication.getContext(), "loginXmpp()",
		// Toast.LENGTH_SHORT).show();
//		Log.e(TAG, "======================================");
//		Log.e(TAG, "============  loginXmpp  ==============");
//		Log.e(TAG, "======================================");
		initCharManager();
		String CHAT_ID_TAIL = "_chat_";

		// 로그인페이지에 머무는경우
		if (PropertyManager.getInstance().getId().split("@").length > 1) {

			XMPPManager
					.getInstance()
					.login(PropertyManager.getInstance().getId().split("@")[0]
							+ CHAT_ID_TAIL
							+ PropertyManager.getInstance().getId().split("@")[1],
							"allchange", new OnLoginListener() {

								@Override
								public void onLoginSuccess(String username) {
								}

								@Override
								public void onLoginFail(String username) {
									Log.e(TAG, "chat failed : " + username);
								}
							}, mChatManagerListener);
		}

	}

	private void initCharManager() {
		mChatManagerListener = new ChatManagerListener() {

			@Override
			public void chatCreated(Chat chat, boolean createdLocally) {
				if (!createdLocally) {
					chat.addMessageListener(new MessageListener() {

						@Override
						public void processMessage(
								final Chat chat,
								final org.jivesoftware.smack.packet.Message message) {
//							Log.e(TAG, "processMessage");
							mMainService = XMPPManager.getInstance()
									.getMainService();

							MyData md = new MyData();
							DBChatData d = new DBChatData();

							DelayInformation inf = null;
							try {
								inf = (DelayInformation) message.getExtension(
										"x", "jabber:x:delay");
							} catch (Exception e) {
								e.printStackTrace();
							}
							// get offline message timestamp
							if (inf != null) {
								Date date = inf.getStamp();
								d.time = "" + date.getTime();
							} else {
								Calendar c = Calendar.getInstance();
								Date today = c.getTime();
								d.time = "" + today.getTime();
							}
							md.description = message.getBody();
							// 상대사진의 Url가 들어가야됨
							md.name = "임시임시임시";
							md.email = message.getFrom().split("@")[0]
									.split("_chat_")[0]
									+ "@"
									+ message.getFrom().split("@")[0]
											.split("_chat_")[1];
							// Log.e(TAG, md.email + " : "
							// + md.description + "  " + d.time);
							// DB에 바로바로 저장
							d.email = md.email;
							d.message = md.description;
							String t = DBModel.getInstance()
									.selectChatTableName(md.email);

							// Log.e(TAG, "table : " + t +
							// "  message : "
							// + d.message);
							DBModel.getInstance().insert(d, t);
							if (mListener != null) {
								mListener.onGetChat(t, d.message);
							}
//							Log.e(TAG, "mMainService : " + mMainService);
							// 화면이 꺼진상태에서 알람받으려면 구조가 안과 밖이 바껴야됨
							if (mMainService != null) {
								Log.e(TAG, "mMainService  != null");
								// view를 갱신하기위해서 등록된 listener에게 알리기
								mMainService.onMessageReceived(
										mMessageReceiverListeners, chat,
										message);
							} else {
								if (PropertyManager.getInstance()
										.getMainServiceIsRunning()) {
//									Log.e(TAG, "getMainServiceIsRunning");
								} else {
									if (PropertyManager.getInstance()
											.getGCMSetting()) {
										Log.e(TAG, "getGCMSetting");
										displayRingingNotification(d);
									}
								}
							}
						}

					});
				}
			}
		};
	}

	HashMap<String, OnMessageReceiveListener> mMessageReceiverListeners = new HashMap<String, XMPPManager.OnMessageReceiveListener>();

	// ArrayList<OnMessageReceiveListener> mMessageReceiverListeners = new
	// ArrayList<OnMessageReceiveListener>();

	public void addOnMessageReceiveListener(String targetTableName,
			OnMessageReceiveListener listener) {
		mMessageReceiverListeners.put(targetTableName, listener);
		Log.d(TAG,
				"mMessageReceiverListeners : "
						+ mMessageReceiverListeners.size());
	}

	public void removeOnMessageReceiveListener(OnMessageReceiveListener listener) {
		mMessageReceiverListeners.remove(listener);
	}

	/**
	 * Linstener to update TextView in TripPalsListFragment
	 * 
	 */
	public interface OnGetChatListener {
		public void onGetChat(String tableName, String message);
	}

	OnGetChatListener mListener;

	public void setOnGetChatListener(OnGetChatListener listener) {
		mListener = listener;
	}

	/**
	 * 지대님 ㅇ
	 */

	private boolean running;
	private boolean sleep;

	public void startWakeUpThread() {
		new Thread(new Runnable() {
			public void run() {
				// TODO Auto-generated method stub
				PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
				while (running) {
					try {
						Thread.sleep(5000);
						if (!powerManager.isScreenOn() && !sleep) {
							sleep = !sleep;
							Log.d(TAG, "isScreenSleep");
							registerRestartAlarm(true);
						} else if (powerManager.isScreenOn() && sleep) {
							sleep = !sleep;
							Log.d(TAG, "isScreenOn");
							registerRestartAlarm(false);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					XMPPManager.getInstance().checkConnection();
				}
			}
		}).start();
	}

	public void registerRestartAlarm(boolean isOn) {
		Intent intent = new Intent(ChatService.this,
				RestartReceiverMainSerivce.class);
		intent.setAction(RestartReceiverMainSerivce.ACTION_RESTART_MAINSERVICE);
		PendingIntent sender = PendingIntent.getBroadcast(
				getApplicationContext(), 0, intent, 0);

		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		if (isOn) {
			am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
					SystemClock.elapsedRealtime() + 1000, 10000, sender);
		} else {
			am.cancel(sender);
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
