package com.ssttkkl.fgoplanningtool.ui.servantfilter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.chip.Chip
import com.ssttkkl.fgoplanningtool.data.item.Item
import com.ssttkkl.fgoplanningtool.databinding.FragmentServantfilterBinding
import com.ssttkkl.fgoplanningtool.resources.itemdescriptor.ItemDescriptor
import com.ssttkkl.fgoplanningtool.resources.servant.Servant
import com.ssttkkl.fgoplanningtool.ui.servantfilter.additem.AddItemDialogFragment
import com.ssttkkl.fgoplanningtool.ui.utils.ChipViewTarget

class ServantFilterFragment : Fragment(), AddItemDialogFragment.OnAddItemListener {
    interface Callback {
        fun onFilter(filtered: List<Servant>, isDefaultState: Boolean)
        fun onRequestCostItems(servant: Servant): Collection<Item>
    }

    private var callback: Callback? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = parentFragment as? Callback
    }

    override fun onDetach() {
        super.onDetach()
        callback = null
    }

    private lateinit var binding: FragmentServantfilterBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentServantfilterBinding.inflate(inflater, container, false)
        binding.viewModel = ViewModelProviders.of(this)[ServantFilterFragmentViewModel::class.java].apply {
            setOrigin(arguments?.getIntArray(ARG_ORIGIN_SERVANT_IDS)?.toSet())
        }
        binding.setLifecycleOwner(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.viewModel?.apply {
            start {
                callback?.onRequestCostItems(it) ?: listOf()
            }
            items.observe(this@ServantFilterFragment, Observer {
                onItemsChanged(it.toList().sortedBy { descriptor -> descriptor.rank })
            })
            filtered.observe(this@ServantFilterFragment, Observer {
                callback?.onFilter(it ?: listOf(), isDefaultState.value == true)
            })
            isDefaultState.observe(this@ServantFilterFragment, Observer {
                callback?.onFilter(filtered.value ?: listOf(), it == true)
            })
            showAddItemUIEvent.observe(this@ServantFilterFragment, Observer {
                showAddItemUI()
            })
        }
    }

    override fun onAddItem(codename: String) {
        binding.viewModel?.onAddItem(codename)
    }

    var originServantIDs: Set<Int>
        get() = arguments?.getIntArray(ARG_ORIGIN_SERVANT_IDS)?.toSet() ?: setOf()
        set(value) {
            if (::binding.isInitialized)
                binding.viewModel?.setOrigin(value)
            (arguments
                    ?: Bundle().also { arguments = it }).putIntArray(ARG_ORIGIN_SERVANT_IDS, value.toIntArray())
        }

    private fun showAddItemUI() {
        AddItemDialogFragment().show(childFragmentManager, AddItemDialogFragment::class.qualifiedName)
    }

    private fun onItemsChanged(items: List<ItemDescriptor>) {
        binding.itemsChipGroup.removeAllViews()
        items.forEach { descriptor ->
            val chip = Chip(binding.itemsChipGroup.context).apply {
                text = descriptor.localizedName
                isCloseIconVisible = true
                setOnCloseIconClickListener {
                    binding.viewModel?.onClickRemoveItem(descriptor)
                }
                Glide.with(binding.itemsChipGroup.context)
                        .load(descriptor.imgFile)
                        .apply(RequestOptions.circleCropTransform())
                        .into(ChipViewTarget(this))
            }
            chip.transitionName = descriptor.codename
            binding.itemsChipGroup.addView(chip)
        }
    }

    companion object {
        private const val ARG_ORIGIN_SERVANT_IDS = "originServantIDs"
    }
}