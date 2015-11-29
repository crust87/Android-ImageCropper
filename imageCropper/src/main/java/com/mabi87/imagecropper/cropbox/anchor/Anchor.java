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

package com.mabi87.imagecropper.cropbox.anchor;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;


public class Anchor {
	// Components
	protected Paint mPaint;

	// Attributes
	private int mId;
	private int mAnchorSize;
	protected float mX;
	protected float mY;
	protected float mRadius;
	private float mTouchArea;

	public Anchor(int id, int anchorSize) {
		this(id, 0, 0, anchorSize / 2);
	}
	
	public Anchor(int id, float x, float y, float radius) {
		mPaint = new Paint();
		mPaint.setColor(Color.WHITE);
		mPaint.setAntiAlias(true);
		mPaint.setStrokeWidth(2);

		mId = id;
		mX = x;
		mY = y;
		mRadius = radius;
		mTouchArea = radius * 2;
	}
	
	public void draw(Canvas pCanvas) {
		pCanvas.drawCircle(mX, mY, mRadius, mPaint);
	}
	
	public void setLocation(double x, double y) {
		mX = (float)x;
		mY = (float)y;
	}

	public boolean contains(float x, float y) {
		if((x >= mX - mTouchArea && x <= mX + mTouchArea) && (y >= mY - mTouchArea && y <= mY + mTouchArea)) {
			return true;
		} else {
			return false;
		}		
	}

	// Getters and Setters
	public void setColor(String colorCode) {
		int color = Color.parseColor(colorCode);
		mPaint.setColor(color);
	}

	public void setColor(int color) {
		mPaint.setColor(color);
	}

	public int getId() {
		return mId;
	}

	public void setId(int id) {
		mId = id;
	}

	public float getRadius() {
		return mRadius;
	}

	public void setRadius(float radius) {
		mRadius = radius;
	}
}
