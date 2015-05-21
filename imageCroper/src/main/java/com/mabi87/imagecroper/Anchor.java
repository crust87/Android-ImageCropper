/*
 * ImageCroper
 * https://github.com/mabi87/Android-ImageCroper
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

package com.mabi87.imagecroper;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;


public class Anchor {
	public static enum ACTION_LIST{anchor, move, none}

	public static final int ANCHOR_SIZE = 60;
	public static final int ANCHOR_SIZE_HALF = ANCHOR_SIZE/2;
	public static final int MIN_BOX_SIZE = 50;

	private Paint mAnchorPaint;
	
	protected float x;
	protected float y;
	protected float radius;
	
	public Anchor(float pX, float pY, float pWidth) {
		mAnchorPaint = new Paint();
		mAnchorPaint.setColor(Color.parseColor("#ffffff"));
		mAnchorPaint.setAntiAlias(true);
		mAnchorPaint.setStrokeWidth(2);

		x = pX;
		y = pY;
		radius = pWidth;
	}
	
	public boolean move(float pX, float pY) {
		x -= pX;
		y -= pY;

		return true;
	}
	
	public void draw(Canvas pCanvas) {
		pCanvas.drawCircle(x, y, ANCHOR_SIZE_HALF, mAnchorPaint);
	}
	
	public void setLocation(double pX, double pY) {
		x = (float)pX;
		y = (float)pY;
	}

	public ACTION_LIST contains(float pX, float pY) {
		if((pX >= x - ANCHOR_SIZE && pX <= x + ANCHOR_SIZE) && (pY >= y - ANCHOR_SIZE && pY <= y + ANCHOR_SIZE)) {
			return ACTION_LIST.anchor;
		} else {
			return ACTION_LIST.none;
		}		
	}
	
	public float getLeft() {
		return x;
	}
	
	public float getTop() {
		return y;
	}
	
	public float getRight() {
		return x + radius;
	}
	
	public float getBottom() {
		return y + radius;
	}

}
