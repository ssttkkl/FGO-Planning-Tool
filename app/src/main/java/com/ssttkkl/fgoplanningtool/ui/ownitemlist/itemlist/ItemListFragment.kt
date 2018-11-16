package com.ssttkkl.fgoplanningtool.ui.ownitemlist.itemlist

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ssttkkl.fgoplanningtool.databinding.FragmentOwnitemlistItemlistBinding
import com.ssttkkl.fgoplanningtool.resources.itemdescriptor.ItemType
import com.ssttkkl.fgoplanningtool.ui.iteminfo.ItemInfoDialogFragment
import com.ssttkkl.fgoplanningtool.ui.utils.CommonRecViewItemDecoration

class ItemListFragment : androidx.fragment.app.Fragment() {
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
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context!!, RecyclerView.VERTICAL, false)
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
                .show(requireFragmentManager(), ItemInfoDialogFragment::class.qualifiedName)
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
