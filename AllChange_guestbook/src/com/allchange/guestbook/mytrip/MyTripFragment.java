package com.allchange.guestbook.mytrip;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.allchange.guestbook.R;
import com.allchange.guestbook.property.MyApplication;

public class MyTripFragment extends Fragment {
	private static final String TAG = "MyTripFragment";

	ListView listMyTrip;
	MyTripListAdapter mAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.fragment_mytrip, container, false);

		listMyTrip = (ListView) v.findViewById(R.id.mytrip_listView);
		return v;
	}

}
