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

import java.util.ArrayList;

public class RectCropBox extends CropBox {

    // Constants
	private static final int TOP_LEFT = 0;
	private static final int TOP_RIGHT = 1;
	private static final int BOTTOM_LEFT = 2;
	private static final int BOTTOM_RIGHT = 3;

	private static final int[] ANCHOR_ITEM = {TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT};

	// Components
	private ArrayList<Anchor> mAnchors;

	// Attributes
	private float mWidth;
    private float mHeight;

	// Working variable
	private ACTION_LIST mCurrentEvent;
	private int mCurrentAnchor;

	public RectCropBox(float x, float y, Rect bound, float scale) {
		super(x - DEFAULT_HALF_SIZE, y - DEFAULT_HALF_SIZE, bound, scale);

        mWidth = mHeight = DEFAULT_HALF_SIZE * 2;

		mAnchors = new ArrayList<Anchor>();
		for(int i = 0; i < ANCHOR_ITEM.length; i++) {
			mAnchors.add(new Anchor(ANCHOR_ITEM[i]));
		}
		setAnchor();
	}

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
				actionResult = resize(dx, dy);
			}

			if(actionResult) {
				setAnchor();
			}

			mPostX = x;
			mPostY = y;

			return true;
		} else if(event.getAction() == MotionEvent.ACTION_UP) {
			mCurrentEvent = ACTION_LIST.none;
			mCurrentAnchor = -1;

			return false;
		}

		return false;
	}
	
	@Override
	public boolean move(float x, float y) {
		boolean isMoved = false;

		if(mY-y > mBound.top && mY+mHeight-y < mBound.bottom) {
			isMoved = super.move(0, y);
		}
		
		if(mX-x > mBound.left && mX+mWidth-x < mBound.right) {
			isMoved = super.move(x, 0);
		}

		return isMoved;
	}
	
	// Image scale
	public boolean resize(float x, float y) {
		if(mCurrentAnchor != -1) {
            float lRight = mX + mWidth;
            float lBottom = mY + mHeight;

            float dX = mX;
            float dY = mY;
            float dWidth = mWidth;
            float dHeight = mHeight;

			switch(mCurrentAnchor) {
				case TOP_LEFT:
                    dX -= x;
                    dY -= y;
                    dWidth += x;
                    dHeight += y;
                    break;
				case TOP_RIGHT:
                    dY -= y;
                    dWidth -= x;
                    dHeight += y;
                    break;
				case BOTTOM_LEFT:
                    dX -= x;
                    dWidth += x;
                    dHeight -= y;
                    break;
				case BOTTOM_RIGHT:
                    dWidth -= x;
                    dHeight -= y;
                    break;
			}

            if(dX < mBound.left) {
                dX = mBound.left;
                dWidth = lRight - dX;
            }

            if(dX + dWidth > mBound.right) {
                dWidth = mBound.right - dX;
            }

            if(dY < mBound.top) {
                dY = mBound.top;
                dHeight = lBottom - dY;
            }

            if(dY + dHeight > mBound.bottom) {
                dHeight = mBound.bottom - dY;
            }

            // FUCK!!!!!
            if(dWidth < MIN_SIZE) {
                dWidth = MIN_SIZE;
            }

            if(dHeight < MIN_SIZE) {
                dHeight = MIN_SIZE;
            }

            mX = dX;
            mY = dY;
            mWidth = dWidth;
            mHeight = dHeight;

            return true;
		}

		return false;
	}

	@Override
	protected void setAnchor() {
		for(Anchor anchor: mAnchors) {
			switch(anchor.getId()) {
				case TOP_LEFT:
					anchor.setLocation(mX, mY);
					break;
				case TOP_RIGHT:
					anchor.setLocation(mX + mWidth, mY);
					break;
				case BOTTOM_LEFT:
					anchor.setLocation(mX, mY + mHeight);
					break;
				case BOTTOM_RIGHT:
					anchor.setLocation(mX + mWidth, mY + mHeight);
					break;
			}
		}
	}

	@Override
	public boolean contains(float x, float y) {
		for(int i = 0; i < mAnchors.size(); i++) {
			Anchor anchor = mAnchors.get(i);
			if (anchor.contains(x, y)) {
				mCurrentEvent = ACTION_LIST.resize;
				mCurrentAnchor = ANCHOR_ITEM[i];
				return false;
			}
		}

		mCurrentAnchor = -1;

		if((x >= mX && x <= mX+mWidth) && (y >= mY && y <= mY+mHeight)) {
			mCurrentEvent = ACTION_LIST.move;
			return true;
		}

		mCurrentEvent = ACTION_LIST.none;
		return false;
	}

	@Override
	public void draw(Canvas canvas) {
		mCanvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), mMaskPaint1);
		mCanvas.drawRect(getX(), getY(), getX() + getWidth(), getY() + getHeight(), mMaskPaint2);
        canvas.drawBitmap(mBitmap, null, mBound, mBitmapPaint);
        canvas.drawRect(mX, mY, mX + mWidth, mY + mHeight, mPaint);
		
		if(mCurrentEvent != ACTION_LIST.move) {
			for(Anchor anchor: mAnchors) {
				anchor.draw(canvas);
			}
		}
	}

	@Override
	protected float getX() {
		return mX - mBound.left;
	}

	@Override
	protected float getY() {
		return mY - mBound.top;
	}

	@Override
	protected float getWidth() {
		return mWidth;
	}

	@Override
	protected float getHeight() {
		return mHeight;
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
		for(Anchor anchor: mAnchors) {
			anchor.setColor(color);
		}
	}

	@Override
	public void setColor(String colorCode) {
		super.setColor(colorCode);
		for(Anchor anchor: mAnchors) {
			anchor.setColor(colorCode);
		}
	}
}
