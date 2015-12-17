package com.crust87.imagecroppersample;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.crust87.imagecropper.cropbox.CropBox;
import com.crust87.imagecropper.ImageCropper;

import java.io.FileOutputStream;
import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    private static final int imageRequestCode = 1000;

    // Layout Components
    private ImageCropper mImageCropper;
    private View mLayoutInfo;
    private TextView mTextCropX;
    private TextView mTextCropY;
    private TextView mTextCropWidth;
    private TextView mTextCropHeight;

    // Attributes
    private String mLastResultPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setElevation(0);

        loadGUI();
        bindEvent();
        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_open:
                openImage();
                return true;
            case R.id.action_crop:
                cropImage();
                return true;
            case R.id.action_set_circle:
                mImageCropper.setBoxType(ImageCropper.CIRCLE_CROP_BOX);
                return true;
            case R.id.action_set_rect:
                mImageCropper.setBoxType(ImageCropper.RECT_CROP_BOX);
                return true;
            case R.id.action_toggle_info:
                if(mLayoutInfo.getVisibility() != View.VISIBLE) {
                    mLayoutInfo.setVisibility(View.VISIBLE);
                } else {
                    mLayoutInfo.setVisibility(View.GONE);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
        mLayoutInfo = findViewById(R.id.layoutInfo);
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

    public void openImage() {
        Intent lIntent = new Intent(Intent.ACTION_PICK);
        lIntent.setType("image/*");
        lIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(lIntent, imageRequestCode);
    }

    public void cropImage() {
        if (mImageCropper != null) {
            Bitmap cropedImage = mImageCropper.crop();

            if (cropedImage != null) {
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

                if (!TextUtils.isEmpty(mLastResultPath)) {
                    mImageCropper.setImage(mLastResultPath);
                } else {
                    Toast.makeText(MainActivity.this, "There is no result!", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
