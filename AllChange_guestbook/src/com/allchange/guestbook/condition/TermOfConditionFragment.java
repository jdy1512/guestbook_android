package com.allchange.guestbook.condition;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.allchange.guestbook.R;

public class TermOfConditionFragment extends Fragment {
	private static final String TAG = "TermOfConditionFragment";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.dialog_term_of_service, container,
				false);

		TextView tv = (TextView) v.findViewById(R.id.dialog_termOfServic_text);
		try {
			tv.setText(getTextTerm());
		} catch (IOException e) {
			e.printStackTrace();
		}

		Button btn = (Button) v.findViewById(R.id.dialog_termOfServic_btn);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Toast.makeText(getActivity(), "음...	", 0).show();
			}
		});
		return v;
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
