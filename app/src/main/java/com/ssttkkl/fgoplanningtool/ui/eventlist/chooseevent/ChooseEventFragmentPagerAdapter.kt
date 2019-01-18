package com.ssttkkl.fgoplanningtool.ui.eventlist.chooseevent

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer

class ChooseEventFragmentPagerAdapter(fragmentManager: FragmentManager,
                                      lifecycleOwner: LifecycleOwner,
                                      viewModel: ChooseEventFragmentViewModel) : FragmentPagerAdapter(fragmentManager) {
    var tabs: IntArray = intArrayOf()

    init {
        viewModel.events.observe(lifecycleOwner, Observer {
            tabs = it.map { (year, _) -> year }.sortedDescending().toIntArray()
            notifyDataSetChanged()
        })
    }

    override fun getCount(): Int = tabs.size
    override fun getItem(position: Int) = ChooseEventListFragment.newInstance(tabs[position])
    override fun getPageTitle(position: Int) = tabs[position].toString()
}