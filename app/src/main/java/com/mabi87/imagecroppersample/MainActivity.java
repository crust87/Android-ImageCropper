package com.mabi87.imagecroppersample;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mabi87.imagecropper.CropBox;
import com.mabi87.imagecropper.ImageCropper;
import com.mabi87.imagecropper.RectCropBox;

import java.io.FileOutputStream;
import java.io.IOException;


public class MainActivity extends ActionBarActivity {

    private static final int imageRequestCode = 1000;

    // Layout Components
    private ImageCropper mImageCropper;
    private TextView mTextCropX;
    private TextView mTextCropY;
    private TextView mTextCropWidth;
    private TextView mTextCropHeight;

    // Attributes
    private String mLastResultPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadGUI();
        bindEvent();
        init();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == imageRequestCode && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();

            mImageCropper.setImage(selectedImageUri);
        }
    }

    private void loadGUI() {
        setContentView(R.layout.activity_main);

        mImageCropper = (ImageCropper) findViewById(R.id.imageCropper);
        mTextCropX = (TextView) findViewById(R.id.textCropX);
        mTextCropY = (TextView) findViewById(R.id.textCropY);
        mTextCropWidth = (TextView) findViewById(R.id.textCropWidth);
        mTextCropHeight = (TextView) findViewById(R.id.textCropHeight);
    }

    private void bindEvent() {
        mImageCropper.setOnCropBoxChangedListener(new ImageCropper.OnCropBoxChangedListener() {
            @Override
            public void onCropBoxChange(CropBox cropBox) {
                mTextCropX.setText("crop x: " + cropBox.getCropX());
                mTextCropY.setText("crop y: " + cropBox.getCropY());
                mTextCropWidth.setText("crop width: " + cropBox.getCropWidth());
                mTextCropHeight.setText("crop height: " + cropBox.getCropHeight());
            }
        });
    }

    private void init() {
        mLastResultPath = null;
    }

    public void onButtonLoadClicked(View v) {
        Intent lIntent = new Intent(Intent.ACTION_PICK);
        lIntent.setType("image/*");
        lIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(lIntent, imageRequestCode);
    }

    public void onButtonCropClicked(View v) {
        if(mImageCropper != null) {
            Bitmap cropedImage = mImageCropper.crop();

            String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + System.currentTimeMillis() + ".png";

            try {
                FileOutputStream out = new FileOutputStream(filePath);
                cropedImage.compress(Bitmap.CompressFormat.PNG, 40, out);
                Toast.makeText(MainActivity.this, "save as " + filePath, Toast.LENGTH_LONG).show();
                mLastResultPath = filePath;
            } catch (IOException e) {
                e.printStackTrace();
                mLastResultPath = null;
            }
        }
    }

    public void onButtonShowClicked(View v) {
        if(!TextUtils.isEmpty(mLastResultPath)) {
            mImageCropper.setImage(mLastResultPath);
        } else {
            Toast.makeText(MainActivity.this, "There is no result!", Toast.LENGTH_LONG).show();
        }
    }

}
