package com.ssttkkl.fgoplanningtool.ui.utils

import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity

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

abstract class BackHandlerFragment : androidx.fragment.app.Fragment() {
    open fun onBackPressed(): Boolean {
        childFragmentManager.fragments.forEach {
            if (it is BackHandlerFragment && it.onBackPressed())
                return true
        }
        return false
    }
}