package com.allchange.guestbook.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.allchange.guestbook.R;

public class Dialog_Loading extends DialogFragment {
	private static Dialog_Loading loading;
	public static final String TAG = "DialogEditTextFragment";
	Button btn;
	EditText editText;
	public boolean isOnScreen = false;
	private Context mContext;

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		mContext = activity;
	}

	public static Dialog_Loading getInstance() {
		if (loading == null) {
			loading = new Dialog_Loading();
		}
		return loading;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final Dialog dialog = new Dialog(getActivity());
		Window window = dialog.getWindow();
		window.requestFeature(Window.FEATURE_NO_TITLE);
		window.setGravity(Gravity.CENTER);
		window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		dialog.setContentView(R.layout.dialog_loading);

		window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		dialog.setCanceledOnTouchOutside(false);
		isOnScreen = true;
		// dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
		// @Override
		// public void onCancel(DialogInterface dialog) {
		// Toast.makeText(mContext, "asdf", 0).show();
		// ((Activity) mContext).finish();
		// }
		// });
		dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					((Activity) mContext).finish();
					dialog.cancel();
					return true;
				}
				return false;
			}
		});

		return dialog;
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		// TODO Auto-generated method stub
		isOnScreen = false;
		super.onDismiss(dialog);
	}
}