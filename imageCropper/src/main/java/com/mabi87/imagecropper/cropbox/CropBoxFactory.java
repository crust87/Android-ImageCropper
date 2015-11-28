package com.mabi87.imagecropper.cropbox;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;

import com.mabi87.imagecropper.ImageCropper;

public class CropBoxFactory {
    public static CropBox create(Context context, int boxType, float leftMargin, float topMargin, Rect imageBound, float scale, int boxColor, int lineWidth, int anchorSize) {
        CropBox cropBox;
        switch(boxType) {
            case ImageCropper.CIRCLE_CROP_BOX:
                cropBox = new CircleCropBox(context);
                break;
            case ImageCropper.RECT_CROP_BOX:
                cropBox = new RectCropBox(context);
                break;
            default:
                cropBox = new CircleCropBox(context);
                break;
        }

        cropBox.setAttributes(leftMargin, topMargin, imageBound, scale, boxColor, lineWidth, anchorSize);
        cropBox.init();
        return cropBox;
    }
}
