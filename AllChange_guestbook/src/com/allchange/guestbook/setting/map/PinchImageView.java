package com.allchange.guestbook.setting.map;

import com.allchange.guestbook.property.PropertyManager;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

public class PinchImageView extends ImageView implements OnTouchListener {
	public static final String TAG = "PinchImageView";
	// These matrices will be used to move and zoom image
	Matrix matrix = new Matrix();
	Matrix savedMatrix = new Matrix();

	private float mScale = 1;

	// We can be in one of these 3 states
	static final int NONE = 0;
	static final int DRAG = 1;
	static final int ZOOM = 2;
	int mode = NONE;

	// Remember some things for zooming
	PointF start = new PointF();
	PointF mid = new PointF();
	float oldDist = 1f;
	String savedItemClicked;

	public PinchImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.setOnTouchListener(this);

	}

	public void setImageCenter(float x, float y) {
		Log.e(TAG, x + ", " + y);
		mid.x = x / 2;
		mid.y = y / 2;
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		mid.x = this.getWidth() / 2;
		mid.y = this.getHeight() / 2;
		Log.e(TAG, "onWindowFocusChangeds radius : "
				+ PropertyManager.getInstance().getRadiusMeter() + "m");
		float ff = (float) (Double.parseDouble(PropertyManager.getInstance()
				.getRadiusMeter()) / 2400);
		matrix.postScale(ff, ff, mid.x, mid.y);
		this.setImageMatrix(matrix);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		ImageView view = (ImageView) v;
		dumpEvent(event);

		// Handle touch events here...
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			// 이동하는 애니
			// savedMatrix.set(matrix);
			// start.set(event.getX(), event.getY());
			// Log.d(TAG, "mode=DRAG");
			// mode = DRAG;
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			oldDist = spacing(event);
			Log.d(TAG, "oldDist=" + oldDist);
			if (oldDist > 10f) {
				savedMatrix.set(matrix);
				// midPoint(mid, event);
				mode = ZOOM;
				Log.d(TAG, "mode=ZOOM");
			}
			break;
		case MotionEvent.ACTION_UP:
			if (mListener != null) {
				mListener.onFinishRange();
			}
		case MotionEvent.ACTION_POINTER_UP:
			mode = NONE;

			Log.d(TAG, "mode=NONE");
			break;
		case MotionEvent.ACTION_MOVE:
			if (mode == DRAG) {
				// ...
				matrix.set(savedMatrix);
				matrix.postTranslate(event.getX() - start.x, event.getY()
						- start.y);
			} else if (mode == ZOOM) {
				float newDist = spacing(event);
				Log.d(TAG, "newDist=" + newDist);
				if (newDist > 10f) {
					matrix.set(savedMatrix);
					float scale = newDist / oldDist;
					Log.e(TAG, "scale : " + scale);

					// mScale = mScale - (prescale - scale);
					// Log.e(TAG, "mScale : " + mScale);
					float[] values = new float[9];

					matrix.postScale(scale, scale, mid.x, mid.y);
					matrix.getValues(values);

					if (values[Matrix.MSCALE_X] > 1
							|| values[Matrix.MSCALE_X] < 0.05) {
						matrix.postScale(1 / scale, 1 / scale, mid.x, mid.y);
						matrix.getValues(values);
						if (mListener != null) {
							mListener
									.onChangedTripPalTap(values[Matrix.MSCALE_X]);
						}
						return false;
					}
					// Log.e(TAG, "========================================");
					// Log.e(TAG, "MPERSP_0 : " + values[Matrix.MPERSP_0]);
					// Log.e(TAG, "MPERSP_1 : " + values[Matrix.MPERSP_1]);
					// Log.e(TAG, "MPERSP_2 : " + values[Matrix.MPERSP_2]);
					// Log.e(TAG, "MSCALE_X : " + values[Matrix.MSCALE_X]);
					// Log.e(TAG, "MSCALE_Y : " + values[Matrix.MSCALE_Y]);
					// Log.e(TAG, "MSKEW_X : " + values[Matrix.MSKEW_X]);
					// Log.e(TAG, "MSKEW_Y : " + values[Matrix.MSKEW_Y]);
					// Log.e(TAG, "MTRANS_X : " + values[Matrix.MTRANS_X]);
					// Log.e(TAG, "MTRANS_Y : " + values[Matrix.MTRANS_Y]);
					if (mListener != null) {
						mListener.onChangedTripPalTap(values[Matrix.MSCALE_X]);
					}
				}
			}
			break;
		}

		view.setImageMatrix(matrix);
		return true;
	}

	private void dumpEvent(MotionEvent event) {
		String names[] = { "DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE",
				"POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?" };
		StringBuilder sb = new StringBuilder();
		int action = event.getAction();
		int actionCode = action & MotionEvent.ACTION_MASK;

		// 로그 안뜨게
		if (actionCode == 2) {
			return;
		}
		sb.append("event ACTION_").append(names[actionCode]);
		if (actionCode == MotionEvent.ACTION_POINTER_DOWN
				|| actionCode == MotionEvent.ACTION_POINTER_UP) {
			sb.append("(pid ").append(
					action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
			sb.append(")");
		}
		sb.append("[");
		for (int i = 0; i < event.getPointerCount(); i++) {
			sb.append("#").append(i);
			sb.append("(pid ").append(event.getPointerId(i));
			sb.append(")=").append((int) event.getX(i));
			sb.append(",").append((int) event.getY(i));
			if (i + 1 < event.getPointerCount())
				sb.append(";");
		}
		sb.append("]");
		Log.d(TAG, sb.toString());
	}

	/** Determine the space between the first two fingers */
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	/** Calculate the mid point of the first two fingers */
	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		// point.set(x / 2, y / 2);
	}

	public interface OnChangedRangeListener {
		public void onChangedTripPalTap(float range);

		public void onFinishRange();
	}

	OnChangedRangeListener mListener;

	public void setOnChangedRangeListener(OnChangedRangeListener listener) {
		mListener = listener;
	}
}
