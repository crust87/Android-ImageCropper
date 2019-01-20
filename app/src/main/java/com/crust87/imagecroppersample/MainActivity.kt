package com.crust87.imagecroppersample

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.crust87.imagecropper.ImageCropper
import kotlinx.android.synthetic.main.activity_main.*
import java.io.FileOutputStream
import java.io.IOException


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        imageCropper.setOnCropBoxChangedListener { cropBox ->
            textBoxInfo.text = "info: $cropBox"
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_CODE && checkStoragePermission()) {
            openImage()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_open -> {
                if (checkStoragePermission()) {
                    openImage()
                } else {
                    requestStoragePermission(PERMISSION_REQUEST_CODE)
                }

                return true
            }
            R.id.action_crop -> {
                cropImage()
                return true
            }
            R.id.action_set_circle -> {
                imageCropper.setBoxType(ImageCropper.CIRCLE_CROP_BOX)
                return true
            }
            R.id.action_set_rect -> {
                imageCropper.setBoxType(ImageCropper.RECT_CROP_BOX)
                return true
            }
            R.id.action_toggle_info -> {
                if (layoutInfo.visibility != View.VISIBLE) {
                    layoutInfo.visibility = View.VISIBLE
                } else {
                    layoutInfo.visibility = View.GONE
                }
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            val selectedImageUri = data.data

            imageCropper.setImage(selectedImageUri)
        }
    }

    fun openImage() {
        val lIntent = Intent(Intent.ACTION_PICK)
        lIntent.type = "image/*"
        lIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivityForResult(lIntent, IMAGE_REQUEST_CODE)
    }

    private fun cropImage() {
        val croppedImage = imageCropper.crop()

        if (croppedImage != null) {
            val filePath = Environment.getExternalStorageDirectory().absolutePath + "/" + System.currentTimeMillis() + ".png"
            var resultPath: String?

            try {
                val out = FileOutputStream(filePath)
                croppedImage.compress(Bitmap.CompressFormat.PNG, 40, out)
                Toast.makeText(this@MainActivity, "save as $filePath", Toast.LENGTH_LONG).show()
                resultPath = filePath
            } catch (e: IOException) {
                e.printStackTrace()
                resultPath = null
            }

            if (resultPath?.isNotEmpty() == true) {
                imageCropper.setImage(resultPath)
            } else {
                Toast.makeText(this@MainActivity, "There is no result!", Toast.LENGTH_LONG).show()
            }
        }
    }

    companion object {
        const val IMAGE_REQUEST_CODE = 1000
        const val PERMISSION_REQUEST_CODE = 100
    }
}
