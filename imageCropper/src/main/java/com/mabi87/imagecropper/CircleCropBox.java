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
import android.graphics.Rect;
import android.view.MotionEvent;

public class CircleCropBox extends CropBox {

	// Components
	private Anchor mAnchor;

	// Attributes
	private double mAnchorLoactionX = Math.cos((45 * Math.PI) / 180);
	private double mAnchorLoactionY = Math.sin((45 * Math.PI) / 180);
	private float mRadius;

	// Working variable
	private ACTION_LIST mCurrentEvent;

	public CircleCropBox(float pX, float pY, Rect pBound, float pScale) {
		super(pX, pY, pBound, pScale);

        mRadius = DEFAULT_HALF_SIZE;
		
		mAnchor = new Anchor(0);
		setAnchor();
	}

	@Override
	public boolean processTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			mPostX = x;
			mPostY = y;
			contains(event.getX(), event.getY());

			return false;
		} else if(event.getAction() == MotionEvent.ACTION_MOVE) {
			float dx = mPostX - x;
			float dy = mPostY - y;

			boolean actionResult = false;
			if(mCurrentEvent == ACTION_LIST.move) {
				actionResult = move(dx, dy);
			} if(mCurrentEvent == ACTION_LIST.resize) {
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
	public boolean move(float x, float y) {
		float dx = x;
		float dy = y;

		float top = mY - mRadius - y;
		if(top < mBound.top) {
			dy = y - mBound.top + top;
		}

		float bottom = mY + mRadius - y;
		if(bottom > mBound.bottom) {
			dy = y - mBound.bottom + bottom;
		}

		float left = mX - mRadius - x;
		if(left < mBound.left) {
			dx = x - mBound.left + left;
		}

		float right = mX + mRadius - x;
		if(right > mBound.right) {
			dx = x - mBound.right + right;
		}

		return super.move(dx, dy);
	}
	
	// Image scale
	public boolean scale(float d) {
		float lRadius = mRadius - d;
		
		if(lRadius > MIN_HALF_SIZE) {
			boolean lLeft = mX - lRadius > mBound.left;
			boolean lTop = mY - lRadius > mBound.top;
			boolean lRight = mX + lRadius < mBound.right;
			boolean lBottom = mY + lRadius < mBound.bottom;
			
			boolean lLeftNot = (mX + d) - lRadius > mBound.left;
			boolean lTopNot = (mY + d) - lRadius > mBound.top;
			boolean lRightNot = (mX - d) + lRadius < mBound.right;
			boolean lBottomNot = (mY - d) + lRadius < mBound.bottom;
			
			if(lLeft && lTop && lRight && lBottom) {
                mRadius = lRadius;
			} else if(!lLeft && lTop && lRight && lBottom) {
				// Left
				if(lRightNot) {
                    mRadius = lRadius;
					mX -= d;
				}
			} else if(!lLeft && !lTop && lRight && lBottom) {
				// Left & Top
				if(lRightNot && lBottomNot) {
                    mRadius = lRadius;
					mX -= d;
					mY -= d;
				}
			} else if(lLeft && !lTop && lRight && lBottom) {
				// Top
				if(lBottomNot) {
                    mRadius = lRadius;
					mY -= d;
				}
			} else if(lLeft && !lTop && !lRight && lBottom) {
				// Top & Right
				if(lBottomNot && lLeftNot) {
                    mRadius = lRadius;
					mX += d;
					mY -= d;
				}
			} else if(lLeft && lTop && !lRight && lBottom) {
				// Right
				if(lLeftNot) {
                    mRadius = lRadius;
					mX += d;
				}
			} else if(lLeft && lTop && !lRight && !lBottom) {
				// Right & Bottom
				if(lLeftNot && lTopNot) {
                    mRadius = lRadius;
					mX += d;
					mY += d;
				}
			} else if(lLeft && lTop && lRight && !lBottom) {
				// Bottom
				if(lTopNot) {
                    mRadius = lRadius;
					mY += d;
				}
			} else if(!lLeft && lTop && lRight && !lBottom) {
				// Left & Bottom
				if(lRightNot && lTopNot) {
                    mRadius = lRadius;
					mX -= d;
					mY += d;
				}
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	protected void setAnchor() {
		mAnchor.setLocation(mX + mAnchorLoactionX * mRadius, mY - mAnchorLoactionY * mRadius);
	}

	@Override
	public boolean contains(float x, float y) {
		if(mAnchor.contains(x, y)) {
			mCurrentEvent = ACTION_LIST.resize;
			return false;
		}

		if((x >= mX-mRadius && x <= mX+mRadius) && (y >= mY-mRadius && y <= mY+mRadius)) {
			mCurrentEvent = ACTION_LIST.move;
			return true;
		}

		mCurrentEvent = ACTION_LIST.none;
		return false;
	}

	@Override
	public void draw(Canvas canvas) {
		mCanvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), mMaskPaint1);
		mCanvas.drawCircle(mX - mBound.left, mY - mBound.top, mRadius, mMaskPaint2);
        canvas.drawBitmap(mBitmap, null, mBound, mBitmapPaint);
        canvas.drawCircle(mX, mY, mRadius, mPaint);
		
		if(mCurrentEvent != ACTION_LIST.move) {
			mAnchor.draw(canvas);
		}
	}
	@Override
	protected float getX() {
		return (mX - mRadius) - mBound.left;
	}

	@Override
	protected float getY() {
		return (mY - mRadius) - mBound.top;
	}

	@Override
	protected float getWidth() {
		return mRadius * 2;
	}

	@Override
	protected float getHeight() {
		return mRadius * 2;
	}

	@Override
	public int getCropX() {
		return (int) (getX() / mScale);
	}

	@Override
	public int getCropY() {
		return (int) (getY() / mScale);
	}

	@Override
	public int getCropWidth() {
		return (int) (getWidth() / mScale);
	}

	@Override
	public int getCropHeight() {
		return (int) (getHeight() / mScale);
	}

	@Override
	public String toString() {
		return "view x: " + getX() + " y: " + getY() + " width " + getWidth();
	}

	@Override
	public void setColor(int color) {
		super.setColor(color);
		mAnchor.setColor(color);
	}

	@Override
	public void setColor(String colorCode) {
		super.setColor(colorCode);
		mAnchor.setColor(colorCode);
	}
}
