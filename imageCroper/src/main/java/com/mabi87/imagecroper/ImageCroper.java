/*
 * ImageCroper
 * https://github.com/mabi87/Android-ImageCroper
 *
 * Mabi
 * crust87@gmail.com
 * last modify 2015-05-21
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
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class ImageCroper extends SurfaceView implements SurfaceHolder.Callback {
	public static enum ACTION_LIST{anchor, move, none}
	
	private Context mContext;
	private SurfaceHolder mHolder;
	
	private Bitmap mImage;
	private int mAngle;
	private Bitmap mScaledImage;
	private Rect mImageBound;
	
	
	private Paint mBitmapPaint;
	private Paint mCropBoxPaint;
	
	private CropBox mCropBox;
	
	private int mViewWidth;
	private int mViewHeight;
	
	private int mLeftMargin;
	private int mTopMargin;
	
	private ACTION_LIST mCurrentAction;
	
	private float mPostX;
	private float mPostY;

	public ImageCroper(Context pContext, Uri pSelectedImage) {
		super(pContext);
		mContext = pContext;

		InputStream imageStream = null;

		try {
			imageStream = mContext.getContentResolver().openInputStream(pSelectedImage);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		try {
			String path = getRealPathFromURI(pSelectedImage);
			ExifInterface exif = new ExifInterface(path);

			switch(Integer.parseInt(exif.getAttribute(ExifInterface.TAG_ORIENTATION))) {
				case 3:
					mAngle = 180;
					break;
				case 6:
					mAngle = 90;
					break;
				case 8:
					mAngle = 270;
					break;
				default:
					mAngle = 0;
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.RGB_565;

		mImage = BitmapFactory.decodeStream(imageStream, null, options);

		mHolder = getHolder();
		mHolder.addCallback(this);

		mBitmapPaint = new Paint();
		mBitmapPaint.setFilterBitmap(true);

		mCropBoxPaint = new Paint();
		mCropBoxPaint.setColor(Color.parseColor("#ffffff"));
		mCropBoxPaint.setAntiAlias(true);
		mCropBoxPaint.setStrokeWidth(5);
		mCropBoxPaint.setStyle(Paint.Style.STROKE);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		setWillNotDraw(false);
		mViewWidth = getWidth();
		mViewHeight = getHeight();
		
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
	    
	    mLeftMargin = (mViewWidth - mScaledImage.getWidth()) / 2;
	    mTopMargin = (mViewHeight - mScaledImage.getHeight()) / 2;
	    
	    mImageBound = new Rect(0 + mLeftMargin, 0 + mTopMargin, mScaledImage.getWidth() + mLeftMargin, mScaledImage.getHeight() + mTopMargin);
		mCropBox = new CropBox(mLeftMargin, mTopMargin, mImageBound);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			mPostX = x;
			mPostY = y;
			mCurrentAction = mCropBox.contains(event.getX(), event.getY());
		} else if(event.getAction() == MotionEvent.ACTION_MOVE) {
			float dx = mPostX - x;
			float dy = mPostY - y;
			
			if(mCurrentAction == ACTION_LIST.move) {
				mCropBox.move(dx, dy);
			} if(mCurrentAction == ACTION_LIST.anchor) {
				mCropBox.scale(dx, dy);
			}
			
			mPostX = x;
			mPostY = y;
		} else if(event.getAction() == MotionEvent.ACTION_UP) {
			mCropBox.setState(ACTION_LIST.none);
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
		drawCropBox(canvas);
	}
	
	private void drawCropBox(Canvas pCanvas) {
		mCropBox.draw(pCanvas, mCropBoxPaint);
	}
	
	private void drawPicture(Canvas pCanvas) {
	    pCanvas.drawBitmap(mScaledImage, null, mImageBound, mBitmapPaint);
	}
	
//	public Bitmap crop() {
//		float scale = (float) mScaledImage.getWidth() / mImage.getWidth();
//
//		int thumbX = (int) ((mEditBox.getX() - mLeftMargin) / scale);
//		int thumbY = (int) ((mEditBox.getY() - mTopMargin) / scale);
//		int thumbWidth = (int) (mEditBox.getWidtn() / scale);
//
//		if(thumbX + thumbWidth > mImage.getWidth()) {
//			thumbWidth -= thumbX + thumbWidth - mImage.getWidth();
//		}
//
//		if(thumbY + thumbWidth > mImage.getHeight()) {
//			thumbWidth -= thumbY + thumbWidth - mImage.getHeight();
//		}
//
//		return Bitmap.createBitmap(mImage, thumbX, thumbY, thumbWidth, thumbWidth);
//	}

	public Bitmap crop() {
		int thumbX = (mCropBox.getX() - mLeftMargin);
		int thumbY = (mCropBox.getY() - mTopMargin);
		int thumbWidth = mCropBox.getWidtn();

		if(thumbX + thumbWidth > mScaledImage.getWidth()) {
			thumbWidth -= thumbX + thumbWidth - mScaledImage.getWidth();
		}

		if(thumbY + thumbWidth > mScaledImage.getHeight()) {
			thumbWidth -= thumbY + thumbWidth - mScaledImage.getHeight();
		}

		return Bitmap.createBitmap(mScaledImage, thumbX, thumbY, thumbWidth, thumbWidth);
	}

	private String getRealPathFromURI(Uri contentUri) {
		Cursor cursor = null;
		try {
			String[] proj = { MediaStore.Images.Media.DATA };
			cursor = mContext.getContentResolver().query(contentUri, proj, null, null, null);
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	
}
