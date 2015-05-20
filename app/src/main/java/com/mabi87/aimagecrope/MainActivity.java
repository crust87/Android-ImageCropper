package com.mabi87.aimagecrope;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.FrameLayout;

import com.mabi87.imagecroper.ImageCroper;

import java.io.FileOutputStream;
import java.io.IOException;


public class MainActivity extends ActionBarActivity {

    // Components
    private FrameLayout mContainerImageCroper;
    private ImageCroper mImageCroper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadGUI();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1000 && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();

            mImageCroper = new ImageCroper(getApplicationContext(), selectedImageUri);
            mContainerImageCroper.addView(mImageCroper);
        } else {
            finish();
        }
    }

    protected void loadGUI() {
        setContentView(R.layout.activity_main);

        mContainerImageCroper = (FrameLayout) findViewById(R.id.containerImageCroper);
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
