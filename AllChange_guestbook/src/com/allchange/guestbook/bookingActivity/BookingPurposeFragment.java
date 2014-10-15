package com.allchange.guestbook.bookingActivity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ToggleButton;

import com.allchange.guestbook.R;
import com.allchange.guestbook.booking.search.map.ParsingData;

public class BookingPurposeFragment extends Fragment implements OnClickListener {

	public static final String TAG = "BookingPurposeFragment";

	ToggleButton btn_vacation, btn_business, btn_convention, btn_visitRelatvie,
			btn_individual, btn_couple, btn_family, btn_group, btn_attractions,
			btn_local, btn_entertainment, btn_love;

	String[] stringArr = { "vacation", "business", "convention",
			"visit_relative", "individual", "couple", "family", "group",
			"attraction", "local_fb", "entertainment", "love" };
	ToggleButton[] btnArray = { btn_vacation, btn_business, btn_convention,
			btn_visitRelatvie, btn_individual, btn_couple, btn_family,
			btn_group, btn_attractions, btn_local, btn_entertainment, btn_love };
	String[] sentenceArr = new String[3];

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.booking_fragment_purpose, container,
				false);

		btn_vacation = (ToggleButton) v
				.findViewById(R.id.booking_purpose_btn_vacation);
		btn_business = (ToggleButton) v
				.findViewById(R.id.booking_purpose_btn_business);
		btn_convention = (ToggleButton) v
				.findViewById(R.id.booking_purpose_btn_convention);
		btn_visitRelatvie = (ToggleButton) v
				.findViewById(R.id.booking_purpose_btn_visitRelatvie);
		btn_individual = (ToggleButton) v
				.findViewById(R.id.booking_purpose_btn_individual);
		btn_couple = (ToggleButton) v
				.findViewById(R.id.booking_purpose_btn_couple);
		btn_family = (ToggleButton) v
				.findViewById(R.id.booking_purpose_btn_family);
		btn_group = (ToggleButton) v
				.findViewById(R.id.booking_purpose_btn_group);
		btn_attractions = (ToggleButton) v
				.findViewById(R.id.booking_purpose_btn_attractions);
		btn_local = (ToggleButton) v
				.findViewById(R.id.booking_purpose_btn_local);
		btn_entertainment = (ToggleButton) v
				.findViewById(R.id.booking_purpose_btn_entertainment);
		btn_love = (ToggleButton) v.findViewById(R.id.booking_purpose_btn_love);

		btn_vacation.setOnClickListener(this);
		btn_business.setOnClickListener(this);
		btn_convention.setOnClickListener(this);
		btn_visitRelatvie.setOnClickListener(this);
		btn_individual.setOnClickListener(this);
		btn_couple.setOnClickListener(this);
		btn_family.setOnClickListener(this);
		btn_group.setOnClickListener(this);
		btn_attractions.setOnClickListener(this);
		btn_local.setOnClickListener(this);
		btn_entertainment.setOnClickListener(this);
		btn_love.setOnClickListener(this);
		return v;
	}

	@Override
	public void onClick(View v) {

		// radio button group 처럼 만들기위한 각 열의 버튼을 check를 false로 셋팅
		if (v.getId() == R.id.booking_purpose_btn_vacation
				|| v.getId() == R.id.booking_purpose_btn_business
				|| v.getId() == R.id.booking_purpose_btn_convention
				|| v.getId() == R.id.booking_purpose_btn_visitRelatvie) {
			btn_vacation.setChecked(false);
			btn_business.setChecked(false);
			btn_convention.setChecked(false);
			btn_visitRelatvie.setChecked(false);
		} else if (v.getId() == R.id.booking_purpose_btn_individual
				|| v.getId() == R.id.booking_purpose_btn_couple
				|| v.getId() == R.id.booking_purpose_btn_family
				|| v.getId() == R.id.booking_purpose_btn_group) {
			btn_individual.setChecked(false);
			btn_couple.setChecked(false);
			btn_family.setChecked(false);
			btn_group.setChecked(false);
		} else {
			btn_attractions.setChecked(false);
			btn_local.setChecked(false);
			btn_entertainment.setChecked(false);
			btn_love.setChecked(false);
		}

		// 눌려진 버튼 하나만 활성
		switch (v.getId()) {
		// 첫째줄
		case R.id.booking_purpose_btn_vacation:
			btn_vacation.setChecked(true);
			sentenceArr[0] = stringArr[0];
			break;
		case R.id.booking_purpose_btn_business:
			btn_business.setChecked(true);
			sentenceArr[0] = stringArr[1];
			break;
		case R.id.booking_purpose_btn_convention:
			btn_convention.setChecked(true);
			sentenceArr[0] = stringArr[2];
			break;
		case R.id.booking_purpose_btn_visitRelatvie:
			btn_visitRelatvie.setChecked(true);
			sentenceArr[0] = stringArr[3];
			break;
		// 둘째줄
		case R.id.booking_purpose_btn_individual:
			btn_individual.setChecked(true);
			sentenceArr[1] = stringArr[4];
			break;
		case R.id.booking_purpose_btn_couple:
			btn_couple.setChecked(true);
			sentenceArr[1] = stringArr[5];
			break;
		case R.id.booking_purpose_btn_family:
			btn_family.setChecked(true);
			sentenceArr[1] = stringArr[6];
			break;
		case R.id.booking_purpose_btn_group:
			btn_group.setChecked(true);
			sentenceArr[1] = stringArr[7];
			break;
		// 셋째줄
		case R.id.booking_purpose_btn_attractions:
			btn_attractions.setChecked(true);
			sentenceArr[2] = stringArr[8];
			break;
		case R.id.booking_purpose_btn_local:
			btn_local.setChecked(true);
			sentenceArr[2] = stringArr[9];
			break;
		case R.id.booking_purpose_btn_entertainment:
			btn_entertainment.setChecked(true);
			sentenceArr[2] = stringArr[10];
			break;
		case R.id.booking_purpose_btn_love:
			btn_love.setChecked(true);
			sentenceArr[2] = stringArr[11];
			break;

		default:
			break;
		}

		// 각 열의 버튼 하나씩 선택되었으면 다음 단계로 보내기
		if ((btn_vacation.isChecked() || btn_business.isChecked()
				|| btn_convention.isChecked() || btn_visitRelatvie.isChecked())
				&& (btn_individual.isChecked() || btn_couple.isChecked()
						|| btn_family.isChecked() || btn_group.isChecked())
				&& (btn_attractions.isChecked() || btn_local.isChecked()
						|| btn_entertainment.isChecked() || btn_love
							.isChecked())) {
			if (mListener != null) {
				// mData에 visit string 채우고 보낸다.
				mData.gb_visit = sentenceArr;
				mListener.onSetPurpose(mData);
			}
		}
	}

	public interface OnSetPurposeListener {
		public void onSetPurpose(ParsingData data);
	}

	OnSetPurposeListener mListener;
	ParsingData mData;

	public void setOnSetPurposeListener(OnSetPurposeListener listener,
			ParsingData data) {
		mListener = listener;
		mData = data;
	}
}
