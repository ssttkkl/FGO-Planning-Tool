package com.ssttkkl.fgoplanningtool.ui.utils.databinding

import androidx.databinding.BindingAdapter
import android.widget.ImageView
import com.bumptech.glide.Glide
import java.io.File

object ImageViewBindingAdapter {
    @BindingAdapter("srcCompat")
    @JvmStatic
    fun setSrcCompat(imageView: ImageView, imgFile: File?) {
        Glide.with(imageView.context).load(imgFile).into(imageView)
    }
}