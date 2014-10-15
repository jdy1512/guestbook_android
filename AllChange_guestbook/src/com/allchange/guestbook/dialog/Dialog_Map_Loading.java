package com.allchange.guestbook.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.allchange.guestbook.R;

public class Dialog_Map_Loading extends DialogFragment {
	private static Dialog_Map_Loading loading;
	public static final String TAG = "Dialog_Map_Loading";
	public static final boolean STATUS_RUNNING = true;
	public static final boolean STATUS_FINISHED = false;
	Button btn;
	EditText editText;
	private Context mContext;
	private boolean runningFlag = false;

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		mContext = activity;
	}

	public static Dialog_Map_Loading getInstance() {
		if (loading == null) {
			loading = new Dialog_Map_Loading();
		}
		return loading;
	}

	public boolean getStatus() {
		if (runningFlag) {
			return STATUS_RUNNING;
		}
		return STATUS_FINISHED;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final Dialog dialog = new Dialog(getActivity());

		Window window = dialog.getWindow();
		window.requestFeature(Window.FEATURE_NO_TITLE);
		window.setGravity(Gravity.CENTER);
		// window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);

		dialog.setContentView(R.layout.dialog_map_loading);

		window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		// window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		// dialog.setCanceledOnTouchOutside(false);
		return dialog;
	}

	@Override
	public void show(FragmentManager manager, String tag) {
		// TODO Auto-generated method stub
		runningFlag = true;
		super.show(manager, tag);
	}

	@Override
	public void dismiss() {
		// TODO Auto-generated method stub
		runningFlag = false;
		super.dismiss();
	}
}