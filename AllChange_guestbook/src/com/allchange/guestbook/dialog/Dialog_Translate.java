package com.allchange.guestbook.dialog;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.allchange.guestbook.R;
import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;

public class Dialog_Translate extends DialogFragment {
	public static final String TAG = "Dialog_Translate";
	private Context mContext;

	private String mText;

	ArrayList<String> Lang1List = new ArrayList<String>();
	ArrayList<String> Lang2List = new ArrayList<String>();
	TextView txtView;
	Language[] langArr = { Language.AUTO_DETECT, Language.KOREAN,
			Language.ENGLISH, Language.JAPANESE, Language.CHINESE_TRADITIONAL };
	String[] strArr = { "자동감지", "한국어", "English", "日本語", "中國語" };
	HashMap<String, Language> mResoloverLang = new HashMap<String, Language>();
	Language selected1, selected2;

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		mContext = activity;
	}

	public void setTextForTrans(String text) {
		mText = text;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final Dialog dialog = new Dialog(getActivity());

		Window window = dialog.getWindow();
		window.requestFeature(Window.FEATURE_NO_TITLE);
		window.setGravity(Gravity.CENTER);
		window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		dialog.setContentView(R.layout.dialog_translate);

		window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		dialog.setCanceledOnTouchOutside(false);

		txtView = (TextView) dialog.findViewById(R.id.dialog_translate_tv);
		txtView.setText(mText);

		// 전송하기
		Button btn = (Button) dialog.findViewById(R.id.dialog_translate_btn);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				FeedFragment fa = (FeedFragment) getActivity();
//				fa.setFeedText(mText);
//				dismiss();
			}
		});

		// 번역하기
		btn = (Button) dialog.findViewById(R.id.dialog_translate_btn_trans);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if (selected1 != null && selected2 != null) {
					Log.e(TAG, "selected1 : " + selected1.toString());
					Log.e(TAG, "selected2 : " + selected2.toString());
					new MyAsyncTask() {
						protected void onPostExecute(Boolean result) {
							txtView.setText(mText);
						}
					}.execute();
				}

			}
		});

		for (int i = 0; i < strArr.length; i++) {
			mResoloverLang.put(strArr[i], langArr[i]);
			Lang1List.add(strArr[i]);
		}

		Spinner spinner = (Spinner) dialog
				.findViewById(R.id.dialog_translate_spinner1);

		// Create the ArrayAdapter
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(mContext,
				android.R.layout.simple_spinner_item, Lang1List);

		// Set the Adapter
		spinner.setAdapter(arrayAdapter);

		// Set the ClickListener for Spinner
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> adapterView, View view,
					int i, long l) {
				// Toast.makeText(
				// mContext,
				// "You Selected : " + difficultyLevelOptionsList.get(i)
				// + " Level ", Toast.LENGTH_SHORT).show();
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

		spinner = (Spinner) dialog.findViewById(R.id.dialog_translate_spinner2);

		// Create the ArrayAdapter
		arrayAdapter = new ArrayAdapter<String>(mContext,
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

		return dialog;

	}

	class MyAsyncTask extends AsyncTask<Void, Integer, Boolean> {
		@Override
		protected Boolean doInBackground(Void... arg0) {
			Translate.setClientId("allchange_guestbook");
			Translate
					.setClientSecret("rR3GUdisIb22vSsGZw2yvPgttp1NLpatRoZ+8WwXV2w=");
			try {
				Log.e(TAG, "mText : " + mText);
				mText = Translate.execute(mText, selected1, selected2);
				Log.e(TAG, "mText : " + mText);
			} catch (Exception e) {
				mText = e.toString();
			}
			return true;
		}
	}
}