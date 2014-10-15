package com.allchange.guestbook.parser;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.os.AsyncTask;
import android.util.Log;

import com.allchange.guestbook.booking.search.map.ParsingData;
import com.allchange.guestbook.booking.search.map.RequestData;

public class ParserWebsite {

	// private ArrayList<ParsingData> mData = new ArrayList<ParsingData>();
	private HashMap<String, ParsingData> mData = new HashMap<String, ParsingData>();

	public static final String TAG = "ParserWebsite";

	private static volatile ParserWebsite instance = null;
	private static ProcessAirbnbTask airbnbTask = null;

	public static ParserWebsite getInstance() {
		if (instance == null) {
			instance = new ParserWebsite();

		}
		return instance;
	}

	public ParserWebsite() {

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
				url_airbnb = "http://www.kozaza.com/s/"
						+ getURLEncode(params[0].word) + "?bounds="
						+ params[0].sw_lat + "%2C" + params[0].sw_lng + "%2C"
						+ params[0].ne_lat + "%2C" + params[0].ne_lng
						+ "&page=" + params[0].page;
			} else {
				url_airbnb = "http://www.airbnb.co.kr/s/"
						+ getURLEncode(params[0].word) + "&page="
						+ params[0].page;
			}

			Log.e(TAG, "url : " + url_airbnb);
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
				resultCount = doc.select("div.num_result").text();

				Elements search_result = doc.select("div[class=result_items]");
				Elements links = search_result.select("div[class=item]");

				for (Element e : links) {

					ParsingData data = new ParsingData();
					Elements results = e.getAllElements().select("div.title");

					data.title = results.select("a").first().ownText();
					Log.e(TAG, "title : " + data.title);

					Elements address = e.getAllElements().select("div.address");
					Elements image = e.getAllElements().select("img.thumb");
					String original_image = image.attr("src");

					data.image_url = original_image;
					data.address = address.first().ownText();

					Log.e(TAG, "image_url : " + data.image_url);
					Log.e(TAG, "address : " + data.address);

					mData.put(data.title, data);
					total_result++;
				}
			} catch (NullPointerException e) {
				e.printStackTrace();
			} catch (Exception e3) {
				e3.printStackTrace();
			}
			resultCount = resultCount.split(" ")[0];
			Log.d(TAG, "url : " + url_airbnb + "   count : " + resultCount);
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
