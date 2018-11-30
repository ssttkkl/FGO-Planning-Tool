package com.ssttkkl.fgoplanningtool.ui.utils

import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.ssttkkl.fgoplanningtool.R

fun AppCompatActivity.setStatusBarColor(color: Int) {
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.statusBarColor = color
}

fun AppCompatActivity.setStatusBarColor() {
    setStatusBarColor(ResourcesCompat.getColor(resources, R.color.colorPrimaryDark, null))
}