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

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;

import com.mabi87.imagecroper.ImageCroper.ACTION_LIST;

public class CropBox extends Anchor {
	
	private Anchor mAnchor;
	private Paint mAnchorPaint;
	protected Rect mBound;
	private Paint mMaskPaint1;
	private Paint mMaskPaint2;
	
	private Paint mBitmapPaint;
	
	private Bitmap mBitmap;
	private Canvas mCanvas;
	
	private ACTION_LIST mCurrentEvent;

	public CropBox(float pX, float pY, Rect pBound) {
		super(pX + pBound.width()/2, pY + pBound.height()/2, MIN_BOX_SIZE + 100);
		mBound = pBound;
		
		mAnchor = new Anchor(0, 0, ANCHOR_SIZE);
		setAnchor();
		
		mAnchorPaint = new Paint();
		mAnchorPaint.setColor(Color.parseColor("#ffffff"));
		mAnchorPaint.setAntiAlias(true);
		mAnchorPaint.setStrokeWidth(2);
		
		mMaskPaint1 = new Paint();
		mMaskPaint1.setColor(Color.parseColor("#000000"));
		mMaskPaint1.setAntiAlias(true);
		
		mMaskPaint2 = new Paint();
		mMaskPaint2.setColor(Color.parseColor("#ffffff")); 
		mMaskPaint2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.XOR));
		
		mBitmapPaint = new Paint();
		mBitmapPaint.setFilterBitmap(true);
		mBitmapPaint.setAlpha(128);
		
		mBitmap = Bitmap.createBitmap(pBound.width(), pBound.height(), Bitmap.Config.ARGB_8888);
		mCanvas = new Canvas(mBitmap);
	}
	
	@Override
	public void move(float pX, float pY) {
		if(y-radius-pY > mBound.top && y+radius-pY < mBound.bottom) {
			super.move(0, pY);
			mAnchor.move(0, pY);
		}
		
		if(x-radius-pX > mBound.left && x+radius-pX < mBound.right) {
			super.move(pX, 0);
			mAnchor.move(pX, 0);
		}
	}
	
	// Image scale
	public void scale(float pX, float pY) {
		float lRadius = radius - pX;
		
		if(lRadius > MIN_BOX_SIZE) {
			boolean lLeft = (x - lRadius > mBound.left) ? true : false;
			boolean lTop = (y-lRadius > mBound.top) ? true : false;
			boolean lRight = (x+lRadius < mBound.right) ? true : false;
			boolean lBottom = (y+lRadius < mBound.bottom) ? true : false;
			
			boolean lLeftNot = ((x+pX) - lRadius > mBound.left) ? true : false;
			boolean lTopNot = ((y+pX)-lRadius > mBound.top) ? true : false;
			boolean lRightNot = ((x-pX)+lRadius < mBound.right) ? true : false;
			boolean lBottomNot = ((y-pX)+lRadius < mBound.bottom) ? true : false;
			
			if(lLeft && lTop && lRight && lBottom) {
				radius = lRadius;
			} else if(!lLeft && lTop && lRight && lBottom) {
				// Left
				if(lRightNot) {
					radius = lRadius;
					x -= pX;
				}
			} else if(!lLeft && !lTop && lRight && lBottom) {
				// Left & Top
				if(lRightNot && lBottomNot) {
					radius = lRadius;
					x -= pX;
					y -= pX;
				}
			} else if(lLeft && !lTop && lRight && lBottom) {
				// Top
				if(lBottomNot) {
					radius = lRadius;
					y -= pX;
				}
			} else if(lLeft && !lTop && !lRight && lBottom) {
				// Top & Right
				if(lBottomNot && lLeftNot) {
					radius = lRadius;
					x += pX;
					y -= pX;
				}
			} else if(lLeft && lTop && !lRight && lBottom) {
				// Right
				if(lLeftNot) {
					radius = lRadius;
					x += pX;
				}
			} else if(lLeft && lTop && !lRight && !lBottom) {
				// Right & Bottom
				if(lLeftNot && lTopNot) {
					radius = lRadius;
					x += pX;
					y += pX;
				}
			} else if(lLeft && lTop && lRight && !lBottom) {
				// Bottom
				if(lTopNot) {
					radius = lRadius;
					y += pX;
				}
			} else if(!lLeft && lTop && lRight && !lBottom) {
				// Left & Bottom
				if(lRightNot && lTopNot) {
					radius = lRadius;
					x -= pX;
					y += pX;
				}
			}
			
			setAnchor();
		}
	}
	
	private void setAnchor() {
		mAnchor.setLocation(x + Math.cos((45 * Math.PI) / 180) * radius, y - Math.sin((45 * Math.PI) / 180) * radius);
	}
	
	public void setState(ACTION_LIST pAction) {
		mCurrentEvent = pAction;
	}
	
	@Override
	public ACTION_LIST contains(float pX, float pY) {
		if(mAnchor.contains(pX, pY) != ACTION_LIST.none) {
			mCurrentEvent = ACTION_LIST.anchor;
			return mCurrentEvent;
		}

		if((pX >= x-radius && pX <= x+radius) && (pY >= y-radius && pY <= y+radius)) {
			mCurrentEvent = ACTION_LIST.move;
			return mCurrentEvent;
		} else {
			mCurrentEvent = ACTION_LIST.none;
			return mCurrentEvent;
		}
	}

	@Override
	public void draw(Canvas pCanvas, Paint pPaint) {
		mCanvas.drawRect(0, 0, pCanvas.getWidth(), pCanvas.getHeight(), mMaskPaint1);
		mCanvas.drawCircle(x - mBound.left, y - mBound.top, radius, mMaskPaint2);
		
		pCanvas.drawBitmap(mBitmap, null, mBound, mBitmapPaint);
		
		pCanvas.drawCircle(x, y, radius, pPaint);
		
		if(mCurrentEvent != ACTION_LIST.move) {
			mAnchor.draw(pCanvas, mAnchorPaint);
		}
	}
	
	public int getX() {
		return (int)(x - radius);
	}
	public int getY() {
		return (int)(y - radius);
	}
	public int getWidth() {
		return (int)(radius * 2);
	}
	public int getHeight() {
		return (int)(radius * 2);
	}

	@Override
	public String toString() {
		return "x: " + (int)(x - radius) + " y: " + (int)(y - radius) + " width " + (int)(radius * 2);
	}
	
}
