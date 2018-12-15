package com.ssttkkl.fgoplanningtool.ui.planlist.costitemlist

import android.animation.AnimatorInflater
import android.animation.ValueAnimator
import android.graphics.drawable.RotateDrawable
import android.widget.Button
import androidx.databinding.BindingAdapter
import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.R

object CostItemListBindingAdapter {
    @BindingAdapter("rotateDrawableEnd")
    @JvmStatic
    fun setRotateDrawableEnd(button: Button, oldValue: Boolean, newValue: Boolean) {
        if (oldValue != newValue) {
            val drawable = button.compoundDrawablesRelative[2] as? RotateDrawable ?: return
            drawable.mutate()

            (AnimatorInflater.loadAnimator(MyApp.context, if (newValue) R.animator.rotate else R.animator.rotate_reversed) as ValueAnimator).apply {
                addUpdateListener { drawable.level = it.animatedValue as Int }
                start()
            }
        }
    }
}