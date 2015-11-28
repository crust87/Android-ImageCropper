# Android-ImageCropper
Image crop view for android

![](./Screenshot1.png) ![](./Screenshot2.png)

see
https://play.google.com/store/apps/details?id=com.mabi87.imagecropper

## Example

create view and add view
```java
mImageCropper = new ImageCropper(getApplicationContext());
mContainerImageCropper.addView(mImageCropper);
```

or append your layout xml
```xml
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:imageCropper="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <com.mabi87.imagecropper.ImageCropper
        android:id="@+id/imageCropper"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        imageCropper:box_color="#ffffff"
        imageCropper:box_type="circle"
        imageCropper:line_width="2dp"
        imageCropper:anchor_size="20dp" />

</FrameLayout>
```

and set image Uri
```java
mImageCropper.setImage(imageUri);
```

and crop
```java
Bitmap cropedImage = mImageCropper.crop();
```

you can set listener to crop box position and width change
```java
mImageCropper.setOnCropBoxChangedListener(new ImageCropper.OnCropBoxChangedListener() {
        @Override
        public void onCropBoxChange(CropBox cropBox) {
                mTextCropX.setText("crop x: " + cropBox.getCropX());
                mTextCropY.setText("crop y: " + cropBox.getCropY());
                mTextCropWidth.setText("crop width: " + cropBox.getCropWidth());
                mTextCropHeight.setText("crop height: " + cropBox.getCropHeight());
        }
});
```
<br />
Icon is designed by Freepik
http://www.freepik.com/

## Licence
Copyright 2015 Mabi

Licensed under the Apache License, Version 2.0 (the "License");<br/>
you may not use this work except in compliance with the License.<br/>
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software<br/>
distributed under the License is distributed on an "AS IS" BASIS,<br/>
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.<br/>
See the License for the specific language governing permissions and<br/>
limitations under the License.
