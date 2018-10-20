package com.ssttkkl.fgoplanningtool.ui.utils.databinding

import android.databinding.BindingAdapter
import android.databinding.InverseBindingAdapter
import android.widget.EditText

object EditTextBindingAdapter {
    @BindingAdapter("android:text")
    @JvmStatic
    fun setText(editText: EditText, text: Int?) {
        val value = text?.toString() ?: ""
        if (editText.text.toString() != value)
            editText.setText(value)
    }

    @InverseBindingAdapter(attribute = "android:text", event = "android:textAttrChanged")
    @JvmStatic
    fun getText(editText: EditText): Int? {
        return editText.text.toString().toIntOrNull()
    }
}