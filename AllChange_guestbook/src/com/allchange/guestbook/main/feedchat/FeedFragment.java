package com.allchange.guestbook.main.feedchat;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
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
import com.allchange.guestbook.aquery.AndroidQuery;
import com.allchange.guestbook.aquery.AqueryWriteTextCallbackListener;
import com.allchange.guestbook.aquery.get.feed.AqueryGetFeedCallbackListener;
import com.allchange.guestbook.dialog.Dialog_Loading;
import com.allchange.guestbook.main.feedchat.NewsFeedListItemUpdate.OnRefreshFeedListener;
import com.allchange.guestbook.property.MyApplication;
import com.allchange.guestbook.property.PropertyManager;
import com.allchange.guestbook.url.Url;
import com.androidquery.callback.AjaxStatus;
import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;

public class FeedFragment extends Fragment implements
		AqueryWriteTextCallbackListener, AqueryGetFeedCallbackListener,
		OnScrollListener, OnRefreshFeedListener {
	private static final String TAG = "FeedFragment";
	public static final String LOADINGBAR = "loardingbar";
	public static final String EXTRA_ROOM_NUMBER = "SearchActivity";
	private static final int REQUEST_MAKE_ROOM = 100;
	private EditText et_write;
	ToggleButton imageBtn;
	// ListView list_newsFeed;
	// NewsFeedListAdapter newsFeedAdapter;
	private String fee_text;
	private Handler mHandler;
	private Runnable mRunnable;
	ListView list_newsFeed;
	NewsFeedListAdapter newsFeedAdapter;
	private int currentFirstVisibleItem;
	private int currentVisibleItemCount;
	private int currentScrollState;
	private ImageView ib_trans_close, ib_trans;
	private LinearLayout ll_trans;

	/**
	 * 번역을 위한 변수들
	 */
	Language[] langArr = { Language.AUTO_DETECT, Language.KOREAN,
			Language.ENGLISH, Language.JAPANESE, Language.CHINESE_TRADITIONAL };
	String[] strArr = { "자동감지", "한국어", "English", "日本語", "中國語" };
	Language selected1, selected2;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.feed_fragment, container, false);
		if (!isSetMyHouse()) {
			return v;
		}

		final ArrayList<String> Lang1List = new ArrayList<String>();
		final ArrayList<String> Lang2List = new ArrayList<String>();
		final HashMap<String, Language> mResoloverLang = new HashMap<String, Language>();

		et_write = (EditText) v.findViewById(R.id.feed_fragment_editText);

		list_newsFeed = (ListView) v.findViewById(R.id.feed_fragment_newsfeed);
		list_newsFeed.setOnScrollListener(this);

		newsFeedAdapter = new NewsFeedListAdapter(getActivity(),
				new ArrayList<MessageData>(), this);
		list_newsFeed.setAdapter(newsFeedAdapter);

		// 글쓰기
		imageBtn = (ToggleButton) v
				.findViewById(R.id.feed_fragment_button_write);
		imageBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!imageBtn.isChecked()) {
					setFeedText(et_write.getText().toString());
				} else {
					Toast.makeText(getActivity(),
							getResources().getString(R.string.short_text),
							Toast.LENGTH_SHORT).show();
					imageBtn.setChecked(false);
				}
			}
		});

		et_write.addTextChangedListener(new TextWatcher() {

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
					imageBtn.setChecked(true);
				} else {
					imageBtn.setChecked(false);
				}
			}
		});

		if (!Dialog_Loading.getInstance().isOnScreen) {
			Dialog_Loading.getInstance().show(
					getActivity().getSupportFragmentManager(), TAG);
		}
		AndroidQuery.getInstance().requestGetFeed(FeedFragment.this,
				Url.SERVER_SHOUT_OUT, null);

		/**
		 * 번역을 위한 ...
		 */

		// 번역하기
		ib_trans = (ImageView) v.findViewById(R.id.feed_fragment_btn_trans);
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
							et_write.setText(mText);
						}
					}.execute();
				}
			}
		});

		ll_trans = (LinearLayout) v
				.findViewById(R.id.feed_fragment_layout_trans);
		ib_trans_close = (ImageView) v
				.findViewById(R.id.feed_fragment_btn_trans_close);
		ib_trans_close.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ll_trans.setVisibility(View.GONE);
			}
		});

		Button btn = (Button) v.findViewById(R.id.feed_fragment_btn_show_trans);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ll_trans.setVisibility(View.VISIBLE);
			}
		});

		for (int i = 0; i < strArr.length; i++) {
			mResoloverLang.put(strArr[i], langArr[i]);
			Lang1List.add(strArr[i]);
		}

		Spinner spinner = (Spinner) v.findViewById(R.id.feed_fragment_spinner1);

		// Create the ArrayAdapter
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
				getActivity(), android.R.layout.simple_spinner_item, Lang1List);

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

		spinner = (Spinner) v.findViewById(R.id.feed_fragment_spinner2);

		// Create the ArrayAdapter
		arrayAdapter = new ArrayAdapter<String>(getActivity(),
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
		return v;
	}

	private boolean isSetMyHouse() {
		if (PropertyManager.getInstance().getCheckin().equals("")
				|| PropertyManager.getInstance().getCheckin().length() < 5) {
			return false;
		}
		return true;
	}

	private String mText = new String();

	class MyAsyncTask extends AsyncTask<Void, Integer, Boolean> {
		@Override
		protected Boolean doInBackground(Void... arg0) {
			Translate.setClientId("allchange_guestbook");
			Translate
					.setClientSecret("rR3GUdisIb22vSsGZw2yvPgttp1NLpatRoZ+8WwXV2w=");
			try {
				mText = Translate.execute(et_write.getText().toString(),
						selected1, selected2);
				Log.e(TAG, "mText : " + mText);
			} catch (Exception e) {
				mText = e.toString();
			}
			return true;
		}
	}

	public void setFeedText(String text) {
		fee_text = text;
		if (!Dialog_Loading.getInstance().isOnScreen) {
			Dialog_Loading.getInstance().show(
					getActivity().getSupportFragmentManager(), TAG);
		}

		AndroidQuery.getInstance().requestWriteText(this,
				Url.SERVER_SHOUT_OUT_WRITE, text);
	}

	private int pagingSize = 20;

	private void setNewsFeedList(ArrayList<MessageData> arr) {

		// 서버에서 받아온 데이터의 길이가 20개보다 많거나 같으면 추가 아이템이 있으므로 가장 아래 Loadingbar를 붙여준다.
		if (arr.size() >= pagingSize) {
			MessageData loading = new MessageData();
			loading.email_id = LOADINGBAR;
			arr.add(loading);
		}
		// 어댑터에 집어넣기위한 data 객체를 생성한다.

		ArrayList<MessageData> data = newsFeedAdapter.getListData();
		Log.e(TAG, " data : " + data.size() + "   arr : " + arr.size());
		for (int i = 0; i < arr.size(); i++) {
			// 중복 메시지를 제거하기 위함
			if ((data.size() > 1)
					&& data.get(data.size() - 1).last_message
							.equals(arr.get(i).last_message)) {
			} else {
				data.add(arr.get(i));
			}

		}

		newsFeedAdapter.setListData(data);
		list_newsFeed.setAdapter(newsFeedAdapter);

		list_newsFeed.setSelection(positionForScroll);
		list_newsFeed.invalidate();
	}

	@Override
	public void writeTextCallback(String url, JSONObject json, AjaxStatus status) {
		if (json != null) {
			try {
				if (json.getString("result").equals("success")) {

					AndroidQuery.getInstance().requestGetFeed(this,
							Url.SERVER_SHOUT_OUT, null);
					et_write.setText("");
					imageBtn.setChecked(false);
					// 글을 본인이쓰면 리스트의 데이터를 초기화함
					newsFeedAdapter.clearListData();
				} else if (json.getString("result").equals("fail")) {
					Log.d(TAG, "---------------");
					Log.d(TAG, json.getString("result_msg"));
					Log.d(TAG, "---------------");
				}
			} catch (JSONException e) {
				Log.e(TAG, "---------------");
				Log.e(TAG, "parsing error");
				Log.e(TAG, "---------------");
				e.printStackTrace();
			}
		} else {
			Log.e(TAG, "---------------");
			Log.e(TAG, "json is null");
			Log.e(TAG, status.getMessage());
			Log.e(TAG, status.getRedirect());
			Log.e(TAG, "---------------");
		}
		if (Dialog_Loading.getInstance().isOnScreen) {
			Dialog_Loading.getInstance().dismiss();
		}

	}

	@Override
	public void getFeedCallback(String url, JSONObject json, AjaxStatus status) {

		String TAG = this.TAG + " getFeedCallback";
		if (json != null) {

			try {
				if (json.getString("result").equals("success")) {
					JSONArray jo = json.getJSONArray("shout");
					ArrayList<MessageData> arr = new ArrayList<MessageData>();
					for (int i = 0; i < jo.length(); i++) {
						MessageData data = new MessageData();
						data.email_id = jo.getJSONObject(i).getString(
								"email_id");
						data.firstname = jo.getJSONObject(i).getString(
								"firstname");
						data.lastname = jo.getJSONObject(i).getString(
								"lastname");
						data.last_message = jo.getJSONObject(i).getString(
								"last_message");
						data.last_message_dtime = jo.getJSONObject(i)
								.getString("last_message_dtime");
						data.profile_img_path = jo.getJSONObject(i).getString(
								"profile_img_path");
						arr.add(data);
					}

					setNewsFeedList(arr);

				} else if (json.getString("result").equals("fail")) {
					Log.d(TAG, "---------------");
					Log.d(TAG, json.getString("result_msg"));
					Log.d(TAG, "---------------");
				}
			} catch (JSONException e) {
				Log.e(TAG, "---------------");
				Log.e(TAG, "parsing error");
				Log.e(TAG, "---------------");
				e.printStackTrace();
			}
		} else {
			Log.e(TAG, "---------------");
			Log.e(TAG, "json is null");
			Log.e(TAG, status.getMessage());
			Log.e(TAG, status.getRedirect());
			Log.e(TAG, "---------------");
		}
		if (Dialog_Loading.getInstance().isOnScreen) {
			Dialog_Loading.getInstance().dismiss();
		}
	}

	int positionForScroll = 0;

	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		this.currentFirstVisibleItem = firstVisibleItem;
		this.currentVisibleItemCount = visibleItemCount;

		try {

			if (list_newsFeed.getLastVisiblePosition() == list_newsFeed
					.getAdapter().getCount() - 1
					&& list_newsFeed.getChildAt(
							list_newsFeed.getChildCount() - 1).getBottom() <= list_newsFeed
							.getHeight()) {
				// Toast.makeText(this, "end of scroll", Toast.LENGTH_SHORT)
				// .show();
				ArrayList<MessageData> data = newsFeedAdapter.getListData();

				positionForScroll = list_newsFeed.getFirstVisiblePosition();
				if (data.get(data.size() - 1).email_id.equals(LOADINGBAR)) {
					data.remove(data.size() - 1);
					AndroidQuery.getInstance().requestGetFeed(
							FeedFragment.this, Url.SERVER_SHOUT_OUT,
							data.get(data.size() - 1).last_message_dtime);
				}

			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void onScrollStateChanged(AbsListView view, int scrollState) {
		this.currentScrollState = scrollState;
	}

	/***
	 * NewsFeedListItemUpdate에서 refresh버튼을 눌렀을 때 새로고침하기
	 */
	@Override
	public void onRefreshFeed() {

		// 새로고침하기전에 초기화 시켜준다.
		newsFeedAdapter = new NewsFeedListAdapter(getActivity(),
				new ArrayList<MessageData>(), this);
		list_newsFeed.setAdapter(newsFeedAdapter);
		// 로딩화면 띄운다
		if (!Dialog_Loading.getInstance().isOnScreen) {
			Dialog_Loading.getInstance().show(
					getActivity().getSupportFragmentManager(), TAG);
		}
		// 요청한다.
		AndroidQuery.getInstance().requestGetFeed(FeedFragment.this,
				Url.SERVER_SHOUT_OUT, null);
	}

}
