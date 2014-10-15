package com.allchange.guestbook.calendar;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.allchange.guestbook.R;

public class CalendarItemView extends LinearLayout {

	TextView numberView;
	TextView contentView;
	CalendarItem mItem;
	// public final static int NUMBER_COLOR = Color.WHITE;
	public final static int NUMBER_COLOR = 0xff2f3e4a;
	public final static int SAT_COLOR = 0xff2f3e4a;
	public final static int SUN_COLOR = 0xff2f3e4a;
	public final static float IN_MONTH_TEXT_SIZE_SP = 15.0f;

	public final static int OUT_MONTH_TEXT_COLOR = 0xffc5c5c5;
	public final static int OCCUPIED_DAY_COLOR = Color.WHITE;
	public final static float OUT_MONTH_TEXT_SIZE_SP = 15.0f;

	public final static int UNOCCUPIED_BACK_COLOR = 0xffeeeeee;
	public final static int RESERVED_BACK_COLOR = 0x332f3e4a;

	public CalendarItemView(Context context) {
		super(context);
		LayoutInflater.from(context).inflate(R.layout.calendar_item, this);
		numberView = (TextView) findViewById(R.id.number);
		// contentView = (TextView) findViewById(R.id.content);
		// this.setBackground(getResources().getDrawable(R.drawable.textfiled_img));

	}

	public void setData(CalendarItem item) {
		mItem = item;
		float textsize = IN_MONTH_TEXT_SIZE_SP;
		int textColor = NUMBER_COLOR;
		// if (!item.inMonth) {
		// textsize = OUT_MONTH_TEXT_SIZE_SP;
		// textColor = OUT_MONTH_TEXT_COLOR;
		// } else {
		// textsize = IN_MONTH_TEXT_SIZE_SP;
		// switch (item.dayOfWeek) {
		// case Calendar.SUNDAY:
		// textColor = SUN_COLOR;
		// break;
		// case Calendar.SATURDAY:
		// textColor = SAT_COLOR;
		// break;
		// default:
		// textColor = NUMBER_COLOR;
		// break;
		// }
		// }

		if (item.typeOfDay == CalendarItem.TYPE_UNOCCUPIED_DAY) {
			// 해당날짜를 선택할 수 있을때(점유상태가 아닐때)
			textColor = SAT_COLOR;
			numberView.setBackgroundColor(0xeeeeee);
			if (!item.inMonth) {
				textsize = OUT_MONTH_TEXT_SIZE_SP;
				textColor = OUT_MONTH_TEXT_COLOR;
			}
		} else if (item.typeOfDay == CalendarItem.TYPE_RESERVE_DAY_START
				|| item.typeOfDay == CalendarItem.TYPE_RESERVE_DAY_END) {
			// 해당날짜가 check_in or check_out날짜일때
			textColor = Color.WHITE;
			numberView.setBackgroundColor(SAT_COLOR);
		} else if (item.typeOfDay == CalendarItem.TYPE_OCCUPIED_DAY) {
			// 해당날짜가 이미 점유된(예약된, 선택할수없는) 날짜 일 때
			numberView.setBackgroundColor(0xeeeeee);
			textColor = 0x10ffffff;
			if (!item.inMonth) {
				textsize = OUT_MONTH_TEXT_SIZE_SP;
				textColor = 0x10ffffff;
			}
		} else {
			// 해당 날짜가 stay기간인데 checkin, checkout date가 아닐때
			numberView.setBackgroundColor(RESERVED_BACK_COLOR);
			textColor = SAT_COLOR;

		}

		numberView.setTextColor(textColor);
		numberView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textsize);
		numberView.setText("" + item.dayOfMonth);
		// contentView setting

		// ArrayList items = item.items;
		// int size = items.size();
		// StringBuilder sb = new StringBuilder();
		// sb.append(size + "일");
		// contentView.setText(sb.toString());

	}
}
