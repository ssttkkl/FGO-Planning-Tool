package com.ssttkkl.fgoplanningtool.ui.utils.databinding.edittext

import android.databinding.BindingAdapter
import android.databinding.InverseBindingAdapter
import android.widget.EditText

object EditTextBindingAdapterForLong {
    @BindingAdapter("android:text")
    @JvmStatic
    fun setText(editText: EditText, text: Long?) {
        val value = text?.toString() ?: ""
        if (editText.text.toString() != value)
            editText.setText(value)
    }

    @InverseBindingAdapter(attribute = "android:text", event = "android:textAttrChanged")
    @JvmStatic
    fun getText(editText: EditText): Long? {
        return editText.text.toString().toLongOrNull()
    }
}