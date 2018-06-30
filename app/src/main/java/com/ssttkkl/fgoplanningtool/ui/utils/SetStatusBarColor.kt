package com.ssttkkl.fgoplanningtool.ui.utils

import android.os.Build
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import com.ssttkkl.fgoplanningtool.R

fun AppCompatActivity.setStatusBarColor(color: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = color
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

    }
}

fun AppCompatActivity.setStatusBarColor() {
    setStatusBarColor(ResourcesCompat.getColor(resources, R.color.colorPrimaryDark, null))
}