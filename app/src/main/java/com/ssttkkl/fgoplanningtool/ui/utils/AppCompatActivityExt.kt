package com.ssttkkl.fgoplanningtool.ui.utils

import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity

fun AppCompatActivity.replaceFragment(@IdRes layoutResID: Int, fragment: Fragment, tag: String?) {
    supportFragmentManager.beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .replace(layoutResID, fragment, tag)
            .commit()
    supportFragmentManager.executePendingTransactions()
}