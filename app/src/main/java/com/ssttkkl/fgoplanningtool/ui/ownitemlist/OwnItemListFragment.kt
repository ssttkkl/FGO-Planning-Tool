package com.ssttkkl.fgoplanningtool.ui.ownitemlist

import android.arch.lifecycle.LifecycleOwner
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ssttkkl.fgoplanningtool.databinding.FragmentOwnitemlistBinding
import com.ssttkkl.fgoplanningtool.resources.itemdescriptor.ItemType
import com.ssttkkl.fgoplanningtool.ui.MainActivity
import com.ssttkkl.fgoplanningtool.ui.ownitemlist.itemlist.ItemListFragment

class OwnItemListFragment : Fragment(), LifecycleOwner {
    private lateinit var binding: FragmentOwnitemlistBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentOwnitemlistBinding.inflate(inflater, container, false)
        binding.setLifecycleOwner(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as? MainActivity)?.apply {
            setSupportActionBar(binding.toolbar)
            setupDrawerToggle(binding.toolbar)
        }

        binding.viewPager.adapter = object : FragmentPagerAdapter(childFragmentManager) {
            override fun getPageTitle(pos: Int) = ItemType.values()[pos].localizedName
            override fun getItem(pos: Int) = ItemListFragment.newInstance(ItemType.values()[pos])
            override fun getCount() = ItemType.values().size
        }
        binding.tabLayout.setupWithViewPager(binding.viewPager)
    }
}