package com.ssttkkl.fgoplanningtool.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.google.android.material.navigation.NavigationView
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.data.Repo
import com.ssttkkl.fgoplanningtool.databinding.ActivityMainBinding
import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider
import com.ssttkkl.fgoplanningtool.ui.utils.BackHandlerActivity

class MainActivity : BackHandlerActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.navView.setNavigationItemSelectedListener(this)

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
        // Handle navigation view mItem clicks here.
        val navController = findNavController(R.id.nav_host_fragment)
        when (item.itemId) {
            R.id.planListFragment -> navController.navigate(R.id.action_global_planListFragment)
            R.id.ownItemListFragment -> navController.navigate(R.id.action_global_ownItemListFragment)
            R.id.servantBaseListFragment -> navController.navigate(R.id.action_global_servantBaseListFragment)
            R.id.settingsActivity -> navController.navigate(R.id.action_global_settingsActivity)
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private lateinit var toggle: ActionBarDrawerToggle

    fun setupDrawerToggle(toolbar: Toolbar) {
        if (::toggle.isInitialized)
            binding.drawerLayout.removeDrawerListener(toggle)
        toggle = ActionBarDrawerToggle(this, binding.drawerLayout, toolbar, R.string.openDrawer_main, R.string.closeDrawer_main)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }
}