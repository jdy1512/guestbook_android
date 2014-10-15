package com.allchange.guestbook.booking.search.list;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnActionExpandListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;

import com.allchange.guestbook.R;
import com.allchange.guestbook.booking.search.map.ParsingData;
import com.allchange.guestbook.booking.search.map.RequestData;
import com.allchange.guestbook.parser.ParserMobileKozaza;
import com.allchange.guestbook.parser.ParserMobileKozaza.ParsingCallbackListener;

public class AccamodationSearchFragment extends Fragment implements
		ParsingCallbackListener {

	private static final String TAG = "BookingSiteFragment";

	private int currentFirstVisibleItem;
	private int currentVisibleItemCount;
	private int currentScrollState;
	public static final String LOADINGBAR = "loardingbar";
	int positionForScroll = 0;
	ListView listview;
	AccamodationSearchAdapter mAdapter;
	ParsingCallbackListener preListener;
	public SearchView mSearchView;
	public MenuItem searchItem;
	public static InputMethodManager imm;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		setHasOptionsMenu(true);
		getActivity().getActionBar().setTitle(
				getResources().getString(R.string.actionbar_title_kozaza));

		View v = inflater.inflate(R.layout.activity_list, container, false);

		listview = (ListView) v.findViewById(R.id.Sample_listView1);
		// ParserAirbnb.getInstance().clearWhenStop();
		preListener = ParserMobileKozaza.getInstance().getParsingListener();
		ParserMobileKozaza.getInstance().setParsingListener(this);
		listview.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				currentFirstVisibleItem = firstVisibleItem;
				currentVisibleItemCount = visibleItemCount;

				try {

					if (listview.getLastVisiblePosition() == listview
							.getAdapter().getCount() - 1
							&& listview
									.getChildAt(listview.getChildCount() - 1)
									.getBottom() <= listview.getHeight()) {

						Log.d(TAG, "end of scroll");

						positionForScroll = listview.getFirstVisiblePosition();
						if (maxPage > RequestData.getInstance().page
								&& isLoading) {

							// Log.e(TAG, "maxPage : " + maxPage + "   page : "
							// + RequestData.getInstance().page);
							RequestData.getInstance().page = RequestData
									.getInstance().page + 1;
							ParserMobileKozaza.getInstance().getList(
									RequestData.getInstance());
						}

					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				currentScrollState = scrollState;
			}
		});

		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ParsingData data = new ParsingData();
				data = mAdapter.getItem(position);
				ParserMobileKozaza.getInstance().getRoomInfo(data);
			}
		});

		// ParserAirbnb.getInstance().getList(RequestData.getInstance());
		// handler.postDelayed(new Runnable() {
		// @Override
		// public void run() {
		// ParserMobileKozaza.getInstance().getList(
		// RequestData.getInstance());
		// }
		// }, 500);

		return v;
	}

	@Override
	public void onDestroyView() {
		ParserMobileKozaza.getInstance().setParsingListener(preListener);
		RequestData.getInstance().page = 1;
		super.onDestroyView();
	}

	private boolean isLoading;
	private int maxPage;
	Handler handler = new Handler();

	@Override
	public void parsingCallback(HashMap<String, ParsingData> data,
			String resultCount) {
		if (resultCount == null || resultCount.equals("null")) {
			Toast.makeText(getActivity(),
					getResources().getString(R.string.no_data),
					Toast.LENGTH_SHORT).show();
			return;
		}
		ArrayList<ParsingData> arrData = new ArrayList<ParsingData>();

		if (RequestData.getInstance().page != 1) {
			ArrayList<ParsingData> backupData = mAdapter.getListData();
			for (String key : data.keySet()) {
				backupData.add(0, data.get(key));
				// mAdapter.insertListData(data.get(key));
			}
			mAdapter = new AccamodationSearchAdapter(getActivity(), backupData);
			listview.setAdapter(mAdapter);
		} else {
			maxPage = Integer.parseInt(resultCount) / 18;
			for (String key : data.keySet()) {
				arrData.add(data.get(key));
			}
			mAdapter = new AccamodationSearchAdapter(getActivity(), arrData);
			listview.setAdapter(mAdapter);
		}
		listview.setSelection(positionForScroll);
		listview.invalidate();
	}

	@SuppressLint("NewApi")
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.kozaza, menu);

		searchItem = menu.findItem(R.id.action_search);

		searchItem.setOnActionExpandListener(new OnActionExpandListener() {

			@Override
			public boolean onMenuItemActionExpand(MenuItem item) {
				// list_Result.setVisibility(View.GONE);
				// list_Search.setVisibility(View.VISIBLE);
				return true;
			}

			@Override
			public boolean onMenuItemActionCollapse(MenuItem item) {
				return true;
			}
		});

		mSearchView = (SearchView) searchItem.getActionView();
		mSearchView.setQueryHint(getResources().getString(
				R.string.search_accomdation_hint));
		mSearchView.setOnQueryTextListener(queryTextListener);
		SearchManager searchManager = (SearchManager) getActivity()
				.getSystemService(Context.SEARCH_SERVICE);
		if (null != searchManager) {
			mSearchView.setSearchableInfo(searchManager
					.getSearchableInfo(getActivity().getComponentName()));
		}
		mSearchView.setIconifiedByDefault(true);
		super.onCreateOptionsMenu(menu, inflater);
	}

	private abstract class QueryListenerFilterable implements
			OnQueryTextListener {
	}

	@SuppressLint("NewApi")
	private QueryListenerFilterable queryTextListener = new QueryListenerFilterable() {
		@Override
		public boolean onQueryTextSubmit(String query) {
			// Log.e("TAG", query);

			RequestData data = new RequestData();
			// searchQuery = query;
			data.word = query;
			data.byMap = false;

			// indicator.setVisibility(View.VISIBLE);
			ParserMobileKozaza.getInstance().getList(data);
			// Dialog_Loading.getInstance().show(getFragmentManager(), TAG);

			// addHistoryItem(query);
			imm = (InputMethodManager) getActivity().getSystemService(
					Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(mSearchView.getWindowToken(), 0);
			mSearchView.setQuery("", false);
			mSearchView.setIconified(true);
			searchItem.collapseActionView();
			// mSearchView.setLayoutParams(new ActionBar.LayoutParams(
			// Gravity.RIGHT));
			return false;
		}

		@Override
		public boolean onQueryTextChange(String newText) {

			return false;
		}

	};

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

	@Override
	public void kozazaDetailCallback(ParsingData data) {
		if (mListener != null) {
			mListener.onSetAccamodation(data);
		}
	}

	OnSetAccamoListener mListener;

	public void setOnSetAccamoListener(OnSetAccamoListener listener) {
		mListener = listener;
	}
}
