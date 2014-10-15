package com.allchange.guestbook.setting.map;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;

import com.allchange.guestbook.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

public class PinchGestureView extends ImageView {
	public static final String TAG = "PinchGestureView";

	private static final int INVALID_POINTER_ID = 0;
	private ScaleGestureDetector mScaleDetector;
	private float mScaleFactor = 1.f;

	private final int MAX_RADIUS = 3000;
	private final float MIN_SCALE = 0.1f;
	private final float MAX_SCALE = 1.0f;

	LatLng notEditing_LatLng;
	Circle mCircle = null;

	private GoogleMap googleMap;

	// Existing code ...

	public PinchGestureView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// Create our ScaleGestureDetector
		// mIcon = context.getResources().getDrawable(R.drawable.map_circle);
		// mIcon.setBounds(0, 0, mIcon.getIntrinsicWidth(),
		// mIcon.getIntrinsicHeight());
		mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());

	}

	public void setGoogleMap(GoogleMap map, LatLng latLng, double radius) {
		googleMap = map;
		notEditing_LatLng = latLng;
		mScaleFactor = (float) (radius / MAX_RADIUS);
		this.invalidate(new Rect(0, 0, this.getWidth(), this.getBottom()));
	}

	float mLastTouchX, mLastTouchY;
	private int mActivePointerId;
	private float mPosX, mPosY;

	// private Drawable mIcon;

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// Let the ScaleGestureDetector inspect all events.
		mScaleDetector.onTouchEvent(ev);

		final int action = ev.getAction();
		switch (action & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN: {
			final float x = ev.getX();
			final float y = ev.getY();

			mLastTouchX = x;
			mLastTouchY = y;
			mActivePointerId = ev.getPointerId(0);
			break;
		}

		case MotionEvent.ACTION_MOVE: {
			final int pointerIndex = ev.findPointerIndex(mActivePointerId);
			final float x = ev.getX(pointerIndex);
			final float y = ev.getY(pointerIndex);

			// Only move if the ScaleGestureDetector isn't processing a gesture.
			if (!mScaleDetector.isInProgress()) {
				final float dx = x - mLastTouchX;
				final float dy = y - mLastTouchY;

				mPosX += dx;
				mPosY += dy;

				invalidate();
			}

			mLastTouchX = x;
			mLastTouchY = y;

			break;
		}

		case MotionEvent.ACTION_UP: {
			mActivePointerId = INVALID_POINTER_ID;
			break;
		}

		case MotionEvent.ACTION_CANCEL: {
			mActivePointerId = INVALID_POINTER_ID;
			break;
		}

		case MotionEvent.ACTION_POINTER_UP: {
			final int pointerIndex = (ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
			final int pointerId = ev.getPointerId(pointerIndex);
			if (pointerId == mActivePointerId) {
				// This was our active pointer going up. Choose a new
				// active pointer and adjust accordingly.
				final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
				mLastTouchX = ev.getX(newPointerIndex);
				mLastTouchY = ev.getY(newPointerIndex);
				mActivePointerId = ev.getPointerId(newPointerIndex);
			}
			if (mListener != null) {
				mListener.onFinishRange(MAX_RADIUS * mScaleFactor);
			}
			break;
		}
		}

		return true;
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		// Log.e(TAG, "" + mScaleFactor);
		// 이미지 스케일
		// canvas.save();
		// canvas.translate(mPosX, mPosY);
		// canvas.scale(mScaleFactor, mScaleFactor);
		// mIcon.draw(canvas);
		// canvas.restore();
		if (mListener != null) {
			mListener.onChangedTripPalTap(MAX_RADIUS * mScaleFactor);
		}
		if (googleMap != null) {
			if (mCircle == null) {
				mCircle = googleMap
						.addCircle(new CircleOptions()
								.center(notEditing_LatLng)
								.radius(MAX_RADIUS * mScaleFactor)
								.strokeColor(
										getResources().getColor(
												R.color.app_color))
								.strokeWidth(4.0f)
								.fillColor(new Color().argb(0, 0, 0, 0)));
				// googleMap.addPolyline(new
				// PolylineOptions().add(notEditing_LatLng).)
			}
			mCircle.setRadius(MAX_RADIUS * mScaleFactor);

		}
	}

	private class ScaleListener extends
			ScaleGestureDetector.SimpleOnScaleGestureListener {
		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			mScaleFactor *= detector.getScaleFactor();

			// Don't let the object get too small or too large.
			mScaleFactor = Math.max(MIN_SCALE,
					Math.min(mScaleFactor, MAX_SCALE));

			invalidate();
			return true;
		}
	}

	public interface OnChangedRangeListener {
		public void onChangedTripPalTap(double range);

		public void onFinishRange(double radius);
	}

	OnChangedRangeListener mListener;

	public void setOnChangedRangeListener(OnChangedRangeListener listener) {
		mListener = listener;
	}
}
