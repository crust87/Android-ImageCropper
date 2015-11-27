package com.mabi87.imagecropper.cropbox;

import android.graphics.Rect;

import com.mabi87.imagecropper.ImageCropper;
import com.mabi87.imagecropper.cropbox.CropBox;

/**
 * Created by test2 on 2015. 11. 27..
 */
public class CropBoxFactory {
    public static CropBox create(int boxType, float x, float y, Rect bound, float scale, int lineWidth, int anchorSize) {
        switch(boxType) {
            case ImageCropper.CIRCLE_CROP_BOX:
                return new CircleCropBox(x, y, bound, scale, lineWidth, anchorSize);
            case ImageCropper.RECT_CROP_BOX:
                return new RectCropBox(x, y, bound, scale, lineWidth, anchorSize);
            default:
                return new CircleCropBox(x, y, bound, scale, lineWidth, anchorSize);
        }
    }
}
