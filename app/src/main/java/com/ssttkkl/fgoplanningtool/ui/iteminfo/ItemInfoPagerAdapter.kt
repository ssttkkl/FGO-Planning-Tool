package com.ssttkkl.fgoplanningtool.ui.iteminfo

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.LifecycleOwner
import android.view.ViewGroup
import com.ssttkkl.fgoplanningtool.ui.requirementlist.RequirementListEntity

class ItemInfoPagerAdapter(fm: FragmentManager,
                           private val lifecycleOwner: LifecycleOwner,
                           private val viewModel: ItemInfoFragmentViewModel) : FragmentPagerAdapter(fm) {
    var pairs: List<Pair<String, List<RequirementListEntity>>> = listOf()

    private fun updatePairs() {
        val list = ArrayList<Pair<String, List<RequirementListEntity>>>()
        viewModel.ascensionItemEntities.value.also {
            if (!it.isNullOrEmpty())
                list.add(Pair(viewModel.ascensionItemTitle, it))
        }
        viewModel.skillItemEntities.value.also {
            if (!it.isNullOrEmpty())
                list.add(Pair(viewModel.skillItemTitle, it))
        }
        viewModel.dressItemEntities.value.also {
            if (!it.isNullOrEmpty())
                list.add(Pair(viewModel.dressItemTitle, it))
        }
        pairs = list
        notifyDataSetChanged()
    }

    init {
        viewModel.ascensionItemEntities.observe(lifecycleOwner, Observer { updatePairs() })
        viewModel.skillItemEntities.observe(lifecycleOwner, Observer { updatePairs() })
        viewModel.dressItemEntities.observe(lifecycleOwner, Observer { updatePairs() })
    }

    override fun getCount(): Int = pairs.size
    override fun getItem(pos: Int): Fragment = RequirementListFragment()

    override fun getPageTitle(pos: Int): CharSequence = pairs[pos].first

    override fun instantiateItem(container: ViewGroup, pos: Int): Any {
        return (super.instantiateItem(container, pos) as RequirementListFragment).apply {
            data = pairs[pos].second
        }
    }
}