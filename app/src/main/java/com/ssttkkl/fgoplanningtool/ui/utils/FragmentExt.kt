package com.ssttkkl.fgoplanningtool.ui.utils

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction

fun Fragment.replaceFragment(@IdRes layoutResID: Int, fragment: Fragment, tag: String?) {
    childFragmentManager.beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .replace(layoutResID, fragment, tag)
            .commit()
    childFragmentManager.executePendingTransactions()
}