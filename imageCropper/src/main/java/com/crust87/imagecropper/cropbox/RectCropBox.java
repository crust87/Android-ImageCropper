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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.view.MotionEvent;

import com.crust87.imagecropper.cropbox.anchor.Anchor;

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
	private int mCurrentAnchor;

	protected RectCropBox(Context context) {
		super(context);
	}

	@Override
	protected void setAttributes(float leftMargin, float topMargin, Rect bound, float scale, int boxColor, int lineWidth, int anchorSize) {
		super.setAttributes(leftMargin - mDefaultHalfSize, topMargin - mDefaultHalfSize, bound, scale, boxColor, lineWidth, anchorSize);

		mWidth = mDefaultHalfSize * 2;
		mHeight = mDefaultHalfSize * 2;
	}

	@Override
	protected void init() {
		super.init();

		mAnchors = new ArrayList<Anchor>();
        for(int anchorItem: ANCHOR_ITEM) {
            Anchor anchor = new Anchor(anchorItem, mAnchorSize / 2);
            anchor.setColor(mBoxColor);
            mAnchors.add(anchor);
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
		float dx = x;
		float dy = y;

		float top = mY - y;
		if(top < mBound.top) {
			dy = y - mBound.top + top;
		}

		float bottom = mY + mHeight - y;
		if(bottom > mBound.bottom) {
			dy = y - mBound.bottom + bottom;
		}

		float left = mX - x;
		if(left < mBound.left) {
			dx = x - mBound.left + left;
		}

		float right = mX + mWidth - x;
		if(right > mBound.right) {
			dx = x - mBound.right + right;
		}

		return super.move(dx, dy);
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

            if(dWidth < mMinSize) {
				dWidth = mMinSize;

				if(mCurrentAnchor == TOP_LEFT || mCurrentAnchor == BOTTOM_LEFT) {
					dX = lRight - mMinSize;
				}
            }

            if(dHeight < mMinSize) {
				dHeight = mMinSize;

				if(mCurrentAnchor == TOP_LEFT || mCurrentAnchor == TOP_RIGHT) {
					dY = lBottom - mMinSize;
				}
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
			anchor.setColor(mBoxColor);
		}
	}

	@Override
	public void setColor(String colorCode) {
		super.setColor(Color.parseColor(colorCode));
	}

	@Override
	protected void onSetAnchorSize() {
		for(Anchor anchor: mAnchors) {
			anchor.setRadius(mAnchorSize / 2);
		}
	}
}
