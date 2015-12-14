/*
 * ImageCropper
 * https://github.com/crust87/Android-ImageCropper
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

package com.crust87.imagecropper.cropbox;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.view.MotionEvent;

import com.crust87.imagecropper.ImageCropper;
import com.crust87.imagecropper.R;

public abstract class CropBox {
	public enum ACTION_LIST{resize, move, none}

	// Components
	protected Context mContext;
	protected Paint mPaint;
	protected Paint mMaskPaint1;
	protected Paint mMaskPaint2;
	protected Paint mBitmapPaint;
	protected Bitmap mBitmap;
	protected Canvas mCanvas;

	// Attributes
	protected Rect mBound;
	protected int mMinSize;
	protected int mMinHalfSize;
	protected int mDefaultHalfSize;
	protected float mX;
	protected float mY;
	protected float mPostX;
	protected float mPostY;
	protected float mScale;
	protected int mBoxColor;
	protected int mLineWidth;
	protected int mAnchorSize;

	// Working Variables
	protected ACTION_LIST mCurrentEvent;

	protected CropBox(Context context) {
		mContext = context;

		mMinSize = mContext.getResources().getDimensionPixelSize(R.dimen.min_box_Size);
		mMinHalfSize = mMinSize / 2;
		mDefaultHalfSize = mContext.getResources().getDimensionPixelSize(R.dimen.default_box_size) / 2;
	}

	protected void setAttributes(float leftMargin, float topMargin, Rect bound, float scale, int boxColor, int lineWidth, int anchorSize) {
		mX = leftMargin + bound.width()/2;
		mY = topMargin + bound.height()/2;
		mBound = bound;
		mScale = scale;
		mBoxColor = boxColor;
		mLineWidth = lineWidth;
		mAnchorSize = anchorSize;
	}

	protected void init() {
		mPaint = new Paint();
		mPaint.setColor(Color.WHITE);
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeWidth(mLineWidth);
		mPaint.setColor(mBoxColor);
		mPaint.setStrokeWidth(mLineWidth);

		mMaskPaint1 = new Paint();
		mMaskPaint1.setColor(Color.BLACK);
		mMaskPaint1.setAntiAlias(true);

		mMaskPaint2 = new Paint();
		mMaskPaint2.setColor(Color.WHITE);
		mMaskPaint2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.XOR));

		mBitmapPaint = new Paint();
		mBitmapPaint.setFilterBitmap(true);
		mBitmapPaint.setAlpha(128);

		mBitmap = Bitmap.createBitmap(mBound.width(), mBound.height(), Bitmap.Config.ARGB_8888);
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
		mBoxColor = color;
		mPaint.setColor(mBoxColor);
	}

	public void setColor(String colorCode) {
		setColor(Color.parseColor(colorCode));
	}

	public int getLineWidth() {
		return mLineWidth;
	}

	public void setLineWidth(int lineWidth) {
		mLineWidth = lineWidth;
		mPaint.setStrokeWidth(mLineWidth);
	}

	public int getAnchorSize() {
		return mAnchorSize;
	}

	public void setAnchorSize(int anchorSize) {
		mAnchorSize = anchorSize;
		onSetAnchorSize();
	}

	protected abstract void onSetAnchorSize();

	public static class CropBoxBuilder {
		private int newBoxType;
		private float newLeftMargin;
		private float newTopMargin;
		private Rect newBound;
		private float newScale;
		private int newBoxColor;
		private int newLineWidth;
		private int newAnchorSize;

		public CropBoxBuilder setBoxType(int boxType) {
			newBoxType = boxType;
			return this;
		}

		public CropBoxBuilder setLeftMargin(float leftMargin) {
			newLeftMargin = leftMargin;
			return this;
		}

		public CropBoxBuilder setTopMargin(float topMargin) {
			newTopMargin = topMargin;
			return this;
		}

		public CropBoxBuilder setBound(Rect bound) {
			newBound = bound;
			return this;
		}

		public CropBoxBuilder setScale(float scale) {
			newScale = scale;
			return this;
		}

		public CropBoxBuilder setBoxColor(int boxColor) {
			newBoxColor = boxColor;
			return this;
		}

		public CropBoxBuilder setLineWidth(int lineWidth) {
			newLineWidth = lineWidth;
			return this;
		}

		public CropBoxBuilder setAnchorSize(int anchorSize) {
			newAnchorSize = anchorSize;
			return this;
		}

		public CropBox createCropBox(Context context) {
			CropBox cropBox;
			switch (newBoxType) {
				case ImageCropper.CIRCLE_CROP_BOX:
					cropBox = new CircleCropBox(context);
					break;
				case ImageCropper.RECT_CROP_BOX:
					cropBox = new RectCropBox(context);
					break;
				default:
					cropBox = new CircleCropBox(context);
					break;
			}

			cropBox.setAttributes(newLeftMargin, newTopMargin, newBound, newScale, newBoxColor, newLineWidth, newAnchorSize);
			cropBox.init();

			return cropBox;
		}
	}
}
