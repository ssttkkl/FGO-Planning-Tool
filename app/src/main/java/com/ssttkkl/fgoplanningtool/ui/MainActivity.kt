package com.ssttkkl.fgoplanningtool.ui

import android.app.Activity
import androidx.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import android.view.MenuItem
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.data.Repo
import com.ssttkkl.fgoplanningtool.ui.databasemanage.DatabaseManageActivity
import com.ssttkkl.fgoplanningtool.ui.ownitemlist.OwnItemListFragment
import com.ssttkkl.fgoplanningtool.ui.planlist.PlanListFragment
import com.ssttkkl.fgoplanningtool.ui.preferences.PreferencesActivity
import com.ssttkkl.fgoplanningtool.ui.servantbaselist.ServantBaseListFragment
import com.ssttkkl.fgoplanningtool.ui.utils.BackHandlerActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

class MainActivity : BackHandlerActivity(), NavigationView.OnNavigationItemSelectedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navview.setNavigationItemSelectedListener(this)

        Repo.databaseDescriptorLiveData.observe(this, Observer {
            currentDatabase_textView.text = it?.name
        })
        databaseManage_button.setOnClickListener { gotoActivity(DatabaseManageActivity::class.java) }

        if (supportFragmentManager.fragments.isEmpty())
            switchToFragment(PlanListFragment::class)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view mItem clicks here.
        when (item.itemId) {
            R.id.planlist_nav -> switchToFragment(PlanListFragment::class)
            R.id.ownitemlist_nav -> switchToFragment(OwnItemListFragment::class)
            R.id.servantdetaillist_nav -> switchToFragment(ServantBaseListFragment::class)
            R.id.preferences_nav -> gotoActivityForResult(PreferencesActivity::class.java, REQUEST_CODE_PREF)
        }
        drawerlayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun switchToFragment(fragmentClass: KClass<out androidx.fragment.app.Fragment>) {
        if (supportFragmentManager.findFragmentByTag(fragmentClass.qualifiedName) == null) {
            supportFragmentManager.apply {
                beginTransaction().setTransition(androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .replace(R.id.frameLayout, fragmentClass.createInstance())
                        .commit()
                executePendingTransactions()
            }
        }
    }

    private fun gotoActivity(activityClass: Class<out Activity>) {
        startActivity(Intent(this, activityClass))
    }

    private fun gotoActivityForResult(activityClass: Class<out Activity>, requestCode: Int) {
        startActivityForResult(Intent(this, activityClass), requestCode)
    }

    private lateinit var toggle: ActionBarDrawerToggle

    fun setupDrawerToggle(toolbar: Toolbar) {
        if (::toggle.isInitialized)
            drawerlayout.removeDrawerListener(toggle)
        toggle = ActionBarDrawerToggle(this, drawerlayout, toolbar, R.string.openDrawer_main, R.string.closeDrawer_main)
        drawerlayout.addDrawerListener(toggle)
        toggle.syncState()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE_PREF -> {
                if (resultCode == Activity.RESULT_OK)
                    recreate()
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    companion object {
        // same to android.support.design.widget.LONG_DURATION_MS
        private const val BACK_AGAIN_DURATION = 2750

        private const val REQUEST_CODE_PREF = 1
    }
}