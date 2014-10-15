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

public class ParserMobileKozaza {

	// private ArrayList<ParsingData> mData = new ArrayList<ParsingData>();
	private HashMap<String, ParsingData> mData = new HashMap<String, ParsingData>();

	public static final String TAG = "ParserMobileKozaza";

	private static volatile ParserMobileKozaza instance = null;
	private static ProcessKozazaTask kozazaTask = null;

	public static ParserMobileKozaza getInstance() {
		if (instance == null) {
			instance = new ParserMobileKozaza();

		}
		return instance;
	}

	public ParserMobileKozaza() {

	}

	public void clearWhenStop() {
		cancelParsing();
		kozazaTask = null;
	}

	private volatile ParsingCallbackListener mListener;

	public interface ParsingCallbackListener {
		public void parsingCallback(HashMap<String, ParsingData> data,
				String resultCount);

		public void kozazaDetailCallback(ParsingData data);
	}

	public ParsingCallbackListener getParsingListener() {
		return mListener;
	}

	public void setParsingListener(ParsingCallbackListener listener) {
		mListener = listener;
	}

	public void cancelParsing() {
		try {
			if (kozazaTask.getStatus().equals(AsyncTask.Status.RUNNING)) {
				kozazaTask.cancel(true);
			}
		} catch (NullPointerException e) {
			Log.e(TAG, "NullPointerException cancelParsing");
		}

	}

	public void getRoomInfo(ParsingData data) {
		ProcessKozazaRoomInfo pkri = new ProcessKozazaRoomInfo();
		pkri.execute(data);
	}

	public void getList(RequestData data) {
		if (mListener == null) {
			return;
		}
		// mData.remov
		if (kozazaTask == null) {
			kozazaTask = new ProcessKozazaTask();
		} else if (kozazaTask.getStatus().equals(AsyncTask.Status.RUNNING)) {
			kozazaTask.cancel(true);
		}
		kozazaTask = new ProcessKozazaTask();
		kozazaTask.execute(data);
	}

	public HashMap<String, ParsingData> getParsingData() {
		return mData;
	}

	// AsyncTask<Params,Progress,Result>
	private class ProcessKozazaTask extends
			AsyncTask<RequestData, Boolean, String> {
		Connection connection;

		@Override
		protected String doInBackground(RequestData... params) {
			String resultCount = null;
			System.out.println(params[0].word + " start");
			int total_result = 0;
			String url_airbnb = "";

			if (params[0].byMap) {
				url_airbnb = "http://www.kozaza.com/search?location="
						+ getURLEncode(params[0].word);
			} else {
				url_airbnb = "http://www.kozaza.com/search?location="
						+ getURLEncode(params[0].word);
			}
			Log.e(TAG, "url : " + url_airbnb);
			Document doc = null;

			try {

				connection = Jsoup
						.connect(url_airbnb)
						.userAgent(
								"Mozilla/5.0 (Linux; U; Android 4.0.3; ko-kr; LG-L160L Build/IML74K) AppleWebkit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30");
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
				resultCount = doc.select("span#num_list").first().ownText();

				Elements search_result = doc.select("ul.lists");
				Elements listSize = doc.select("div.desc");

				for (Element e : listSize) {

					ParsingData data = new ParsingData();
					Elements results = e.getAllElements().select(
							"div.list_title");

					data.title = results.first().ownText();
					Log.e(TAG, "title : " + data.title);

					Elements address = e.getAllElements().select("div.address");
					Elements image = search_result.select("a.thumb[href="
							+ e.getAllElements().select("a.list").attr("href")
							+ "]");
					Log.e(TAG, "image : " + image.toString());

					String original_image = image.select("img").attr("src");

					data.image_url = original_image;
					data.address = address.first().ownText();
					data.gh_id = data.image_url.split("/")[4];

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
			// resultCount = resultCount.split(" ")[0];
			Log.d(TAG, "url : " + url_airbnb + "   count : " + resultCount
					+ ", " + total_result);
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

	private class ProcessKozazaRoomInfo extends
			AsyncTask<ParsingData, ParsingData, ParsingData> {
		Connection connection;

		@Override
		protected ParsingData doInBackground(ParsingData... params) {
			Connection connection;

			ParsingData data = params[0];

			String url = "http://www.kozaza.com/rooms/"
					+ getURLEncode(data.gh_id);

			Log.e(TAG, "url : " + url);
			Document doc = null;

			try {

				connection = Jsoup
						.connect(url)
						.userAgent(
								"Mozilla/5.0 (Linux; U; Android 4.0.3; ko-kr; LG-L160L Build/IML74K) AppleWebkit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30");
				connection.timeout(5000); // timeout in millis
				doc = connection.get();
			} catch (IOException e) {

				if (e.getMessage().toString().trim().equals(("Read timed out"))) {
					System.out.printf("(Read timed out)try again...");
				} else if (e.getMessage().toString().trim()
						.equals(("connect timed out"))) {
					System.out.printf("(connect timed out)try again...");
				} else {
					e.printStackTrace();
				}
			}

			Log.e(TAG, "" + doc.text());
			Elements imgmap = doc.select("div#imgmap");
			data.latitude = imgmap.first().attr("data-lat");
			data.longitude = imgmap.first().attr("data-lng");

			return data;

		}

		@Override
		protected void onPostExecute(ParsingData result) {
			if (mListener != null) {
				mListener.kozazaDetailCallback(result);
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
