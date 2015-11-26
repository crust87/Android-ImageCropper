/*
 * ImageCropper
 * https://github.com/mabi87/Android-ImageCropper
 *
 * Mabi
 * crust87@gmail.com
 * last modify 2015-05-19
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mabi87.imagecropper;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.view.MotionEvent;

public abstract class CropBox {
	public static final int MIN_SIZE = 100;
	public static final int MIN_HALF_SIZE = MIN_SIZE / 2;
	public static final int DEFAULT_HALF_SIZE = MIN_SIZE + 50;

	public static enum ACTION_LIST{resize, move, none}

	// Components
	protected Rect mBound;
	protected Paint mPaint;
	protected Paint mMaskPaint1;
	protected Paint mMaskPaint2;
	protected Paint mBitmapPaint;
	protected Bitmap mBitmap;
	protected Canvas mCanvas;

	// Attributes
	protected float mX;
	protected float mY;
	protected float mPostX;
	protected float mPostY;
	protected float mScale;


	public CropBox(float x, float y, Rect bound, float scale) {
		mX = x + bound.width()/2;
		mY = y + bound.height()/2;
		mBound = bound;
		mScale = scale;

		mPaint = new Paint();
		mPaint.setColor(Color.WHITE);
		mPaint.setAntiAlias(true);
		mPaint.setStrokeWidth(5);
		mPaint.setStyle(Paint.Style.STROKE);
		
		mMaskPaint1 = new Paint();
		mMaskPaint1.setColor(Color.BLACK);
		mMaskPaint1.setAntiAlias(true);
		
		mMaskPaint2 = new Paint();
		mMaskPaint2.setColor(Color.WHITE);
		mMaskPaint2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.XOR));
		
		mBitmapPaint = new Paint();
		mBitmapPaint.setFilterBitmap(true);
		mBitmapPaint.setAlpha(128);
		
		mBitmap = Bitmap.createBitmap(bound.width(), bound.height(), Bitmap.Config.ARGB_8888);
		mCanvas = new Canvas(mBitmap);
	}

	public abstract boolean processTouchEvent(MotionEvent event);
	public boolean move(float x, float y) {
		mX -= x;
		mY -= y;

		return true;
	}

	protected abstract void setAnchor();

	public abstract boolean contains(float x, float y);
	public abstract void draw(Canvas canvas);

	protected abstract float getX();
	protected abstract float getY();
	protected abstract float getWidth();
	protected abstract float getHeight();

	public abstract int getCropX();
	public abstract int getCropY();
	public abstract int getCropWidth();
	public abstract int getCropHeight();

	@Override
	public String toString() {
		return "view x: " + getX() + " y: " + getY() + " width " + getWidth();
	}

	public void setColor(int color) {
		mPaint.setColor(color);
	}

	public void setColor(String colorCode) {
		int color = Color.parseColor(colorCode);
		mPaint.setColor(color);
	}
}
