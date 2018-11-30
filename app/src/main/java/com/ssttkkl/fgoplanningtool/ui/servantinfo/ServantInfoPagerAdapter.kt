package com.ssttkkl.fgoplanningtool.ui.servantinfo

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer

class ServantInfoPagerAdapter(fm: FragmentManager,
                              private val lifecycleOwner: LifecycleOwner,
                              private val viewModel: ServantInfoFragmentViewModel) : FragmentPagerAdapter(fm) {
    var pairs: List<Pair<String, List<ServantInfoLevelListEntity>>> = listOf()

    private fun updatePairs() {
        val list = ArrayList<Pair<String, List<ServantInfoLevelListEntity>>>()
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
    override fun getItem(pos: Int): Fragment = ServantInfoLevelListFragment()

    override fun getPageTitle(pos: Int): CharSequence = pairs[pos].first

    override fun instantiateItem(container: ViewGroup, pos: Int): Any {
        return (super.instantiateItem(container, pos) as ServantInfoLevelListFragment).apply {
            data = pairs[pos].second
        }
    }
}