/*
 * ImageCropper
 * https://github.com/mabi87/Android-ImageCropper
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

package com.mabi87.imagecropper;

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
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.mabi87.imagecropper.cropbox.CircleCropBox;
import com.mabi87.imagecropper.cropbox.CropBox;
import com.mabi87.imagecropper.cropbox.CropBoxFactory;
import com.mabi87.imagecropper.cropbox.RectCropBox;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class ImageCropper extends SurfaceView implements SurfaceHolder.Callback {
	// Constants
	public static final int CIRCLE_CROP_BOX = 0;
	public static final int RECT_CROP_BOX = 1;

	private static final int DEFAULT_BOX_COLOR = Color.WHITE;
	private static final int DEFAULT_BOX_TYPE = CIRCLE_CROP_BOX;

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
	private String mImagePath;
	private int mBoxColor = DEFAULT_BOX_COLOR;
	private int mBoxType = DEFAULT_BOX_TYPE;
	private int mLineWidth;
	private int mAnchorSize;
	private boolean isImageOpen;

	// Listener
	private OnCropBoxChangedListener mOnCropBoxChangedListener;

	// Constructor
	public ImageCropper(Context context) {
		super(context);
		mContext = context;

		initImageCropper();
	}

	public ImageCropper(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;

		initAttributes(context, attrs, 0);
		initImageCropper();
	}

	public ImageCropper(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mContext = context;

		initAttributes(context, attrs, defStyleAttr);
		initImageCropper();
	}

	private void initAttributes(Context context, AttributeSet attrs, int defStyleAttr) {
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ImageCropper, defStyleAttr, 0);

		mBoxColor = typedArray.getColor(R.styleable.ImageCropper_box_color, DEFAULT_BOX_COLOR);

		String lBoxType = typedArray.getString(R.styleable.ImageCropper_box_type);
		if(TextUtils.equals(lBoxType, "circle")) {
			mBoxType = CIRCLE_CROP_BOX;
		} else if(TextUtils.equals(lBoxType, "rect")) {
			mBoxType = RECT_CROP_BOX;
		} else {
			mBoxType = DEFAULT_BOX_TYPE;
		}

		int defaultLineWidth = getResources().getDimensionPixelSize(R.dimen.default_line_width);
		mLineWidth = typedArray.getLayoutDimension(R.styleable.ImageCropper_line_width, defaultLineWidth);

		int defaultAnchorSize = getResources().getDimensionPixelSize(R.dimen.default_anchor_size);
		mAnchorSize = typedArray.getLayoutDimension(R.styleable.ImageCropper_anchor_size, defaultAnchorSize);
	}

	private void initImageCropper() {
		mHolder = getHolder();
		mHolder.addCallback(this);
		isImageOpen = false;
	}

	/**
	 *
	 * @param imageUri it will convert into real path
     */
	public void setImage(Uri imageUri) {
		setImage(getRealPathFromURI(imageUri));
	}

	/**
	 *
	 * @param imageFile it will convert into absolute path
     */
	public void setImage(File imageFile) {
		setImage(imageFile.getAbsolutePath());
	}

	/**
	 *
	 * @param imagePath path of image to draw on view
     */
	public void setImage(String imagePath) {
		mImagePath = imagePath;

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

		if(TextUtils.isEmpty(mImagePath)) {
			return;
		}

		InputStream imageStream = null;

		try {
			imageStream = new FileInputStream(mImagePath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		// check rotation
		int lRotaion = 0;
		try {
			ExifInterface exif = new ExifInterface(mImagePath);

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

		mCropBox = CropBoxFactory.create(mBoxType, lLeftMargin, lTopMargin, mImageBound, lScale, mLineWidth, mAnchorSize);
		mCropBox.setColor(mBoxColor);

		if(mOnCropBoxChangedListener != null) {
			mOnCropBoxChangedListener.onCropBoxChange(mCropBox);
		}

		isImageOpen = true;
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

	/**
	 * crop image
	 *
	 * @return Bitmap of cropped image
     */
	public Bitmap crop() {
		if(!isImageOpen) {
			return null;
		}

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

	public int getBoxType() {
		return mBoxType;
	}

	public void setBoxType(int boxType) {
		mBoxType = boxType;

		if(isImageOpen) {
			openImage();
			invalidate();
		}
	}

	public int getLineWidth() {
		return mLineWidth;
	}

	public void setLineWidth(int lineWidth) {
		mLineWidth = lineWidth;
	}

	public boolean isImageOpen() {
		return isImageOpen;
	}

	public void setImageOpen(boolean imageOpen) {
		isImageOpen = imageOpen;
	}
}
