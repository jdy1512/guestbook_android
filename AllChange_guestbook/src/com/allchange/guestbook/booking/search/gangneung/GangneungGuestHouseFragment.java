package com.allchange.guestbook.booking.search.gangneung;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.allchange.guestbook.R;
import com.allchange.guestbook.booking.search.list.OnSetAccamoListener;
import com.allchange.guestbook.booking.search.map.ParsingData;

public class GangneungGuestHouseFragment extends Fragment {

	private static final String TAG = "BookingSiteFragment";

	ListView listview;
	GangneungGuestHouseAdapter mAdapter;

	int[] imgArr = { R.drawable.gang1, R.drawable.gang2, R.drawable.gang3 };
	String[] logiArr = { "128.8857874", "128.9504524", "128.8753887" };
	String[] latiArr = { "37.8080923", "37.7706513", "37.8371362" };
	int[] nameArr = { R.string.gangneung_guesthous1,
			R.string.gangneung_guesthous2, R.string.gangneung_guesthous3 };
	int[] addArr = { R.string.gangneung_guesthous_add1,
			R.string.gangneung_guesthous_add2,
			R.string.gangneung_guesthous_add3 };

	// Gangneung Guesthouse 강릉게스트하우스 1호점 , 강원도 강릉시 안현동 403-3번지 , 128.8857874
	// 37.8080923
	//
	// Gangneung Guesthouse (Coffe street) / 강릉게스트하우스 2호점 /강릉게스트하우스 커피거리점 , 강릉시
	// 견소동 9-1번지 , 128.9504524 37.7706513"
	//
	// Gangneung Guesthouse 강릉게스트하우스 3호점 , 강원 강릉시 사천면 사천진리 86-39 번지 ,
	// 128.8753887 37.8371362

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		setHasOptionsMenu(true);
		getActivity().getActionBar().setTitle(
				getResources().getString(
						R.string.actionbar_title_gangneung_guesthouse));

		View v = inflater.inflate(R.layout.activity_list, container, false);

		ArrayList<ParsingData> arrData = new ArrayList<ParsingData>();
		for (int i = 0; i < 3; i++) {
			ParsingData data = new ParsingData();
			data.image_url = "" + imgArr[i];
			data.title = getResources().getString(nameArr[i]);
			data.address = getResources().getString(addArr[i]);
			data.latitude = latiArr[i];
			data.longitude = logiArr[i];
			data.gh_id = "";
			arrData.add(data);
		}
		listview = (ListView) v.findViewById(R.id.Sample_listView1);

		mAdapter = new GangneungGuestHouseAdapter(getActivity(), arrData);
		listview.setAdapter(mAdapter);

		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ParsingData data = new ParsingData();
				data = mAdapter.getItem(position);
				mListener.onSetAccamodation(data);
			}
		});

		return v;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// Respond to the action bar's Up/Home button
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(getActivity());
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	OnSetAccamoListener mListener;

	public void setOnSetAccamoListener(OnSetAccamoListener listener) {
		mListener = listener;
	}
}
