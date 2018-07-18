package com.ssttkkl.fgoplanningtool.ui.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.support.annotation.DrawableRes

fun Context.getScaledDrawable(@DrawableRes drawableResID: Int, dstWidth: Int, dstHeight: Int): Drawable {
    val bitmap = BitmapFactory.decodeResource(resources, drawableResID)
    val scaledBitmap = Bitmap.createScaledBitmap(bitmap, dstWidth, dstHeight, false)
    return BitmapDrawable(resources, scaledBitmap)
}