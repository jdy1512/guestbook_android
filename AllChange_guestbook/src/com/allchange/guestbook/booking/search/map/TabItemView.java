package com.allchange.guestbook.booking.search.map;

import android.content.Context;
import android.graphics.drawable.StateListDrawable;
import android.widget.ImageView;

public class TabItemView extends ImageView {

	public TabItemView(Context context) {
		super(context);
	}

	public void setImageSelector(int normal, int selected) {
		StateListDrawable states = new StateListDrawable();
		states.addState(new int[] { android.R.attr.state_pressed },
				getResources().getDrawable(selected));

		states.addState(new int[] { android.R.attr.state_focused },
				getResources().getDrawable(normal));

		states.addState(new int[] {}, getResources().getDrawable(normal));

		this.setImageDrawable(states);
	}
}
