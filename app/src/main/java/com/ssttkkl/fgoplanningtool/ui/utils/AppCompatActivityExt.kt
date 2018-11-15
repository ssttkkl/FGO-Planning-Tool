package com.ssttkkl.fgoplanningtool.ui.utils

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.appcompat.app.AppCompatActivity

fun AppCompatActivity.replaceFragment(@IdRes layoutResID: Int, fragment: androidx.fragment.app.Fragment, tag: String?) {
    supportFragmentManager.beginTransaction()
            .setTransition(androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .replace(layoutResID, fragment, tag)
            .commit()
    supportFragmentManager.executePendingTransactions()
}