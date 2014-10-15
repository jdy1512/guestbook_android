package com.allchange.guestbook.chat;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.apache.harmony.javax.security.sasl.SaslException;
import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.AlreadyLoggedInException;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.SmackException.NotLoggedInException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.FormField;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.allchange.guestbook.chat.push.ChatService;
import com.allchange.guestbook.chat.push.MainService;
import com.allchange.guestbook.chat.push.XmppSocketFactory;
import com.allchange.guestbook.property.MyApplication;
import com.allchange.guestbook.url.Url;

import de.duenndns.ssl.MemorizingTrustManager;

public class XMPPManager {
	public static final int DISCONNECTED = 1;
	// A "transient" state - will only be CONNECTING *during* a call to start()
	public static final int CONNECTING = 2;
	public static final int CONNECTED = 3;
	// A "transient" state - will only be DISCONNECTING *during* a call to
	// stop()
	public static final int DISCONNECTING = 4;
	// This state means we are waiting for a retry attempt etc.
	// mostly because a connection went down
	public static final int WAITING_TO_CONNECT = 5;
	// We are waiting for a valid data connection
	public static final int WAITING_FOR_NETWORK = 6;

	private static final String TAG = "XMPPManager";
	private static volatile XMPPManager instance;
	Handler mHandler;

	ChatService mChatService;
	MainService mMainService;

	public static XMPPManager getInstance() {
		if (instance == null) {
			instance = new XMPPManager();
		}
		return instance;
	}

	public void setMainService(MainService service) {
		mMainService = service;
	}

	public MainService getMainService() {
		return mMainService;
	}

	public void setChatService(ChatService chatService) {
		mChatService = chatService;
	}

	// private final static String DOMAIN = "talk.google.com";
	private final static int PORT = 5222;
	// private final static String SERVICE = "gmail.com";
	private XMPPConnection mXmppConnection;
	private FileTransferManager mFileManager;

	public interface OnLoginListener {
		public void onLoginSuccess(String username);

		public void onLoginFail(String username);
	}

	public interface OnMessageReceiveListener {
		public void onMessageReceived(Chat chat, Message message);
	}

	public interface OnRosterListener {
		public void onRoasterReceived(List<User> users);
	}

	public interface OnChatListener {
		public void onChatMessageReceived(Chat chat, Message message);
	}

	public interface OnMessageSendListener {
		public void onMessageSendSuccess(Chat chat, String message);

		public void onMessageSendFail(Chat chat, String message);
	}

	public void disconnectXmpp() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (mXmppConnection.isConnected()) {
					try {
						mXmppConnection.disconnect();
					} catch (NotConnectedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();

	}

	public ChatService getChatService() {
		if (mChatService != null) {

		}
		return mChatService;
	}

	private XMPPManager() {
		mHandler = new Handler();

		mChatService = new ChatService();

		/**
		 * 이전 방식
		 */
		// ConnectionConfiguration confing = new ConnectionConfiguration(DOMAIN,
		// PORT, SERVICE);
		// ConnectionConfiguration confing = new ConnectionConfiguration(
		// Url.CHAT_DOMAIN, PORT);
		// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
		// {
		// confing.setKeystoreType("AndroidCAStore");
		// // confing.setTruststoreType("AndroidCAStore");
		// // confing.setTruststorePassword(null);
		// // confing.setTruststorePath(null);
		// } else {
		// confing.setKeystoreType("BKS");
		// // confing.setTruststoreType("BKS");
		// String path = System.getProperty("javax.net.ssl.trustStore");
		// if (path == null)
		// path = System.getProperty("java.home") + File.separator + "etc"
		// + File.separator + "security" + File.separator
		// + "cacerts.bks";
		// confing.setKeystorePath(path);
		// // confing.setTruststorePath(path);
		// }
		// /**
		// * ===================solution==========================
		// *
		// *
		// http://stackoverflow.com/questions/15174040/cant-create-a-multi-user-
		// * chat-muc-room-with-asmack-library-for-android-pack
		// */
		// SmackAndroid.init(MyApplication.getContext());
		//
		// mXmppConnection = new XMPPTCPConnection(confing);
		// // mXmppConnection = new XMPPConnection(confing);

		/**
		 * gtalk에서 가져온 방식
		 */
		ConnectionConfiguration conf = new ConnectionConfiguration(
				Url.CHAT_DOMAIN, PORT);

		conf.setSocketFactory(new XmppSocketFactory());
		conf.setLegacySessionDisabled(false);

		try {
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, MemorizingTrustManager.getInstanceList(MyApplication
					.getContext()), new SecureRandom());
			conf.setCustomSSLContext(sc);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e);
		} catch (KeyManagementException e) {
			throw new IllegalStateException(e);
		}

		// switch (settings.xmppSecurityModeInt) {
		// case SettingsManager.XMPPSecurityOptional:
		// conf.setSecurityMode(ConnectionConfiguration.SecurityMode.enabled);
		// break;
		// case SettingsManager.XMPPSecurityRequired:
		// conf.setSecurityMode(ConnectionConfiguration.SecurityMode.required);
		// break;
		// case SettingsManager.XMPPSecurityDisabled:
		// default:
		conf.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
		// break;
		// }

		// if (settings.useCompression) {
		conf.setCompressionEnabled(true);
		// }

		SmackConfiguration.setDefaultPacketReplyTimeout(5000);

		// disable the built-in ReconnectionManager since we handle this
		conf.setReconnectionAllowed(false);
		conf.setSendPresence(false);

		// conf.setDebuggerEnabled(settings.debugLog);
		// SmackConfiguration.setDefaultPacketReplyTimeout(5 * 1000);

		mXmppConnection = new XMPPTCPConnection(conf);
	}

	public void addFileTransfer() {
		mFileManager = new FileTransferManager(mXmppConnection);
		mFileManager.addFileTransferListener(new FileTransferListener() {

			@Override
			public void fileTransferRequest(FileTransferRequest request) {
				IncomingFileTransfer infile = request.accept();
				try {
					infile.recieveFile(new java.io.File("filename"));
				} catch (SmackException e) {
					e.printStackTrace();
				}

			}
		});
	}

	public void sendFileTransfor(String userId) throws XMPPException {
		OutgoingFileTransfer outfile = mFileManager
				.createOutgoingFileTransfer(userId);
		try {
			outfile.sendFile(new java.io.File("filename"), "file description");
		} catch (SmackException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean addAccount(final String username, final String password,
			Map<String, String> attributes) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					if (!mXmppConnection.isConnected()) {
						mXmppConnection.connect();
					}
					AccountManager am = AccountManager
							.getInstance(mXmppConnection);
					// 8.1.0에서 이런식으로
					// AccountManager am = mXmppConnection.getAccountManager();
					if (am.supportsAccountCreation()) {
						am.createAccount(username, password);
					}
				} catch (XMPPException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
		return true;

	}

	public RosterEntry addRoster(String user, String name, String... groups) {
		Roster roster = mXmppConnection.getRoster();
		if (!roster.contains(user)) {
			for (String group : groups) {
				RosterGroup rg = roster.getGroup(group);
				if (rg == null) {
					roster.createGroup(group);
				}
			}
			try {
				roster.createEntry(user, name, groups);
				return roster.getEntry(user);
			} catch (XMPPException e) {
				e.printStackTrace();
			} catch (NotLoggedInException e) {
				e.printStackTrace();
			} catch (NoResponseException e) {
				e.printStackTrace();
			} catch (NotConnectedException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public boolean isConnectedXMPP() {
		if (mXmppConnection.isConnected()) {
			return true;
		}
		return false;
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

	public void login(final String username, final String password,
			final OnLoginListener listener, final ChatManagerListener cmListener) {
		// Log.e(TAG, "======================================");
		// Log.e(TAG, "==============  login  ================");
		// Log.e(TAG, "======================================");
		new Thread(new Runnable() {

			@Override
			public void run() {
				if (!mXmppConnection.isConnected()) {
					try {
						mXmppConnection.connect();
					} catch (XMPPException e) {
						e.printStackTrace();
					} catch (SmackException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (mXmppConnection.isConnected()) {
					try {
						// Log.e(TAG,
						// "login() username : " + username + ", pw : "
						// + password + "user : "
						// + mXmppConnection.getUser());
						mXmppConnection.login(username, password);
					} catch (XMPPException e) {
						e.printStackTrace();
						return;
					} catch (AlreadyLoggedInException e) {
						e.printStackTrace();
						// return;
					} catch (IllegalStateException e) {
						e.printStackTrace();
						return;
					} catch (SaslException e) {
						e.printStackTrace();
						return;
					} catch (IOException e) {
						e.printStackTrace();
						return;
					} catch (SmackException.NoResponseException e) {
						e.printStackTrace();
						return;
					} catch (SmackException e) {
						e.printStackTrace();
						return;
					} catch (Exception e) {
						e.printStackTrace();
						return;
					}

					Presence p = new Presence(Presence.Type.available);
					p.setStatus("getmessage");
					try {
						mXmppConnection.sendPacket(p);
					} catch (NotConnectedException e) {
						e.printStackTrace();
					}
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							if (mXmppConnection.isConnected()) {
								listener.onLoginSuccess(username);
								// 연결에 성공해야 리스터를 등록한다 안그러면 실패해도 되서 리스너가 두개가됨
								ChatManager.getInstanceFor(mXmppConnection)
										.addChatListener(cmListener);
							} else {
								listener.onLoginFail(username);
							}
						}
					});
				}
			}
		}).start();
	}

	public void getRoster(final OnRosterListener listener) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Roster roster = mXmppConnection.getRoster();
				roster.addRosterListener(new RosterListener() {

					@Override
					public void presenceChanged(Presence presence) {
						Log.i("Roster", "From : " + presence.getFrom() + ", "
								+ presence.getStatus());
					}

					@Override
					public void entriesUpdated(Collection<String> arg0) {
					}

					@Override
					public void entriesDeleted(Collection<String> arg0) {
					}

					@Override
					public void entriesAdded(Collection<String> arg0) {
					}
				});
				Collection<RosterEntry> entries = roster.getEntries();
				final ArrayList<User> users = new ArrayList<User>();
				for (RosterEntry entry : entries) {
					User user = new User();
					user.user = entry;
					user.presence = roster.getPresence(entry.getUser());
					users.add(user);
				}
				mHandler.post(new Runnable() {

					@Override
					public void run() {
						listener.onRoasterReceived(users);
					}
				});
			}
		}).start();
	}

	public void checkConnection() {
		// Log.d(TAG, "checkConnection() : " + mXmppConnection.isConnected());
		// connection상태가 아니면 다시 연결해준다.
		if (mXmppConnection.isConnected() == false) {
			try {
				Log.d(TAG, "try connect");
				mXmppConnection.connect();
				Log.d(TAG, "mChatService : " + mChatService);
				mChatService.loginXmpp();

			} catch (NotConnectedException e) {
				e.printStackTrace();
			} catch (SmackException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (XMPPException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void sendMessage(final Chat chat, final String message,
			final OnMessageSendListener listener) {

		// Log.e(TAG,
		// "sendMessage : isConnected - " + mXmppConnection.isConnected()
		// + " , id - " + mXmppConnection.getUser());

		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					chat.sendMessage(message);
					mHandler.post(new Runnable() {

						@Override
						public void run() {
							if (listener != null) {
								listener.onMessageSendSuccess(chat, message);
							}
						}
					});
				} catch (XMPPException e) {
					Log.e(TAG, "run1");
					e.printStackTrace();
					mHandler.post(new Runnable() {

						@Override
						public void run() {
							listener.onMessageSendFail(chat, message);
						}
					});
				} catch (SmackException.NotConnectedException e2) {
					Log.e(TAG, "SmackException.NotConnectedException");
					try {
						mXmppConnection.connect();
					} catch (SmackException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (XMPPException e) {
						e.printStackTrace();
					}
					// mChatService.loginXmpp();
					mChatService.requestHandler(chat, message);
					e2.printStackTrace();
				}
			}
		}).start();
	}

	public Chat createChat(String user,
			final OnMessageReceiveListener listener, String targetTableName) {
		// Log.e(TAG,
		// "createChat : isConnected - " + mXmppConnection.isConnected()
		// + " , you - " + mXmppConnection.getUser());
		if (mXmppConnection.getUser() == null
				|| mXmppConnection.getUser().equals("null")) {
			mChatService.loginXmpp();
		}

		// 액티비티가 떠있는 상태에서 화면 업데이트를 위한 리스너 등록
		mChatService.addOnMessageReceiveListener(targetTableName, listener);
		return ChatManager.getInstanceFor(mXmppConnection).createChat(user,
				new MessageListener() {

					@Override
					public void processMessage(final Chat chat,
							final Message message) {
						mHandler.post(new Runnable() {

							@Override
							public void run() {
								// listener.onMessageReceived(chat, message);
							}
						});
					}
				});
		// asmack-android-8-0.8.1.1.jar 버전
		// return mXmppConnection.getChatManager().createChat(user,
		// new MessageListener() {
		//
		// @Override
		// public void processMessage(final Chat chat,
		// final Message message) {
		// mHandler.post(new Runnable() {
		//
		// @Override
		// public void run() {
		// listener.onChatMessageReceived(chat, message);
		// }
		// });
		// }
		// });
	}

	public boolean createGroup(String groupName) {
		if (mXmppConnection == null)
			return false;
		try {
			mXmppConnection.getRoster().createGroup(groupName);
			Log.e("Group created : ", groupName);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Create a room
	 * 
	 * @param roomName
	 *            The name of the room
	 */
	public MultiUserChat createRoom(String roomName, String password) {
		if (mXmppConnection == null)
			return null;

		MultiUserChat muc = null;
		try {
			// Create a MultiUserChat
			muc = new MultiUserChat(mXmppConnection, roomName + "@asdfasdf."
					+ mXmppConnection.getServiceName());
			// Create a chat room
			muc.create(roomName);
			// To obtain the chat room configuration form
			Form form = muc.getConfigurationForm();
			// Create a new form to submit the original form according to the.
			Form submitForm = form.createAnswerForm();
			// To submit the form to add a default reply
			for (FormField field : form.getFields()) {
				if (!FormField.TYPE_HIDDEN.equals(field.getType())
						&& field.getVariable() != null) {
					// Set default values for an answer
					submitForm.setDefaultAnswer(field.getVariable());
				}
			}
			// asmack-android-8-0.8.1.1.jar 버전
			// for (Iterator<FormField> fields = form.getFields(); fields
			// .hasNext();) {
			// FormField field = (FormField) fields.next();
			// if (!FormField.TYPE_HIDDEN.equals(field.getType())
			// && field.getVariable() != null) {
			// // Set default values for an answer
			// submitForm.setDefaultAnswer(field.getVariable());
			// }
			// }
			// Set the chat room of the new owner
			List<String> owners = new ArrayList<String>();
			owners.add(mXmppConnection.getUser());// The user JID
			submitForm.setAnswer("muc#roomconfig_roomowners", owners);
			// Set the chat room is a long chat room, soon to be preserved
			submitForm.setAnswer("muc#roomconfig_persistentroom", true);
			// Only members of the open room
			submitForm.setAnswer("muc#roomconfig_membersonly", false);
			// Allows the possessor to invite others
			submitForm.setAnswer("muc#roomconfig_allowinvites", true);
			if (!password.equals("")) {
				// Enter the password if needed
				submitForm.setAnswer("muc#roomconfig_passwordprotectedroom",
						true);
				// Set to enter the password
				submitForm.setAnswer("muc#roomconfig_roomsecret", password);
			}
			// Can be found in possession of real JID role
			// submitForm.setAnswer("muc#roomconfig_whois", "anyone");
			// Login room dialogue
			submitForm.setAnswer("muc#roomconfig_enablelogging", true);
			// Only allow registered nickname log
			submitForm.setAnswer("x-muc#roomconfig_reservednick", true);
			// Allows the user to modify the nickname
			submitForm.setAnswer("x-muc#roomconfig_canchangenick", false);
			// Allows the user to register the room
			submitForm.setAnswer("x-muc#roomconfig_registration", false);
			// Send the completed form (the default) to the server to configure
			// the chat room
			muc.sendConfigurationForm(submitForm);
		} catch (XMPPException e) {
			e.printStackTrace();
			return null;
		} catch (NoResponseException e) {
			e.printStackTrace();
		} catch (NotConnectedException e) {
			e.printStackTrace();
		} catch (SmackException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return muc;
	}

	/**
	 * To join the conference room
	 * 
	 * @param user
	 *            Nickname
	 * @param password
	 *            The conference room password
	 * @param roomsName
	 *            The meeting room
	 */
	public MultiUserChat joinMultiUserChat(String user, String roomsName,
			String password) {
		if (mXmppConnection == null)
			return null;
		try {
			// Create a MultiUserChat window using XMPPConnection
			MultiUserChat muc = new MultiUserChat(mXmppConnection, roomsName
					+ "@conference." + mXmppConnection.getServiceName());
			// The number of chat room services will decide to accept the
			// historical record
			DiscussionHistory history = new DiscussionHistory();
			history.setMaxChars(0);
			// history.setSince(new Date());
			// Users to join in the chat room
			muc.join(user, password, history,
					SmackConfiguration.getDefaultPacketReplyTimeout());
			Log.i("MultiUserChat", "The conference room [" + roomsName
					+ "] success....");
			return muc;
		} catch (XMPPException e) {
			e.printStackTrace();
			Log.i("MultiUserChat", "The conference room [" + roomsName
					+ "] join failure....");
		} catch (NoResponseException e) {
			e.printStackTrace();
		} catch (NotConnectedException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Query the conference room member name
	 * 
	 * @param muc
	 */
	public List<String> findMulitUser(MultiUserChat muc) {
		if (mXmppConnection == null)
			return null;
		// List<String> listUser = new ArrayList<String>();
		List<String> listUser = muc.getOccupants();
		// asmack-android-8-0.8.1.1.jar 버전
		// // Traverse the chat room name
		// while (it.hasNext()) {
		// // Chat room members name
		// String name = StringUtils.parseResource(it.next());
		// listUser.add(name);
		// }
		return listUser;
	}

}
