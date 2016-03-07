package com.tribaltech.android.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.tribaltech.android.scnstrikefirst.R;

import rmn.androidscreenlibrary.ASSL;

public class PinView extends View {

	Bitmap filledPin;
	Bitmap emptyPin;
	float progress;
	Rect rectangle;

	boolean draw = false;

	public PinView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void updateProgress(int percentFilled) {
		int height = (int) (111 * ASSL.Yscale());
		int width = (int) (45 * ASSL.Xscale());
		rectangle = new Rect(0, height - (int) (height / 100f * percentFilled),
				width, height);
		draw = true;
		emptyPin = BitmapFactory.decodeResource(getResources(),
				R.drawable.empty_pin);
		emptyPin = Bitmap.createScaledBitmap(emptyPin, width, height, false);

		filledPin = BitmapFactory.decodeResource(getResources(),
				R.drawable.filled_pin);
		filledPin = Bitmap.createScaledBitmap(filledPin, width, height, false);
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (draw) {
			canvas.drawBitmap(emptyPin, 0, 0, null);
			canvas.drawBitmap(filledPin, rectangle, rectangle, null);
		}
	}
}