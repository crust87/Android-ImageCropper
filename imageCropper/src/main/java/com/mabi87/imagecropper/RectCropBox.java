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

import java.util.ArrayList;

public class RectCropBox extends CropBox {

	private enum ANCHOR_LIST {topLeft, topRight, bottomLeft, bottomRight};

	// Components
	private ArrayList<Anchor> mAnchors;

	// Attributes
	private float radius;

	// Working variable
	private ACTION_LIST mCurrentEvent;

	public RectCropBox(float pX, float pY, Rect pBound, float pScale) {
		super(pX, pY, pBound, pScale);

		radius = MIN_SIZE + 100;

		mAnchors = new ArrayList<Anchor>();
		for(int i = 0; i < ANCHOR_LIST.values().length; i++) {
			mAnchors.add(new Anchor(0, 0, Anchor.ANCHOR_SIZE));
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
		
		if(lRadius > MIN_SIZE) {
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

	@Override
	protected void setAnchor() {
		for(ANCHOR_LIST anchorItem: ANCHOR_LIST.values()) {
			Anchor anchor = mAnchors.get(anchorItem.ordinal());
			switch(anchorItem) {
				case topLeft:
					anchor.setLocation(x - radius, y - radius);
					break;
				case topRight:
					anchor.setLocation(x + radius, y - radius);
					break;
				case bottomLeft:
					anchor.setLocation(x - radius, y + radius);
					break;
				case bottomRight:
					anchor.setLocation(x + radius, y + radius);
					break;
			}
		}
	}

	@Override
	public boolean contains(float pX, float pY) {
		for(Anchor anchor: mAnchors) {
			if (anchor.contains(pX, pY)) {
				mCurrentEvent = ACTION_LIST.resize;
				return false;
			}
		}

		if((pX >= x-radius && pX <= x+radius) && (pY >= y-radius && pY <= y+radius)) {
			mCurrentEvent = ACTION_LIST.move;
			return true;
		}

		mCurrentEvent = ACTION_LIST.none;
		return false;
	}

	@Override
	public void draw(Canvas pCanvas) {
		mCanvas.drawRect(0, 0, pCanvas.getWidth(), pCanvas.getHeight(), mMaskPaint1);
		mCanvas.drawRect(getX(), getY(), getX() + getWidth(), getY() + getHeight(), mMaskPaint2);
		pCanvas.drawBitmap(mBitmap, null, mBound, mBitmapPaint);
		pCanvas.drawRect(x - radius, y - radius, x + radius, y + radius, mPaint);
		
		if(mCurrentEvent != ACTION_LIST.move) {
			for(Anchor anchor: mAnchors) {
				anchor.draw(pCanvas);
			}
		}
	}

	@Override
	protected float getX() {
		return (x - radius) - mBound.left;
	}

	@Override
	protected float getY() {
		return (y - radius) - mBound.top;
	}

	@Override
	protected float getWidth() {
		return radius * 2;
	}

	@Override
	protected float getHeight() {
		return radius * 2;
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
