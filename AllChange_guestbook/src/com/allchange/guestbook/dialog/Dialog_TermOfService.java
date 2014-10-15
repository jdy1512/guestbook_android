package com.allchange.guestbook.dialog;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.app.Dialog;
import android.content.res.AssetManager;
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
import android.widget.TextView;

import com.allchange.guestbook.R;

public class Dialog_TermOfService extends DialogFragment {
	public static final String TAG = "Dialog_TermOfService";

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
		window.setGravity(Gravity.BOTTOM);
		window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		dialog.setContentView(R.layout.dialog_term_of_service);

		window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		dialog.setCanceledOnTouchOutside(false);
		TextView tv = (TextView) dialog
				.findViewById(R.id.dialog_termOfServic_text);

		try {
			tv.setText(getTextTerm());
		} catch (IOException e) {
			e.printStackTrace();
		}

		Button btn = (Button) dialog.findViewById(R.id.dialog_termOfServic_btn);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// 동의했을경우 넘어가는 로직추가
				dismiss();
			}
		});

		return dialog;
	}

	private String getTextTerm() throws IOException {
		AssetManager am = getActivity().getAssets();
		InputStream inputStream = am.open("Terms and Conditions.txt");
		// InputStream inputStream = getResources().openRawResource(R.raw.toc);
		// InputStream inputStream =
		// getResources().openRawResource(R.raw.internals);
		System.out.println(inputStream);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		int i;
		try {
			i = inputStream.read();
			while (i != -1) {
				byteArrayOutputStream.write(i);
				i = inputStream.read();
			}
			inputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return byteArrayOutputStream.toString();
	}
}