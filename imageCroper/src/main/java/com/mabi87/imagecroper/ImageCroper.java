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
import android.content.res.TypedArray;
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
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class ImageCroper extends SurfaceView implements SurfaceHolder.Callback {
	// Constants
	private static final int DEFAULT_BOX_COLOR = Color.WHITE;

	// Components
	private Context mContext;
	private SurfaceHolder mHolder;
	private Bitmap mImage;
	private Bitmap mScaledImage;
	private Rect mImageBound;
	private Paint mImagePaint;
	private CropBox mCropBox;

	// Attributes
	private int mViewWidth;
	private int mViewHeight;
	private Uri mSelectedImage;
	private int mBoxColor = DEFAULT_BOX_COLOR;

	// Listener
	private OnCropBoxChangedListener mOnCropBoxChangedListener;

	// Constructor
	public ImageCroper(Context context) {
		super(context);
		mContext = context;

		initImageCroper();
	}

	public ImageCroper(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ImageCroper, 0, 0);
		mBoxColor = a.getColor(R.styleable.ImageCroper_box_color, DEFAULT_BOX_COLOR);

		initImageCroper();
	}

	public ImageCroper(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mContext = context;

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ImageCroper, defStyleAttr, 0);
		mBoxColor = a.getColor(R.styleable.ImageCroper_box_color, DEFAULT_BOX_COLOR);

		initImageCroper();
	}

	private void initImageCroper() {
		mHolder = getHolder();
		mHolder.addCallback(this);
	}

	public void setImage(Uri pSelectedImage) {
		mSelectedImage = pSelectedImage;

		openImage();
		invalidate();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		setWillNotDraw(false);
		mViewWidth = getWidth();
		mViewHeight = getHeight();

		openImage();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		mViewWidth = width;
		mViewHeight = height;
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		destroyDrawingCache();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(mCropBox != null) {
			boolean isChanged = mCropBox.processTouchEvent(event);

			if(isChanged) {
				if(mOnCropBoxChangedListener != null) {
					mOnCropBoxChangedListener.onCropBoxChange(mCropBox);
				}
			}

			invalidate();
		}

		return true;
	}

	private void openImage() {
		if(mViewWidth == 0 || mViewHeight == 0) {
			return;
		}

		if(mSelectedImage == null) {
			return;
		}

		InputStream imageStream = null;

		try {
			imageStream = mContext.getContentResolver().openInputStream(mSelectedImage);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		int lRotaion = 0;

		try {
			String path = getRealPathFromURI(mSelectedImage);
			ExifInterface exif = new ExifInterface(path);

			switch(Integer.parseInt(exif.getAttribute(ExifInterface.TAG_ORIENTATION))) {
				case 3:
					lRotaion = 180;
					break;
				case 6:
					lRotaion = 90;
					break;
				case 8:
					lRotaion = 270;
					break;
				default:
					lRotaion = 0;
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

		Matrix matrix = new Matrix();
		matrix.postRotate(lRotaion);

		mImage = BitmapFactory.decodeStream(imageStream, null, options);
		mImage = Bitmap.createBitmap(mImage, 0, 0, mImage.getWidth(), mImage.getHeight(), matrix, true);

		mImagePaint = new Paint();
		mImagePaint.setFilterBitmap(true);

		float scaleX;
		float scaleY;

		scaleX = (float) getWidth() / mImage.getWidth();
		scaleY = (float) getHeight() / mImage.getHeight();

		int newWidth;
		int newHeight;
		float lScale;

		if(scaleX > scaleY) {
			lScale = scaleY;
		} else {
			lScale = scaleX;
		}

		newWidth = (int) (mImage.getWidth() * lScale);
		newHeight = (int) (mImage.getHeight() * lScale);

		mScaledImage = Bitmap.createScaledBitmap(mImage, newWidth, newHeight, true);

		int lLeftMargin = (mViewWidth - mScaledImage.getWidth()) / 2;
		int lTopMargin = (mViewHeight - mScaledImage.getHeight()) / 2;

		mImageBound = new Rect(lLeftMargin, lTopMargin, mScaledImage.getWidth() + lLeftMargin, mScaledImage.getHeight() + lTopMargin);
		mCropBox = new CropBox(lLeftMargin, lTopMargin, mImageBound, lScale);
		mCropBox.setColor(mBoxColor);

		if(mOnCropBoxChangedListener != null) {
			mOnCropBoxChangedListener.onCropBoxChange(mCropBox);
		}
	}

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		drawPicture(canvas);
		drawCropBox(canvas);
	}
	
	private void drawCropBox(Canvas pCanvas) {
		if(mCropBox != null) {
			mCropBox.draw(pCanvas);
		}
	}
	
	private void drawPicture(Canvas pCanvas) {
		if(mScaledImage != null) {
			pCanvas.drawBitmap(mScaledImage, null, mImageBound, mImagePaint);
		}
	}

	public Bitmap crop() {
		int lCropX = mCropBox.getCropX();
		int lCropY = mCropBox.getCropY();
		int lCropWidth = mCropBox.getCropWidth();
		int lCropHeight = mCropBox.getCropHeight();

		if(lCropX + lCropWidth > mImage.getWidth()) {
			lCropWidth -= lCropX + lCropWidth - mImage.getWidth();
		}

		if(lCropY + lCropHeight > mImage.getHeight()) {
			lCropHeight -= lCropY + lCropHeight - mImage.getHeight();
		}

		return Bitmap.createBitmap(mImage, lCropX, lCropY, lCropWidth, lCropHeight);
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

	public void setOnCropBoxChangedListener(OnCropBoxChangedListener pOnCropBoxChangedListener) {
		mOnCropBoxChangedListener = pOnCropBoxChangedListener;
	}

	public interface OnCropBoxChangedListener {
		public abstract void onCropBoxChange(CropBox cropBox);
	}

	public void setBoxColor(int color) {
		mBoxColor = color;

		if(mCropBox != null) {
			mCropBox.setColor(mBoxColor);
		}
	}

	public void setBoxColor(String colorCode) {
		mBoxColor = Color.parseColor(colorCode);

		if(mCropBox != null) {
			mCropBox.setColor(mBoxColor);
		}
	}

}
