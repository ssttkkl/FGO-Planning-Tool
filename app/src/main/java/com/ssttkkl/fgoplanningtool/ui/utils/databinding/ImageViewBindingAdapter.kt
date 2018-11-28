package com.ssttkkl.fgoplanningtool.ui.utils.databinding

import android.animation.AnimatorInflater
import android.animation.ValueAnimator
import android.graphics.drawable.RotateDrawable
import androidx.databinding.BindingAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.R
import java.io.File

object ImageViewBindingAdapter {
    @BindingAdapter("srcCompat")
    @JvmStatic
    fun setSrcCompat(imageView: ImageView, imgFile: File?) {
        Glide.with(imageView.context).load(imgFile).into(imageView)
    }

    @BindingAdapter("rotateDrawable")
    @JvmStatic
    fun setRotateDrawable(imageView: ImageView, oldValue: Boolean, newValue: Boolean) {
        if (oldValue != newValue) {
            val drawable = imageView.drawable as? RotateDrawable ?: return
            drawable.mutate()

            (AnimatorInflater.loadAnimator(MyApp.context, if (newValue) R.animator.rotate else R.animator.rotate_reversed) as ValueAnimator).apply {
                addUpdateListener { drawable.level = it.animatedValue as Int }
                start()
            }
        }
    }
}