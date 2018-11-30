package com.ssttkkl.fgoplanningtool.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.google.android.material.navigation.NavigationView
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.data.Repo
import com.ssttkkl.fgoplanningtool.databinding.ActivityMainBinding
import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider
import com.ssttkkl.fgoplanningtool.ui.utils.BackRouterActivity

class MainActivity : BackRouterActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding

    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.navView.setNavigationItemSelectedListener(this)

        setSupportActionBar(binding.toolbar)
        binding.title = title
        toggle = ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar, R.string.openDrawer_main, R.string.closeDrawer_main).apply {
            setToolbarNavigationClickListener {
                onBackPressed()
            }
        }
        binding.drawerLayout.addDrawerListener(toggle)
        onChangeDrawerState(drawerState)

        Repo.databaseDescriptorLiveData.observe(this, Observer {
            binding.currentDatabaseTextView.text = it?.name
        })
        ResourcesProvider.addOnRenewListener(this) {
            recreate()
        }
        binding.databaseManageButton.setOnClickListener {
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_databaseManageFragment)
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        when (item.itemId) {
            R.id.planListFragment -> navController.navigate(R.id.action_global_planListFragment)
            R.id.ownItemListFragment -> navController.navigate(R.id.action_global_ownItemListFragment)
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

    var drawerState: Boolean = true
        set(value) {
            if (field != value)
                onChangeDrawerState(value)
            field = value
        }

    private fun onChangeDrawerState(enable: Boolean) {
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

    var title: String = ""
        set(value) {
            if (::binding.isInitialized)
                binding.title = value
            field = value
        }
}