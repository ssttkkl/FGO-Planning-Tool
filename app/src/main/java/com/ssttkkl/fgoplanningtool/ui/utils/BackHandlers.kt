package com.ssttkkl.fgoplanningtool.ui.utils

import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity

abstract class BackHandlerActivity : AppCompatActivity() {
    protected fun spreadBackPressedEvent(): Boolean {
        supportFragmentManager.fragments.forEach {
            if (it is BackHandlerFragment && it.onBackPressed())
                return true
        }
        return false
    }

    override fun onBackPressed() {
        if (!spreadBackPressedEvent())
            super.onBackPressed()
    }
}

abstract class BackHandlerFragment : Fragment() {
    open fun onBackPressed(): Boolean {
        childFragmentManager.fragments.forEach {
            if (it is BackHandlerFragment && it.onBackPressed())
                return true
        }
        return false
    }
}