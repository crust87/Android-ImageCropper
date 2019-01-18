package com.crust87.imagecroppersample

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat

fun Activity.checkStoragePermission(): Boolean {
    val read = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
    val write = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)

    return read == PackageManager.PERMISSION_GRANTED && write == PackageManager.PERMISSION_GRANTED
}

fun Activity.requestStoragePermission(code: Int) {
    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), code)
}