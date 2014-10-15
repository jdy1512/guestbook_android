package com.allchange.guestbook.chat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;

import android.app.Activity;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.allchange.guestbook.R;
import com.allchange.guestbook.chat.XMPPManager.OnMessageReceiveListener;
import com.allchange.guestbook.chat.XMPPManager.OnMessageSendListener;
import com.allchange.guestbook.main.profiles.ChattingRoomPeopleData;
import com.allchange.guestbook.property.MyApplication;
import com.allchange.guestbook.property.PropertyManager;
import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;

public class MessageActivity extends Activity {
	private static final String TAG = "MessageActivity";
	private static final String SERVER_DETAIL = "@54.213.32.124";
	// private static final String SERVER_DETAIL = "@" + Url.CHAT_DOMAIN;
	public static final String CHAT_ID_TAIL = "_chat_";

	// String[] columns = { DBConstants.ChatTable._ID,
	// DBConstants.ChatTable.COLUMN_ID,
	// DBConstants.ChatTable.COLUMN_CONTENT };
	Cursor mCursor = null;

	public static final String PARAM_USER = "user";
	public static final String PARAM_USER_NAME = "user_name";
	// int[] photos = { R.drawable.default_pic, R.drawable.delete_pic };

	ListView mList;
	EditText inputView;
	ArrayList<MyData> mData = new ArrayList<MyData>();
	ChattingRoomPeopleData mPeopleData = null;

	MyAdapter mAdapter;

	Chat mChat;
	public static String mUser;
	OnMessageReceiveListener mReceiveListner;
	private String targetTableName;
	private ImageView ib_trans_close, ib_trans;
	private LinearLayout ll_trans;

	/**
	 * 번역을 위한 변수들
	 */
	ArrayList<String> Lang1List = new ArrayList<String>();
	ArrayList<String> Lang2List = new ArrayList<String>();
	Language[] langArr = { Language.AUTO_DETECT, Language.KOREAN,
			Language.ENGLISH, Language.JAPANESE, Language.CHINESE_TRADITIONAL };
	String[] strArr = { "자동감지", "한국어", "English", "日本語", "中國語" };
	HashMap<String, Language> mResoloverLang = new HashMap<String, Language>();
	Language selected1, selected2;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message_layout);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		// Bundle bundle = getIntent().getExtras();

		// get member data from db
		String target = DBModel.getInstance().getMember(
				getIntent().getStringExtra("data"));

		mPeopleData = new ChattingRoomPeopleData();
		mPeopleData.email_id = target;
		mPeopleData.firstname = ChatCacheStore.getInstance()
				.getFirstNameResovler().get(target);
		mPeopleData.lastname = ChatCacheStore.getInstance()
				.getLastNameResovler().get(target);
		mPeopleData.profile_img_path = ChatCacheStore.getInstance()
				.getImageResovler().get(target);
		// mPeopleData = bundle.getParcelable("data");
		final String mUserName = mPeopleData.email_id;
		// 채팅할 상대 정보
		final String mUser = mPeopleData.email_id.trim().split("@")[0]
				+ CHAT_ID_TAIL + mPeopleData.email_id.split("@")[1];
		final String chatUser = mUser + SERVER_DETAIL;

		if (mPeopleData.firstname == null
				|| mPeopleData.firstname.equals("null")) {
		} else {
			setTitle(mPeopleData.firstname + mPeopleData.lastname);
		}
		// 사용자의 정보를 가져온다
		ChatCacheStore.getInstance().putAtResovler(
				PropertyManager.getInstance().getId(),
				PropertyManager.getInstance().getProperties()
						.split(PropertyManager.SPLITE_STRING)[3],
				PropertyManager.getInstance().getProperties()
						.split(PropertyManager.SPLITE_STRING)[4],
				PropertyManager.getInstance().getProperties()
						.split(PropertyManager.SPLITE_STRING)[7]);

		// 상대방의 정보를 가져온다
		try {
			ChatCacheStore.getInstance().putAtResovler(mPeopleData.email_id,
					mPeopleData.firstname, mPeopleData.lastname,
					mPeopleData.profile_img_path);
		} catch (Exception e) {
		}

		targetTableName = DBModel.getInstance().selectChatTableName(
				mPeopleData.email_id);
		getDataInDB(mUser);

		inputView = (EditText) findViewById(R.id.chat_editText);
		mList = (ListView) findViewById(R.id.chat_listview);

		mAdapter = new MyAdapter(this, mData);
		mAdapter.setOnMyAdapterListener(new MyAdapter.OnMyAdapterListener() {
			@Override
			public void onItemImageClick(MyAdapter adapter, View v, MyData d) {
				// Toast.makeText(MessageActivity.this,
				// "Item Image Clicked..." + d.title, Toast.LENGTH_SHORT)
				// .show();
			}
		});

		mList.setAdapter(mAdapter);
		mList.setSelection(mAdapter.getCount() - 1);

		// 메시지 보내기
		final ToggleButton btn_send = (ToggleButton) findViewById(R.id.chat_button_write);

		btn_send.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (btn_send.isChecked()) {
					btn_send.setChecked(false);
					return;
				}
				String message = inputView.getText().toString();
				inputView.setText("");
				XMPPManager.getInstance().sendMessage(mChat, message,
						new OnMessageSendListener() {

							@Override
							public void onMessageSendSuccess(Chat chat,
									String message) {
								// mAdapter.add("Me" + ":" + message);

								MyData md = new MyData();
								DBChatData d = new DBChatData();

								Calendar c = Calendar.getInstance();
								Date today = c.getTime();
								md.date = "" + today.getTime();

								md.description = message;
								// 본인 사진의 Url가 들어가야됨
								md.imageUrl = PropertyManager.getInstance()
										.getProperties()
										.split(PropertyManager.SPLITE_STRING)[6];
								md.name = "Me";
								md.email = PropertyManager.getInstance()
										.getId();
								mAdapter.add(md);
								mList.setSelection(mAdapter.getCount() - 1);

								// DB에 바로바로 저장
								d.email = md.email;
								d.message = md.description;
								d.time = "" + today.getTime();
								DBModel.getInstance()
										.insert(d, targetTableName);
							}

							@Override
							public void onMessageSendFail(Chat chat,
									String message) {
								Toast.makeText(MessageActivity.this,
										"send fail : " + message,
										Toast.LENGTH_SHORT).show();
							}
						});
			}
		});

		// 번역하기
		ib_trans = (ImageView) findViewById(R.id.chat_btn_trans);
		ib_trans.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// 다이얼로그를 이용한 번역
				// Dialog_Translate dt = new Dialog_Translate();
				// dt.setTextForTrans(et_write.getText().toString());
				// dt.show(getSupportFragmentManager(), TAG);
				if (selected1 != null && selected2 != null) {
					// Log.e(TAG, "selected1 : " + selected1.toString());
					// Log.e(TAG, "selected2 : " + selected2.toString());
					new MyAsyncTask() {
						protected void onPostExecute(Boolean result) {
							inputView.setText(mText);
						}
					}.execute();
				}
			}
		});
		ll_trans = (LinearLayout) findViewById(R.id.chat_layout_trans);
		ll_trans = (LinearLayout) findViewById(R.id.chat_layout_trans);
		ib_trans_close = (ImageView) findViewById(R.id.chat_btn_trans_close);
		ib_trans_close.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ll_trans.setVisibility(View.GONE);
			}
		});

		Button btn = (Button) findViewById(R.id.chat_btn_show_trans);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ll_trans.setVisibility(View.VISIBLE);
			}
		});

		inputView.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() > 0) {
					btn_send.setChecked(true);
				} else {
					btn_send.setChecked(false);
				}
			}
		});

		mReceiveListner = new OnMessageReceiveListener() {

			@Override
			public void onMessageReceived(Chat chat, Message message) {
				MyData md = new MyData();
				md.description = message.getBody();
				// 상대사진의 Url가 들어가야됨
				md.imageUrl = mPeopleData.profile_img_path;
				md.name = mPeopleData.firstname + mPeopleData.lastname;
				md.email = mPeopleData.email_id;
				Calendar c = Calendar.getInstance();
				Date today = c.getTime();
				md.date = "" + today.getTime();
				Log.e(TAG, "date : " + md.date);
				mAdapter.add(md);
				mList.setSelection(mAdapter.getCount() - 1);
			}
		};
		// Log.e(TAG, "targetTableName " + targetTableName);
		mChat = XMPPManager.getInstance().createChat(chatUser, mReceiveListner,
				targetTableName);

		// mChat = XMPPManager.getInstance().createChat(chatUser,
		// new OnChatListener() {
		//
		// @Override
		// public void onChatMessageReceived(Chat chat,
		// org.jivesoftware.smack.packet.Message message) {
		// // Auto-generated
		// // method stub
		// // mAdapter.add(message.getFrom()
		// // + ":" +
		// // message.getBody());
		//
		// // XMPP에 넣어버림
		// MyData md = new MyData();
		// md.description = message.getBody();
		// // 상대사진의 Url가 들어가야됨
		// md.imageUrl = mPeopleData.profile_img_path;
		// md.name = mPeopleData.firstname + mPeopleData.lastname;
		// md.email = mPeopleData.email_id;
		// mAdapter.add(md);
		// mList.setSelection(mAdapter.getCount() - 1);
		// }
		// });

		// XMPPManager.getInstance().getRoster(new
		// OnRosterListener() {
		//
		// @Override
		// public void onRoasterReceived(List<User> users) {
		// MessageActivity.mUser = users.get(0).user.getUser();
		// // mUser =
		// // getIntent().getStringExtra(PARAM_USER);
		// mChat = XMPPManager.getInstance().createChat(mUser,
		// new OnChatListener() {
		//
		// @Override
		// public void onChatMessageReceived(
		// Chat chat,
		// org.jivesoftware.smack.packet.Message message) {
		// // Auto-generated
		// // method stub
		// // mAdapter.add(message.getFrom()
		// // + ":" +
		// // message.getBody());
		// MyData md = new MyData();
		// md.description = message.getBody();
		// md.imageId = R.drawable.delete_pic;
		// md.title = mUser;
		// mAdapter.add(md);
		// }
		// });
		// }
		// });

		for (int i = 0; i < strArr.length; i++) {
			mResoloverLang.put(strArr[i], langArr[i]);
			Lang1List.add(strArr[i]);
		}

		Spinner spinner = (Spinner) findViewById(R.id.chat_spinner1);

		// Create the ArrayAdapter
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, Lang1List);

		// Set the Adapter
		spinner.setAdapter(arrayAdapter);

		// Set the ClickListener for Spinner
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> adapterView, View view,
					int i, long l) {
				selected1 = mResoloverLang.get(Lang1List.get(i));
			}

			// If no option selected
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		for (int i = 1; i < strArr.length; i++) {
			mResoloverLang.put(strArr[i], langArr[i]);
			Lang2List.add(strArr[i]);
		}

		spinner = (Spinner) findViewById(R.id.chat_spinner2);

		// Create the ArrayAdapter
		arrayAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, Lang2List);

		// Set the Adapter
		spinner.setAdapter(arrayAdapter);

		// Set the ClickListener for Spinner
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> adapterView, View view,
					int i, long l) {
				selected2 = mResoloverLang.get(Lang2List.get(i));
			}

			// If no option selected
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}

	private String mText = new String();

	class MyAsyncTask extends AsyncTask<Void, Integer, Boolean> {
		@Override
		protected Boolean doInBackground(Void... arg0) {
			Translate.setClientId("allchange_guestbook");
			Translate
					.setClientSecret("rR3GUdisIb22vSsGZw2yvPgttp1NLpatRoZ+8WwXV2w=");
			try {
				mText = Translate.execute(inputView.getText().toString(),
						selected1, selected2);
				Log.e(TAG, "mText : " + mText);
			} catch (Exception e) {
				mText = e.toString();
			}
			return true;
		}
	}

	private void getDataInDB(String tName) {
		Cursor c = DBModel.getInstance().queryChat(targetTableName);
		int nameIndex = c.getColumnIndex(DBConstants.ChatTable.COLUMN_EMAIL);
		int descripIndex = c
				.getColumnIndex(DBConstants.ChatTable.COLUMN_MESSAGE);
		int dateIndex = c
				.getColumnIndex(DBConstants.ChatTable.COLUMN_MESSAGE_TIME);
		while (c.moveToNext()) {
			String email = c.getString(nameIndex);
			String descrip = c.getString(descripIndex);
			String date = c.getString(dateIndex);
			MyData md = new MyData();
			md.email = email;
			md.name = ChatCacheStore.getInstance().getFirstNameResovler()
					.get(email)
					+ ChatCacheStore.getInstance().getLastNameResovler()
							.get(email);
			md.imageUrl = ChatCacheStore.getInstance().getImageResovler()
					.get(email);
			md.description = descrip;
			md.date = date;
			mData.add(md);
		}

	}

	// final Thread th = new Thread(new Runnable() {
	//
	// @Override
	// public void run() {
	// String CHAT_ID_TAIL = "_chat_";
	// XMPPManager
	// .getInstance()
	// .login(PropertyManager.getInstance().getId().split("@")[0]
	// + CHAT_ID_TAIL
	// + PropertyManager.getInstance().getId().split("@")[1],
	// "allchange", new OnLoginListener() {
	//
	// @Override
	// public void onLoginSuccess(String username) {
	// }
	//
	// @Override
	// public void onLoginFail(String username) {
	// th.start();
	// }
	// });
	// }
	// });

	@Override
	protected void onResume() {

		// connetion 확인하고 끊어졋으면 연결하기
		XMPPManager.getInstance().checkConnection();

		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// DBModel.getInstance().insertMulti(mData);

		// for (int i = 0; i < mData.size(); i++) {
		// MyData data = new MyData();
		// data.title = mData.get(i).title;
		// data.description = mData.get(i).description;
		// DBModel.getInstance().insert(data);
		// // Toast.makeText(MessageActivity.this, "added", Toast.LENGTH_SHORT)
		// // .show();
		// mCursor = DBModel.getInstance().query(columns);
		// }

		if (mCursor != null) {
			mCursor.close();
			mCursor = null;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// Respond to the action bar's Up/Home button
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
