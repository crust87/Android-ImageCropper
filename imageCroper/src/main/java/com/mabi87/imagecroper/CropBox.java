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
import android.view.MotionEvent;

public class CropBox extends Anchor {

	// Components
	private Anchor mAnchor;
	protected Rect mBound;
	private Paint mMaskPaint1;
	private Paint mMaskPaint2;
	private Paint mBitmapPaint;
	private Paint mCropBoxPaint;
	private Bitmap mBitmap;
	private Canvas mCanvas;

	// Attributes
	private double anchorLoactionX = Math.cos((45 * Math.PI) / 180);
	private double anchorLoactionY = Math.sin((45 * Math.PI) / 180);
	private float mPostX;
	private float mPostY;
	private float mScale;

	// Working variable
	private ACTION_LIST mCurrentEvent;

	public CropBox(float pX, float pY, Rect pBound, float pScale) {
		super(pX + pBound.width()/2, pY + pBound.height()/2, MIN_BOX_SIZE + 100);
		mBound = pBound;
		mScale = pScale;
		
		mAnchor = new Anchor(0, 0, ANCHOR_SIZE);
		setAnchor();
		
		mMaskPaint1 = new Paint();
		mMaskPaint1.setColor(Color.parseColor("#000000"));
		mMaskPaint1.setAntiAlias(true);
		
		mMaskPaint2 = new Paint();
		mMaskPaint2.setColor(Color.parseColor("#ffffff")); 
		mMaskPaint2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.XOR));
		
		mBitmapPaint = new Paint();
		mBitmapPaint.setFilterBitmap(true);
		mBitmapPaint.setAlpha(128);

		mCropBoxPaint = new Paint();
		mCropBoxPaint.setColor(Color.parseColor("#ffffff"));
		mCropBoxPaint.setAntiAlias(true);
		mCropBoxPaint.setStrokeWidth(5);
		mCropBoxPaint.setStyle(Paint.Style.STROKE);
		
		mBitmap = Bitmap.createBitmap(pBound.width(), pBound.height(), Bitmap.Config.ARGB_8888);
		mCanvas = new Canvas(mBitmap);
	}

	public boolean processTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			mPostX = x;
			mPostY = y;
			mCurrentEvent = contains(event.getX(), event.getY());

			return false;
		} else if(event.getAction() == MotionEvent.ACTION_MOVE) {
			float dx = mPostX - x;
			float dy = mPostY - y;

			boolean actionResult = false;
			if(mCurrentEvent == ACTION_LIST.move) {
				actionResult = move(dx, dy);
			} if(mCurrentEvent == ACTION_LIST.anchor) {
				actionResult = scale(dx);
			}

			if(actionResult) {
				setAnchor();
			}

			mPostX = x;
			mPostY = y;

			return true;
		} else if(event.getAction() == MotionEvent.ACTION_UP) {
			mCurrentEvent = ACTION_LIST.none;

			return false;
		}

		return false;
	}
	
	@Override
	public boolean move(float pX, float pY) {
		boolean isMoved = false;

		if(y-radius-pY > mBound.top && y+radius-pY < mBound.bottom) {
			isMoved = super.move(0, pY);
		}
		
		if(x-radius-pX > mBound.left && x+radius-pX < mBound.right) {
			isMoved = super.move(pX, 0);
		}

		return isMoved;
	}
	
	// Image scale
	public boolean scale(float d) {
		float lRadius = radius - d;
		
		if(lRadius > MIN_BOX_SIZE) {
			boolean lLeft = x - lRadius > mBound.left;
			boolean lTop = y - lRadius > mBound.top;
			boolean lRight = x + lRadius < mBound.right;
			boolean lBottom = y + lRadius < mBound.bottom;
			
			boolean lLeftNot = (x + d) - lRadius > mBound.left;
			boolean lTopNot = (y + d) - lRadius > mBound.top;
			boolean lRightNot = (x - d) + lRadius < mBound.right;
			boolean lBottomNot = (y - d) + lRadius < mBound.bottom;
			
			if(lLeft && lTop && lRight && lBottom) {
				radius = lRadius;
			} else if(!lLeft && lTop && lRight && lBottom) {
				// Left
				if(lRightNot) {
					radius = lRadius;
					x -= d;
				}
			} else if(!lLeft && !lTop && lRight && lBottom) {
				// Left & Top
				if(lRightNot && lBottomNot) {
					radius = lRadius;
					x -= d;
					y -= d;
				}
			} else if(lLeft && !lTop && lRight && lBottom) {
				// Top
				if(lBottomNot) {
					radius = lRadius;
					y -= d;
				}
			} else if(lLeft && !lTop && !lRight && lBottom) {
				// Top & Right
				if(lBottomNot && lLeftNot) {
					radius = lRadius;
					x += d;
					y -= d;
				}
			} else if(lLeft && lTop && !lRight && lBottom) {
				// Right
				if(lLeftNot) {
					radius = lRadius;
					x += d;
				}
			} else if(lLeft && lTop && !lRight && !lBottom) {
				// Right & Bottom
				if(lLeftNot && lTopNot) {
					radius = lRadius;
					x += d;
					y += d;
				}
			} else if(lLeft && lTop && lRight && !lBottom) {
				// Bottom
				if(lTopNot) {
					radius = lRadius;
					y += d;
				}
			} else if(!lLeft && lTop && lRight && !lBottom) {
				// Left & Bottom
				if(lRightNot && lTopNot) {
					radius = lRadius;
					x -= d;
					y += d;
				}
			}

			return true;
		} else {
			return false;
		}
	}

	private void setAnchor() {
		mAnchor.setLocation(x + anchorLoactionX * radius, y - anchorLoactionY * radius);
	}

	@Override
	public ACTION_LIST contains(float pX, float pY) {
		if(mAnchor.contains(pX, pY) == ACTION_LIST.anchor) {
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
	public void draw(Canvas pCanvas) {
		mCanvas.drawRect(0, 0, pCanvas.getWidth(), pCanvas.getHeight(), mMaskPaint1);
		mCanvas.drawCircle(x - mBound.left, y - mBound.top, radius, mMaskPaint2);
		pCanvas.drawBitmap(mBitmap, null, mBound, mBitmapPaint);
		pCanvas.drawCircle(x, y, radius, mCropBoxPaint);
		
		if(mCurrentEvent != ACTION_LIST.move) {
			mAnchor.draw(pCanvas);
		}
	}
	
	private float getX() {
		return (x - radius) - mBound.left;
	}
	private float getY() {
		return (y - radius) - mBound.top;
	}
	private float getWidth() {
		return radius * 2;
	}
	private float getHeight() {
		return radius * 2;
	}
	public int getCropX() {
		return (int) (getX() / mScale);
	}
	public int getCropY() {
		return (int) (getY() / mScale);
	}
	public int getCropWidth() {
		return (int) (getWidth() / mScale);
	}
	public int getCropHeight() {
		return (int) (getHeight() / mScale);
	}

	@Override
	public String toString() {
		return "view x: " + getX() + " y: " + getY() + " width " + getWidth();
	}
	
}
