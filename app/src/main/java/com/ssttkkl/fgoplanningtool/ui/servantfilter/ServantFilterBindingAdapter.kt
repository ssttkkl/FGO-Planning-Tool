package com.ssttkkl.fgoplanningtool.ui.servantfilter

import android.animation.AnimatorInflater
import android.animation.ValueAnimator
import android.graphics.drawable.RotateDrawable
import android.widget.Spinner
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.R

object ServantFilterBindingAdapter {
    @BindingAdapter("rotateDrawableEnd")
    @JvmStatic
    fun setRotateDrawableEnd(textView: TextView, oldValue: Boolean, newValue: Boolean) {
        if (oldValue != newValue) {
            val drawable = textView.compoundDrawablesRelative[2] as? RotateDrawable ?: return
            drawable.mutate()

            (AnimatorInflater.loadAnimator(MyApp.context, if (newValue) R.animator.rotate else R.animator.rotate_reversed) as ValueAnimator).apply {
                addUpdateListener { drawable.level = it.animatedValue as Int }
                start()
            }
        }
    }

    @BindingAdapter("selection")
    @JvmStatic
    fun setSelection(spinner: Spinner, selection: ItemFilterMode) {
        spinner.setSelection(selection.ordinal)
    }

    @InverseBindingAdapter(attribute = "selection", event = "onItemSelected")
    @JvmStatic
    fun getSelection(spinner: Spinner): ItemFilterMode {
        return ItemFilterMode.values()[spinner.selectedItemPosition]
    }
}