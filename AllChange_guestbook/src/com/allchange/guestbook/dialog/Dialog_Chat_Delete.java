package com.allchange.guestbook.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.allchange.guestbook.R;

public class Dialog_Chat_Delete extends DialogFragment {
	public static final String TAG = "Dialog_Chat_Delete";

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final Dialog dialog = new Dialog(getActivity());

		Window window = dialog.getWindow();
		window.requestFeature(Window.FEATURE_NO_TITLE);
		window.setGravity(Gravity.CENTER);
		window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		dialog.setContentView(R.layout.dialog_chat_delete);

		window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		dialog.setCanceledOnTouchOutside(false);

		Button btn = (Button) dialog
				.findViewById(R.id.dialog_chat_delete_cancel);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// 다이얼로그 닫기
				dismiss();
			}
		});
		btn = (Button) dialog.findViewById(R.id.dialog_chat_delete);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// 삭제하기
				if (mListener != null) {
					mListener.onDelete();
				}
				dismiss();
			}
		});
		return dialog;
	}

	public interface OnDeleteListener {
		public void onDelete();

	}

	OnDeleteListener mListener;

	public void setOnDeleteListener(OnDeleteListener listener) {
		mListener = listener;
	}
}