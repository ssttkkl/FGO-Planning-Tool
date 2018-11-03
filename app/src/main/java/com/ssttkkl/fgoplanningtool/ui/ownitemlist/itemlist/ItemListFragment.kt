package com.ssttkkl.fgoplanningtool.ui.ownitemlist.itemlist

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ssttkkl.fgoplanningtool.databinding.FragmentOwnitemlistItemlistBinding
import com.ssttkkl.fgoplanningtool.resources.itemdescriptor.ItemType
import com.ssttkkl.fgoplanningtool.ui.iteminfo.ItemInfoDialogFragment
import com.ssttkkl.fgoplanningtool.ui.utils.CommonRecViewItemDecoration
import com.ssttkkl.fgoplanningtool.ui.utils.NoInterfaceImplException

class ItemListFragment : Fragment() {
    private lateinit var binding: FragmentOwnitemlistItemlistBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentOwnitemlistItemlistBinding.inflate(inflater, container, false)
        binding.setLifecycleOwner(this)
        binding.viewModel = ViewModelProviders.of(this)[ItemListFragmentViewModel::class.java].apply {
            type.value = arguments!!.getSerializable(ARG_TYPE) as ItemType
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.recView.apply {
            adapter = ItemListRecViewAdapter(context!!, this@ItemListFragment, binding.viewModel!!)
            layoutManager = LinearLayoutManager(context!!, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(CommonRecViewItemDecoration(context!!))
        }

        binding.viewModel?.apply {
            showItemInfoEvent.observe(this@ItemListFragment, Observer {
                showItemInfo(it ?: return@Observer)
            })
            showMessageEvent.observe(this@ItemListFragment, Observer {
                showMessage(it ?: return@Observer)
            })
        }
    }

    private fun showItemInfo(codename: String) {
        ItemInfoDialogFragment.newInstance(codename)
                .show(fragmentManager, ItemInfoDialogFragment::class.qualifiedName)
    }

    private fun showMessage(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    companion object {
        fun newInstance(type: ItemType) = ItemListFragment().apply {
            arguments = Bundle().apply {
                putSerializable(ARG_TYPE, type)
            }
        }

        private const val ARG_TYPE = "type"
    }
}
