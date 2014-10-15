package com.allchange.guestbook.config;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;

import com.allchange.guestbook.R;
import com.allchange.guestbook.property.PropertyManager;

public class ConfigurationFragment extends Fragment {
	private static final String TAG = "ConfigurationFragment";

	ToggleButton btn_noti;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.fragment_configuration, container,
				false);

		btn_noti = (ToggleButton) v.findViewById(R.id.settings_toggle_gcm);
		btn_noti.setChecked(PropertyManager.getInstance().getGCMSetting());
		btn_noti.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				PropertyManager.getInstance().setGCMSetting(
						btn_noti.isChecked());
			}
		});

		return v;
	}

}
