/*
 * ImageCroper
 * https://github.com/mabi87/Android-ImageCroper
 *
 * Mabi
 * crust87@gmail.com
 * last modify 2015-05-20
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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class ImageCroper extends SurfaceView implements SurfaceHolder.Callback {
	public static enum ACTION_LIST{anchor, move, none}
	
	private Context mContext;
	private SurfaceHolder mHolder;
	
	private Bitmap mImage;
	private int mAngle;
	private Bitmap mScaledImage;
	private Rect mImageBound;
	
	
	private Paint mBitmapPaint;
	private Paint mEditBoxPaint;
	
	private EditBox mEditBox;
	
	private int mWidth;
	private int mHeight;
	
	private int mLeftMargin;
	private int mTopMargin;
	
	private ACTION_LIST mCurrentAction;
	
	private float mPostX;
	private float mPostY;
	
	public ImageCroper(Context pContext, Bitmap pImage, int pAngle) {
		super(pContext);
		mContext = pContext;
		mImage = pImage;
		mAngle = pAngle;
		
		mHolder = getHolder();
		mHolder.addCallback(this);
		
		mBitmapPaint = new Paint();
		mBitmapPaint.setFilterBitmap(true);
		
		mEditBoxPaint = new Paint();
		mEditBoxPaint.setColor(Color.parseColor("#ffffff"));
		mEditBoxPaint.setAntiAlias(true);
		mEditBoxPaint.setStrokeWidth(5);
		mEditBoxPaint.setStyle(Paint.Style.STROKE);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		setWillNotDraw(false);
		mWidth = getWidth();
		mHeight = getHeight();
		
		float scaleX;
	    float scaleY;
	    if(mAngle == 90) {
	    	scaleX = (float) getWidth() / mImage.getHeight();
		    scaleY = (float) getHeight() / mImage.getWidth();
	    } else {
	    	scaleX = (float) getWidth() / mImage.getWidth();
		    scaleY = (float) getHeight() / mImage.getHeight();
	    }
	    
	    int newWidth;
	    int newHeight;
	    if(scaleX > scaleY) {
	    	newWidth = (int) (mImage.getWidth() * scaleY);
	    	newHeight = (int) (mImage.getHeight() * scaleY);
	    } else {
	    	newWidth = (int) (mImage.getWidth() * scaleX);
	    	newHeight = (int) (mImage.getHeight() * scaleX);
	    }
	    
	    mScaledImage = Bitmap.createScaledBitmap(mImage, newWidth, newHeight, true);
	    
	    if(mAngle == 90) {
	    	Matrix matrix = new Matrix();
	 	    matrix.postRotate(mAngle);
	    	mScaledImage = Bitmap.createBitmap(mScaledImage, 0, 0, newWidth, newHeight, matrix, true);
	    }
	    
	    mLeftMargin = (mWidth - mScaledImage.getWidth()) / 2;
	    mTopMargin = (mHeight - mScaledImage.getHeight()) / 2;
	    
	    mImageBound = new Rect(0 + mLeftMargin, 0 + mTopMargin, mScaledImage.getWidth() + mLeftMargin, mScaledImage.getHeight() + mTopMargin);
	    mEditBox = new EditBox(mLeftMargin, mTopMargin, mImageBound);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			mPostX = x;
			mPostY = y;
			mCurrentAction = mEditBox.contains(event.getX(), event.getY());
		} else if(event.getAction() == MotionEvent.ACTION_MOVE) {
			float dx = mPostX - x;
			float dy = mPostY - y;
			
			if(mCurrentAction == ACTION_LIST.move) {
				mEditBox.move(dx, dy);
			} if(mCurrentAction == ACTION_LIST.anchor) {
				mEditBox.scale(dx, dy);
			}
			
			mPostX = x;
			mPostY = y;
		} else if(event.getAction() == MotionEvent.ACTION_UP) {
			mEditBox.setState(ACTION_LIST.none);
			mCurrentAction = null;
		}
		
		invalidate();

		return true;
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		destroyDrawingCache();
	}

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		drawPicture(canvas);
		drawEditBox(canvas);
	}
	
	private void drawEditBox(Canvas pCanvas) {
		mEditBox.draw(pCanvas, mEditBoxPaint);
	}
	
	private void drawPicture(Canvas pCanvas) {
	    pCanvas.drawBitmap(mScaledImage, null, mImageBound, mBitmapPaint);
	}
	
	public Bitmap crop() {
		int thumbX = mEditBox.getX() - mLeftMargin;
		int thumbY = mEditBox.getY() - mTopMargin;
		int thumbWidth = mEditBox.getWidtn();
		    
		if(thumbX + thumbWidth > mScaledImage.getWidth()) {
			thumbWidth -= thumbX + thumbWidth - mScaledImage.getWidth();
		}
		    
		if(thumbY + thumbWidth > mScaledImage.getHeight()) {
			thumbWidth -= thumbY + thumbWidth - mScaledImage.getHeight();
		}
		    
		Bitmap thumb = Bitmap.createBitmap(mScaledImage, thumbX, thumbY, thumbWidth, thumbWidth);

		return thumb;
	}
	
}
