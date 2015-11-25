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

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;


public class Anchor {
	// Constants
	public static final int ANCHOR_SIZE = 60;
	public static final int ANCHOR_SIZE_HALF = ANCHOR_SIZE/2;

	// Components
	protected Paint mPaint;

	// Attributes
	protected float x;
	protected float y;
	protected float radius;
	
	public Anchor(float pX, float pY, float pWidth) {
		mPaint = new Paint();
		mPaint.setColor(Color.WHITE);
		mPaint.setAntiAlias(true);
		mPaint.setStrokeWidth(2);

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
		pCanvas.drawCircle(x, y, ANCHOR_SIZE_HALF, mPaint);
	}
	
	public void setLocation(double pX, double pY) {
		x = (float)pX;
		y = (float)pY;
	}

	public boolean contains(float pX, float pY) {
		if((pX >= x - ANCHOR_SIZE && pX <= x + ANCHOR_SIZE) && (pY >= y - ANCHOR_SIZE && pY <= y + ANCHOR_SIZE)) {
			return true;
		} else {
			return false;
		}		
	}

	public void setColor(String colorCode) {
		int color = Color.parseColor(colorCode);
		mPaint.setColor(color);
	}

	public void setColor(int color) {
		mPaint.setColor(color);
	}

}
