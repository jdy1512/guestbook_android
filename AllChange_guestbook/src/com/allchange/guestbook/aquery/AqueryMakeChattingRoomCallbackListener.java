package com.allchange.guestbook.aquery;

import java.util.ArrayList;

import org.json.JSONObject;

import com.allchange.guestbook.main.profiles.ChattingRoomPeopleData;
import com.androidquery.callback.AjaxStatus;

public interface AqueryMakeChattingRoomCallbackListener {
	public void AqueryMakeChattingRoomCallback(String url,
			JSONObject json, AjaxStatus status, ArrayList<ChattingRoomPeopleData> member);
}
