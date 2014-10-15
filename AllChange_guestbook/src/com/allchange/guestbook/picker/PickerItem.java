package com.allchange.guestbook.picker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.allchange.guestbook.R;

public class PickerItem extends FrameLayout {

	public TextView tv;
	public ImageView iv, iv_shadow;

	public PickerItem(Context context) {
		super(context);
		init();
	}

	private void init() {

		View v = LayoutInflater.from(getContext()).inflate(
				R.layout.picker_item, this);
		tv = (TextView) v.findViewById(R.id.picker_item_tv);
		iv = (ImageView) v.findViewById(R.id.picker_item_iv);
		iv_shadow = (ImageView) v.findViewById(R.id.picker_item_shadow);
	}

}
