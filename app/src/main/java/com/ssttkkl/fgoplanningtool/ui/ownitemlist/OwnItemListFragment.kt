package com.ssttkkl.fgoplanningtool.ui.ownitemlist

import android.os.Bundle
import android.view.*
import android.widget.Switch
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.databinding.FragmentOwnitemlistBinding
import com.ssttkkl.fgoplanningtool.ui.MainActivity

class OwnItemListFragment : Fragment(), LifecycleOwner, ItemListFragment.OnShowItemInfoListener {
    private lateinit var binding: FragmentOwnitemlistBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentOwnitemlistBinding.inflate(inflater, container, false)
        binding.setLifecycleOwner(this)
        binding.viewModel = ViewModelProviders.of(this)[OwnItemListFragmentViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as? MainActivity)?.apply {
            drawerState = true
            title = getString(R.string.ownItemList)
        }
        setHasOptionsMenu(true)
        binding.viewPager.adapter = OwnItemListPagerAdapter(childFragmentManager, this, binding.viewModel!!)
        binding.tabLayout.setupWithViewPager(binding.viewPager)
    }

    override fun onShowItemInfo(codename: String) {
        findNavController().navigate(OwnItemListFragmentDirections.actionOwnItemListFragmentToItemInfoFragment(codename))
    }

    private lateinit var withEventItemsSwitch: Switch

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.ownitemlist, menu)

        val withEventItemsItem = menu.findItem(R.id.withEventItems)
        withEventItemsSwitch = withEventItemsItem.actionView.findViewById(R.id.switchWidget)
        withEventItemsSwitch.text = withEventItemsItem.title
        withEventItemsSwitch.setOnCheckedChangeListener { _, isChecked ->
            val alreadyChecked = binding.viewModel?.withEventItems?.value == true
            if (alreadyChecked != isChecked)
                binding.viewModel?.withEventItems?.value = isChecked
        }
        binding.viewModel?.withEventItems?.observe(this, Observer {
            withEventItemsSwitch.isChecked = it
        })
    }
}