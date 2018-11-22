package com.ssttkkl.fgoplanningtool.ui.utils

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.appcompat.app.AppCompatActivity

fun AppCompatActivity.replaceFragment(@IdRes layoutResID: Int, fragment: Fragment, tag: String?) {
    supportFragmentManager.beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .replace(layoutResID, fragment, tag)
            .commit()
    supportFragmentManager.executePendingTransactions()
}