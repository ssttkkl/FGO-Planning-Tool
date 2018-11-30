package com.ssttkkl.fgoplanningtool.ui.utils.databinding

import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingListener

object SpinnerBindingAdapter {
    @BindingAdapter("onItemSelected")
    @JvmStatic
    fun setOnSelectedItemChanged(spinner: Spinner, listener: InverseBindingListener) {
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                listener.onChange()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                listener.onChange()
            }
        }
    }
}