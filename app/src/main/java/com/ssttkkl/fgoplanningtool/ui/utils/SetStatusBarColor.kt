package com.ssttkkl.fgoplanningtool.ui.utils

import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import com.ssttkkl.fgoplanningtool.R

fun AppCompatActivity.setStatusBarColor(color: Int) {
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.statusBarColor = color
}

fun AppCompatActivity.setStatusBarColor() {
    setStatusBarColor(ResourcesCompat.getColor(resources, R.color.colorPrimaryDark, null))
}