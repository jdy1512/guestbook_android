package com.allchange.guestbook.main.profiles;

import android.os.Parcel;
import android.os.Parcelable;

public class ChattingRoomPeopleData implements Parcelable {
	public String profile_img_path;
	public String email_id;
	public String firstname;
	public String lastname;
	public String gb_for;
	public String gb_with;
	public String gb_seeking;
	public double gb_latitude;
	public double gb_longitude;

	public ChattingRoomPeopleData() {
		profile_img_path = "";
		email_id = "";
		firstname = "";
		lastname = "";
		gb_for = "";
		gb_with = "";
		gb_seeking = "";
		gb_latitude = 0.0;
		gb_longitude = 0.0;
	}

	public ChattingRoomPeopleData(Parcel in) {
		profile_img_path = in.readString();
		email_id = in.readString();
		firstname = in.readString();
		lastname = in.readString();
		gb_for = in.readString();
		gb_with = in.readString();
		gb_seeking = in.readString();
		gb_latitude = in.readDouble();
		gb_longitude = in.readDouble();
	}

	public String getGb_for() {
		return gb_for;
	}

	public void setGb_for(String gb_for) {
		this.gb_for = gb_for;
	}

	public String getGb_with() {
		return gb_with;
	}

	public void setGb_with(String gb_with) {
		this.gb_with = gb_with;
	}

	public String getGb_seeking() {
		return gb_seeking;
	}

	public void setGb_seeking(String gb_seeking) {
		this.gb_seeking = gb_seeking;
	}

	public double getGb_latitude() {
		return gb_latitude;
	}

	public void setGb_latitude(double gb_latitude) {
		this.gb_latitude = gb_latitude;
	}

	public double getGb_longitude() {
		return gb_longitude;
	}

	public void setGb_longitude(double gb_longitude) {
		this.gb_longitude = gb_longitude;
	}

	public String getProfile_img_path() {
		return profile_img_path;
	}

	public void setProfile_img_path(String profile_img_path) {
		this.profile_img_path = profile_img_path;
	}

	public String getEmail_id() {
		return email_id;
	}

	public void setEmail_id(String email_id) {
		this.email_id = email_id;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(profile_img_path);
		dest.writeString(email_id);
		dest.writeString(firstname);
		dest.writeString(lastname);
		dest.writeString(gb_for);
		dest.writeString(gb_with);
		dest.writeString(gb_seeking);
		dest.writeDouble(gb_latitude);
		dest.writeDouble(gb_longitude);

	}

	public static final Parcelable.Creator<ChattingRoomPeopleData> CREATOR = new Parcelable.Creator<ChattingRoomPeopleData>() {
		public ChattingRoomPeopleData createFromParcel(Parcel in) {
			return new ChattingRoomPeopleData(in);
		}

		public ChattingRoomPeopleData[] newArray(int size) {
			return new ChattingRoomPeopleData[size];
		}
	};
}
