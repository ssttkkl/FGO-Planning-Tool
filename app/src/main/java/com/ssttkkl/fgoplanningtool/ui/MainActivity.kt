package com.ssttkkl.fgoplanningtool.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.app.FragmentTransaction
import android.support.v4.view.GravityCompat
import android.view.MenuItem
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.ui.ownitemlist.OwnItemListFragment
import com.ssttkkl.fgoplanningtool.ui.planlist.PlanListFragment
import com.ssttkkl.fgoplanningtool.ui.preferences.PreferencesActivity
import com.ssttkkl.fgoplanningtool.ui.utils.BackHandlerActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.reflect.full.createInstance

class MainActivity : BackHandlerActivity(), NavigationView.OnNavigationItemSelectedListener {
    private val fragmentClasses = listOf(PlanListFragment::class, OwnItemListFragment::class)

    private var cur = -1 // current fragment position

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navview.setNavigationItemSelectedListener(this)
        switchToFragment(savedInstanceState?.getInt(KEY_CUR) ?: 0)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view mItem clicks here.
        when (item.itemId) {
            R.id.planlist_nav -> switchToFragment(0)
            R.id.ownitemlist_nav -> switchToFragment(1)
            R.id.preferences_nav -> gotoActivity(PreferencesActivity::class.java)
        }
        drawerlayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun switchToFragment(idx: Int) {
        if (supportFragmentManager.findFragmentByTag(fragmentClasses[idx].qualifiedName) == null) {
            supportFragmentManager.apply {
                beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .replace(R.id.frameLayout, fragmentClasses[idx].createInstance())
                        .commit()
                executePendingTransactions()
            }
            cur = idx
        }
    }

    private fun gotoActivity(activityClass: Class<out Activity>) {
        startActivity(Intent(this, activityClass))
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putInt(KEY_CUR, cur)
    }

    private var lastBackPressedTime: Long = 0

    override fun onBackPressed() {
        if (drawerlayout.isDrawerOpen(GravityCompat.START)) {
            drawerlayout.closeDrawer(GravityCompat.START)
        } else if (!spreadBackPressedEvent()) {
            val now = System.currentTimeMillis()
            if (now - lastBackPressedTime <= BACK_AGAIN_DURATION)
                finish()
            else {
                lastBackPressedTime = now
                Snackbar.make(frameLayout.getChildAt(0), R.string.backAgain_main, Snackbar.LENGTH_LONG)
                        .show()
            }
        }
    }

    companion object {
        // same to android.support.design.widget.LONG_DURATION_MS
        private const val BACK_AGAIN_DURATION = 2750

        private const val KEY_CUR = "cur"
    }
}