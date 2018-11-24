package com.ssttkkl.fgoplanningtool.ui.utils

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager

abstract class BackRouterActivity : AppCompatActivity() {
    private fun searchBackHandler(fm: FragmentManager): Boolean {
        fm.fragments.forEach {
            if (it is BackHandler && it.onBackPressed())
                return true
            else if (searchBackHandler(it.childFragmentManager))
                return true
        }
        return false
    }

    override fun onBackPressed() {
        if (!searchBackHandler(supportFragmentManager))
            super.onBackPressed()
    }
}