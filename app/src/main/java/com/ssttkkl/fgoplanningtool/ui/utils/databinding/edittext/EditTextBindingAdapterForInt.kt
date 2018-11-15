package com.ssttkkl.fgoplanningtool.ui.utils.databinding.edittext

import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import android.widget.EditText

object EditTextBindingAdapterForInt {
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