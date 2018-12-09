package com.ssttkkl.fgoplanningtool.ui.ownitemlist

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.ssttkkl.fgoplanningtool.resources.itemdescriptor.ItemType

class OwnItemListPagerAdapter(fragmentManager: FragmentManager,
                              lifecycleOwner: LifecycleOwner,
                              viewModel: OwnItemListFragmentViewModel)
    : FragmentPagerAdapter(fragmentManager) {
    private var itemTypes: List<ItemType> = listOf()

    init {
        viewModel.data.observe(lifecycleOwner, Observer { map ->
            itemTypes = map.keys.filterNotNull().sortedBy { it }
            notifyDataSetChanged()
        })
    }

    override fun getPageTitle(position: Int) = itemTypes[position].localizedName
    override fun getItem(position: Int) = ItemListFragment.newInstance(itemTypes[position])
    override fun getCount() = itemTypes.size
}