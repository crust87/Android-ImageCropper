package com.mabi87.imagecropersample;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.TextView;

import com.mabi87.imagecroper.CropBox;
import com.mabi87.imagecroper.ImageCroper;

import java.io.FileOutputStream;
import java.io.IOException;


public class MainActivity extends ActionBarActivity {

    // Layout Components
    private ImageCroper mImageCroper;
    private TextView mTextCropX;
    private TextView mTextCropY;
    private TextView mTextCropWidth;
    private TextView mTextCropHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadGUI();
        bindEvent();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1000 && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();

            mImageCroper.setImage(selectedImageUri);
        }
    }

    private void loadGUI() {
        setContentView(R.layout.activity_main);

        mImageCroper = (ImageCroper) findViewById(R.id.imageCroper);
        mTextCropX = (TextView) findViewById(R.id.textCropX);
        mTextCropY = (TextView) findViewById(R.id.textCropY);
        mTextCropWidth = (TextView) findViewById(R.id.textCropWidth);
        mTextCropHeight = (TextView) findViewById(R.id.textCropHeight);
    }

    private void bindEvent() {
        mImageCroper.setOnCropBoxChangedListener(new ImageCroper.OnCropBoxChangedListener() {
            @Override
            public void onCropBoxChange(CropBox cropBox) {
                mTextCropX.setText("crop x: " + cropBox.getCropX());
                mTextCropY.setText("crop y: " + cropBox.getCropY());
                mTextCropWidth.setText("crop width: " + cropBox.getCropWidth());
                mTextCropHeight.setText("crop height: " + cropBox.getCropHeight());
            }
        });
    }

    public void onButtonLoadClicked(View v) {
        Intent lIntent = new Intent(Intent.ACTION_PICK);
        lIntent.setType("image/*");
        lIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(lIntent, 1000);
    }

    public void onButtonCropClicked(View v) {
        if(mImageCroper != null) {
            Bitmap cropedImage = mImageCroper.crop();

            try {
                FileOutputStream out = new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath() + "/thumb.png");
                cropedImage.compress(Bitmap.CompressFormat.PNG, 40, out);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
