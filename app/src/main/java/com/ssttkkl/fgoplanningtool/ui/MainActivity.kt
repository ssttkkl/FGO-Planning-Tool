package com.ssttkkl.fgoplanningtool.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.google.android.material.navigation.NavigationView
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.databinding.ActivityMainBinding
import com.ssttkkl.fgoplanningtool.ui.utils.BackRouterActivity

class MainActivity : BackRouterActivity(),
        NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding

    private lateinit var toggle: ActionBarDrawerToggle

    private val navController
        get() = findNavController(R.id.nav_host_fragment)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.setLifecycleOwner(this)
        binding.viewModel = ViewModelProviders.of(this)[MainActivityViewModel::class.java].apply {
            gotoDatabaseManageUIEvent.observe(this@MainActivity, Observer {
                gotoDatabaseManageUI()
            })
            drawerState.observe(this@MainActivity, Observer {
                onDrawerStateChanged(it == true)
            })
        }

        // setup navigation view
        binding.navView.setNavigationItemSelectedListener(this)

        // setup toolbar
        setSupportActionBar(binding.toolbar)
        toggle = ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar, R.string.openDrawer, R.string.closeDrawer).apply {
            setToolbarNavigationClickListener {
                onBackPressed()
            }
        }
        binding.drawerLayout.addDrawerListener(toggle)
        onDrawerStateChanged(drawerState)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.planListFragment -> navController.navigate(R.id.action_global_planListFragment)
            R.id.ownItemListFragment -> navController.navigate(R.id.action_global_ownItemListFragment)
            R.id.eventListFragment -> navController.navigate(R.id.action_global_eventListFragment)
            R.id.servantBaseListFragment -> navController.navigate(R.id.action_global_servantBaseListFragment)
            R.id.settingsFragment -> navController.navigate(R.id.action_global_settingsFragment)
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START))
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        else
            super.onBackPressed()
    }

    var drawerState: Boolean
        get() = ::binding.isInitialized && binding.viewModel?.drawerState?.value == true
        set(value) {
            if (::binding.isInitialized)
                binding.viewModel?.drawerState?.value = value
        }

    var title: String
        get() {
            return if (::binding.isInitialized)
                binding.viewModel?.title?.value ?: ""
            else
                ""
        }
        set(value) {
            if (::binding.isInitialized)
                binding.viewModel?.title?.value = value
        }

    private fun onDrawerStateChanged(enable: Boolean) {
        if (enable) {
            if (::binding.isInitialized)
                binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            if (::toggle.isInitialized) {
                toggle.isDrawerIndicatorEnabled = true
                toggle.syncState()
            }
        } else {
            if (::binding.isInitialized)
                binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            if (::toggle.isInitialized) {
                toggle.isDrawerIndicatorEnabled = false
                toggle.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp)
                toggle.syncState()
            }
        }
    }

    private fun gotoDatabaseManageUI() {
        navController.navigate(R.id.action_global_databaseManageFragment)
        binding.drawerLayout.closeDrawer(GravityCompat.START)
    }

    companion object {
        private val homeDestinationIDs = setOf(R.id.planListFragment,
                R.id.ownItemListFragment,
                R.id.servantBaseListFragment,
                R.id.settingsFragment,
                R.id.databaseManageFragment)
    }
}