package com.ssttkkl.fgoplanningtool.ui.ownitemlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.fragment.findNavController
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.databinding.FragmentOwnitemlistBinding
import com.ssttkkl.fgoplanningtool.resources.itemdescriptor.ItemType
import com.ssttkkl.fgoplanningtool.ui.MainActivity

class OwnItemListFragment : Fragment(), LifecycleOwner, ItemListFragment.OnShowItemInfoListener {
    private lateinit var binding: FragmentOwnitemlistBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentOwnitemlistBinding.inflate(inflater, container, false)
        binding.setLifecycleOwner(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as? MainActivity)?.apply {
            drawerState = true
            title = getString(R.string.title_ownitemlist)
            invalidateOptionsMenu()
        }

        binding.viewPager.adapter = object : androidx.fragment.app.FragmentPagerAdapter(childFragmentManager) {
            override fun getPageTitle(pos: Int) = ItemType.values()[pos].localizedName
            override fun getItem(pos: Int) = ItemListFragment.newInstance(ItemType.values()[pos])
            override fun getCount() = ItemType.values().size
        }
        binding.tabLayout.setupWithViewPager(binding.viewPager)
    }

    override fun onShowItemInfo(codename: String) {
        findNavController().navigate(OwnItemListFragmentDirections.actionOwnItemListFragmentToItemInfoFragment(codename))
    }
}