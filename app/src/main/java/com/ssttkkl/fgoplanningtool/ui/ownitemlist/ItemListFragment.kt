package com.ssttkkl.fgoplanningtool.ui.ownitemlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.databinding.FragmentOwnitemlistItemlistBinding
import com.ssttkkl.fgoplanningtool.resources.itemdescriptor.ItemType
import com.ssttkkl.fgoplanningtool.ui.utils.CommonRecViewItemDecoration

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
        findNavController().navigate(R.id.action_global_itemInfoFragment, bundleOf("codename" to codename))
    }

    private fun showMessage(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    companion object {
        fun newInstance(type: ItemType) = ItemListFragment().apply {
            arguments = bundleOf(ARG_TYPE to type)
        }

        private const val ARG_TYPE = "type"
    }
}
