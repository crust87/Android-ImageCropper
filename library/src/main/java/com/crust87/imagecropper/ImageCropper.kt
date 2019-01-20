/*
 * ImageCropper
 * https://github.com/crust87/Android-ImageCropper
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

package com.crust87.imagecropper

import android.content.Context
import android.database.Cursor
import android.graphics.*
import android.media.ExifInterface
import android.net.Uri
import android.provider.MediaStore
import android.text.TextUtils
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.crust87.imagecropper.cropbox.CropBox
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException

class ImageCropper : SurfaceView, SurfaceHolder.Callback {

    private val cropBoxBuilder: CropBox.CropBoxBuilder = CropBox.CropBoxBuilder()
    private var originImage: Bitmap? = null
    private var displayImage: Bitmap? = null
    private val imageBound = Rect()
    private var imagePaint = Paint().apply {
        isFilterBitmap = true
    }
    private var cropBox: CropBox? = null

    /*
    Attributes
     */
    private var viewWidth = 0
    private var viewHeight = 0
    private var imagePath: String? = null
    private var scale = 0f

    var isOpened: Boolean = false
        private set

    private var onCropBoxChangedListener: OnCropBoxChangedListener? = null

    /*
    Constructors
     */
    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs, defStyleAttr)
    }

    private fun init() {
        cropBoxBuilder.setBoxColor(resources.getColor(R.color.default_box_color))
                .setBoxType(DEFAULT_BOX_TYPE)
                .setLineWidth(resources.getDimensionPixelSize(R.dimen.default_line_width))
                .setAnchorSize(resources.getDimensionPixelSize(R.dimen.default_anchor_size))

        holder.addCallback(this)
        isOpened = false
    }

    private fun init(context: Context, attrs: AttributeSet, defStyleAttr: Int) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ImageCropper, defStyleAttr, 0)

        val boxColor = typedArray.getColor(R.styleable.ImageCropper_box_color, resources.getColor(R.color.default_box_color))
        val boxType = when (typedArray.getString(R.styleable.ImageCropper_box_type)) {
            "circle" -> CIRCLE_CROP_BOX
            "rect" -> RECT_CROP_BOX
            else -> CIRCLE_CROP_BOX
        }
        val defaultLineWidth = resources.getDimensionPixelSize(R.dimen.default_line_width)
        val defaultAnchorSize = resources.getDimensionPixelSize(R.dimen.default_anchor_size)
        val lineWidth = typedArray.getLayoutDimension(R.styleable.ImageCropper_line_width, defaultLineWidth)
        val anchorSize = typedArray.getLayoutDimension(R.styleable.ImageCropper_anchor_size, defaultAnchorSize)

        cropBoxBuilder.setBoxColor(boxColor)
                .setBoxType(boxType)
                .setLineWidth(lineWidth)
                .setAnchorSize(anchorSize)

        holder.addCallback(this)
        isOpened = false

        typedArray.recycle()
    }

    /**
     *
     * @param uri it will convert into real path
     */
    fun setImage(uri: Uri) {
        setImage(getRealPathFromURI(uri))
    }

    /**
     * box_color
     * @param file it will convert into absolute path
     */
    fun setImage(file: File) {
        setImage(file.absolutePath)
    }

    /**
     *
     * @param path path of image to draw on view
     */
    fun setImage(path: String) {
        imagePath = path

        openImage()
        invalidate()
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        setWillNotDraw(false)
        viewWidth = width
        viewHeight = height

        openImage()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        viewWidth = width
        viewHeight = height
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        cropBox?.let { cropBox ->
            val isChanged = cropBox.processTouchEvent(event)

            if (isChanged) {
                onCropBoxChangedListener?.onCropBoxChange(cropBox)
            }

            invalidate()
        }

        return true
    }

    private fun openImage() {
        if (viewWidth == 0 || viewHeight == 0) {
            return
        }

        if (TextUtils.isEmpty(imagePath)) {
            return
        }

        try {
            val imageStream = FileInputStream(imagePath)
            val exif = ExifInterface(imagePath)
            val rotation = when (Integer.parseInt(exif.getAttribute(ExifInterface.TAG_ORIENTATION))) {
                3 -> 180F
                6 -> 90F
                8 -> 270F
                else -> 0F
            }

            val options = BitmapFactory.Options().apply {
                inPreferredConfig = Bitmap.Config.RGB_565
            }
            val decodedImage = BitmapFactory.decodeStream(imageStream, null, options)?.run {
                val matrix = Matrix().apply {
                    postRotate(rotation)
                }

                Bitmap.createBitmap(this, 0, 0, this.width, this.height, matrix, true)
            }

            decodedImage ?: return

            val scaleX = width.toFloat() / decodedImage.width
            val scaleY = height.toFloat() / decodedImage.height

            scale = when (scaleX > scaleY) {
                true -> scaleY
                false -> scaleX
            }
            val newWidth = (decodedImage.width * scale).toInt()
            val newHeight = (decodedImage.height * scale).toInt()

            val scaledImage = Bitmap.createScaledBitmap(decodedImage, newWidth, newHeight, true)

            scaledImage ?: return

            val leftMargin = (viewWidth - scaledImage.width) / 2
            val topMargin = (viewHeight - scaledImage.height) / 2

            imageBound.left = leftMargin
            imageBound.top = topMargin
            imageBound.right = scaledImage.width + leftMargin
            imageBound.bottom = scaledImage.height + topMargin

            cropBoxBuilder.setLeftMargin(leftMargin.toFloat())
                    .setTopMargin(topMargin.toFloat())
                    .setBound(imageBound)

            cropBox = cropBoxBuilder.createCropBox(context).apply {
                onCropBoxChangedListener?.onCropBoxChange(this)
            }

            originImage = decodedImage
            displayImage = scaledImage
            isOpened = true
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawPicture(canvas)
        drawCropBox(canvas)
    }

    private fun drawCropBox(canvas: Canvas) {
        cropBox?.draw(canvas)
    }

    private fun drawPicture(canvas: Canvas) {
        displayImage?.let { displayImage ->
            canvas.drawBitmap(displayImage, null, imageBound, imagePaint)
        }
    }

    /**
     * crop image
     *
     * @return Bitmap of cropped image
     */
    fun crop(): Bitmap? {
        if (!isOpened) {
            return null
        }

        val originImage = originImage
        val cropBox = cropBox

        originImage ?: return null
        cropBox ?: return null


        val x = (cropBox.x / scale).toInt()
        val y = (cropBox.y / scale).toInt()
        var width = (cropBox.width / scale).toInt()
        var height = (cropBox.height / scale).toInt()

        if (x + width > originImage.width) {
            width -= x + width - originImage.width
        }

        if (y + height > originImage.height) {
            height -= y + height - originImage.height
        }

        return Bitmap.createBitmap(originImage, x, y, width, height)
    }

    fun setOnCropBoxChangedListener(l: OnCropBoxChangedListener) {
        onCropBoxChangedListener = l
    }

    fun setOnCropBoxChangedListener(l: (CropBox) -> Unit) {
        onCropBoxChangedListener = object : OnCropBoxChangedListener {
            override fun onCropBoxChange(cropBox: CropBox) {
                l(cropBox)
            }
        }
    }

    interface OnCropBoxChangedListener {
        fun onCropBoxChange(cropBox: CropBox)
    }

    fun setBoxType(boxType: Int) {
        cropBoxBuilder.setBoxType(boxType)

        if (isOpened) {
            cropBox = cropBoxBuilder.createCropBox(context)
            invalidate()
        }
    }

    private fun getRealPathFromURI(contentUri: Uri): String {
        var cursor: Cursor? = null
        try {
            val projection = arrayOf(MediaStore.Images.Media.DATA)
            cursor = context.contentResolver.query(contentUri, projection, null, null, null)
            val columnIndex = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            return cursor.getString(columnIndex)
        } finally {
            cursor?.close()
        }
    }

    companion object {
        const val CIRCLE_CROP_BOX = 0
        const val RECT_CROP_BOX = 1
        const  val DEFAULT_BOX_TYPE = CIRCLE_CROP_BOX
    }
}
