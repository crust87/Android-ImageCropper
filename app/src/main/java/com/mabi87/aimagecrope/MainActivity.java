package com.mabi87.aimagecrope;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.FrameLayout;

import com.mabi87.imagecroper.ImageCroper;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class MainActivity extends ActionBarActivity {

    // Components
    private FrameLayout mContainerImageCroper;
    private ImageCroper mImageCroper;

    // Attributes
    private String imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadGUI();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1000 && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            int lRotate = 0;
            InputStream imageStream = null;

            try {
                imageStream = getContentResolver().openInputStream(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            try {
                imageUri = getRealPathFromURI(getApplicationContext(), selectedImage);
                ExifInterface exif = new ExifInterface(imageUri);

                switch(Integer.parseInt(exif.getAttribute(ExifInterface.TAG_ORIENTATION))) {
                    case 3:
                        lRotate = 180;
                        break;
                    case 6:
                        lRotate = 90;
                        break;
                    case 8:
                        lRotate = 270;
                        break;
                    default:
                        lRotate = 0;
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
            Bitmap selected = BitmapFactory.decodeStream(imageStream, null, options);

            mImageCroper = new ImageCroper(getApplicationContext(), selected, lRotate);
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
            Bitmap thumb = mImageCroper.crop();

            try {
                FileOutputStream out = new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath() + "/thumb.png");
                thumb.compress(Bitmap.CompressFormat.PNG, 40, out);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
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
