package com.allchange.guestbook.parser;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.os.AsyncTask;
import android.util.Log;

import com.allchange.guestbook.booking.search.map.ParsingData;
import com.allchange.guestbook.booking.search.map.RequestData;

public class ParserAirbnb {

	// private ArrayList<ParsingData> mData = new ArrayList<ParsingData>();
	private HashMap<String, ParsingData> mData = new HashMap<String, ParsingData>();

	public static final String TAG = "ParserAirbnb";

	private static volatile ParserAirbnb instance = null;
	private static ProcessAirbnbTask airbnbTask = null;

	public static ParserAirbnb getInstance() {
		if (instance == null) {
			instance = new ParserAirbnb();

		}
		return instance;
	}

	public ParserAirbnb() {

	}

	public void printLi() {
		Log.e(TAG, "printLi : " + mListener);
	}

	public void clearWhenStop() {
		cancelParsing();
		airbnbTask = null;
	}

	private volatile ParsingCallbackListener mListener;

	public interface ParsingCallbackListener {
		public void parsingCallback(HashMap<String, ParsingData> data,
				String resultCount);
	}

	public ParsingCallbackListener getParsingListener() {
		return mListener;
	}

	public void setParsingListener(ParsingCallbackListener listener) {
		mListener = listener;
	}

	public void cancelParsing() {
		try {
			if (airbnbTask.getStatus().equals(AsyncTask.Status.RUNNING)) {
				airbnbTask.cancel(true);
			}
		} catch (NullPointerException e) {
			Log.e(TAG, "NullPointerException cancelParsing");
		}

	}

	public void getList(RequestData data) {
		if (mListener == null) {
			return;
		}
		// mData.remov
		if (airbnbTask == null) {
			airbnbTask = new ProcessAirbnbTask();
		} else if (airbnbTask.getStatus().equals(AsyncTask.Status.RUNNING)) {
			airbnbTask.cancel(true);
		}
		airbnbTask = new ProcessAirbnbTask();
		airbnbTask.execute(data);
	}

	public HashMap<String, ParsingData> getParsingData() {
		return mData;
	}

	// AsyncTask<Params,Progress,Result>
	private class ProcessAirbnbTask extends
			AsyncTask<RequestData, Boolean, String> {
		Connection connection;

		@Override
		protected String doInBackground(RequestData... params) {
			String resultCount = null;
			System.out.println(params[0].word + " start");
			int total_result = 0;
			String url_airbnb = "";
			if (params[0].byMap) {
				url_airbnb = "https://www.airbnb.com/s/"
						+ getURLEncode(params[0].word) + "?sw_lat="
						+ params[0].sw_lat + "&sw_lng=" + params[0].sw_lng
						+ "&ne_lat=" + params[0].ne_lat + "&ne_lng="
						+ params[0].ne_lng + "&zoom=" + params[0].zoom
						+ "&search_by_map=true&page=" + params[0].page;
			} else {
				url_airbnb = "https://www.airbnb.co.kr/s/"
						+ getURLEncode(params[0].word) + "?page="
						+ params[0].page;
			}
			Document doc = null;

			try {

				connection = Jsoup
						.connect(url_airbnb)
						.userAgent(
								"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.116 Safari/537.36");
				connection.timeout(5000); // timeout in millis
				doc = connection.get();
			} catch (IOException e) {

				if (e.getMessage().toString().trim().equals(("Read timed out"))) {
					System.out.printf("(Read timed out)try again...");
					// return getMaxPage(url);
				} else if (e.getMessage().toString().trim()
						.equals(("connect timed out"))) {
					System.out.printf("(connect timed out)try again...");
					// return getMaxPage(url);
				} else {
					e.printStackTrace();
				}
				// return result;
			}

			try {
				// Log.d(TAG, doc.html());

				Elements result_count = doc.select(".map-search ");
				JSONObject jsonObj = new JSONObject(
						result_count.attr("data-bootstrap-data"));
				resultCount = jsonObj.getString("visible_results_count");

				Elements search_result = doc
						.select("div[class=col-6 row-space-1]");

				for (Element e : search_result) {

					ParsingData data = new ParsingData();
					Elements results = e.getAllElements().select("div.listing");

					data.title = results.attr("data-name");
					data.latitude = results.attr("data-lat");
					data.longitude = results.attr("data-lng");

					Element imageElement = results
							.select("div[class=listing-img-container media-cover text-center]")
							.select("img.img-responsive-height").first();

					Element addressElement = results
							.select("div[class=panel-overlay-top-right wl-social-connection-panel]")
							.first().children().select("span").first();
					String original_image = null;

					try {
						original_image = imageElement.attr("data-urls");
					} catch (NullPointerException e2) {
					}

					String[] imageParts = original_image.split("/");
					try {
						data.image_url = "https://a0.muscache.com/pictures/"
								+ imageParts[5] + "/small.jpg";
					} catch (Exception e2) {
					}

					data.address = addressElement.attr("data-address");
					mData.put(data.title, data);
					total_result++;
				}
			} catch (NullPointerException e) {
				e.printStackTrace();
			} catch (Exception e3) {
				e3.printStackTrace();
			}

			Log.d(TAG, "url : " + url_airbnb + "   count : " + total_result);
			return resultCount;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			if (mListener != null) {
				mListener.parsingCallback(mData, result);
				mData.clear();
			} else {
				Log.e(TAG, "parsingCallback ERROR!");
			}
			super.onPostExecute(result);
		}
	}

	private String getURLEncode(String content) {
		if (content == null || content.equals("")) {
			return "-";
		}
		try {
			return URLEncoder.encode(content, "utf-8"); // UTF-8
			// return URLEncoder.encode(content, "euc-kr"); // EUC-KR
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

}
