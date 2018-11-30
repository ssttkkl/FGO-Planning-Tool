package com.ssttkkl.fgoplanningtool.ui.servantfilter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.ssttkkl.fgoplanningtool.data.item.Item
import com.ssttkkl.fgoplanningtool.databinding.FragmentServantfilterBinding
import com.ssttkkl.fgoplanningtool.resources.servant.Servant
import com.ssttkkl.fgoplanningtool.resources.servant.ServantClass
import com.ssttkkl.fgoplanningtool.resources.servant.WayToGet
import com.ssttkkl.fgoplanningtool.ui.servantfilter.additem.AddItemDialogFragment
import com.ssttkkl.fgoplanningtool.ui.utils.NoInterfaceImplException

class ServantFilterFragment : Fragment(), AddItemDialogFragment.OnAddItemListener {
    interface Callback {
        fun onFilter(filtered: List<Servant>)
        fun onRequestCostItems(servant: Servant): Collection<Item>
    }

    private var callback: Callback? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = when {
            parentFragment is Callback -> parentFragment as Callback
            activity is Callback -> activity as Callback
            else -> throw NoInterfaceImplException(Callback::class)
        }
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
        binding.orderRecView.apply {
            adapter = ServantFilterSingleSelectRecViewAdapter(context!!,
                    this@ServantFilterFragment,
                    Order.values().toList(),
                    binding.viewModel!!.order) { it.localizedName }
            layoutManager = FlexboxLayoutManager(context!!).apply {
                flexDirection = FlexDirection.ROW
                flexWrap = FlexWrap.WRAP
            }
            isNestedScrollingEnabled = false
        }
        binding.orderbyRecView.apply {
            adapter = ServantFilterSingleSelectRecViewAdapter(context!!,
                    this@ServantFilterFragment,
                    OrderBy.values().toList(),
                    binding.viewModel!!.orderBy) { it.localizedName }
            layoutManager = FlexboxLayoutManager(context!!).apply {
                flexDirection = FlexDirection.ROW
                flexWrap = FlexWrap.WRAP
            }
            isNestedScrollingEnabled = false
        }
        binding.starRecView.apply {
            adapter = ServantFilterMultipleSelectRecViewAdapter(context!!,
                    this@ServantFilterFragment,
                    listOf(1, 2, 3, 4, 5),
                    binding.viewModel!!.stars) { it.toString() }
            layoutManager = FlexboxLayoutManager(context!!).apply {
                flexDirection = FlexDirection.ROW
                flexWrap = FlexWrap.WRAP
            }
            isNestedScrollingEnabled = false
        }
        binding.classRecView.apply {
            adapter = ServantFilterMultipleSelectRecViewAdapter(context!!,
                    this@ServantFilterFragment,
                    ServantClass.values().toList(),
                    binding.viewModel!!.servantClasses) { it.name }
            layoutManager = FlexboxLayoutManager(context!!).apply {
                flexDirection = FlexDirection.ROW
                flexWrap = FlexWrap.WRAP
            }
            isNestedScrollingEnabled = false
        }
        binding.wayToGetRecView.apply {
            adapter = ServantFilterMultipleSelectRecViewAdapter(context!!,
                    this@ServantFilterFragment,
                    WayToGet.values().toList(),
                    binding.viewModel!!.waysToGet) { it.localizedName }
            layoutManager = FlexboxLayoutManager(context!!).apply {
                flexDirection = FlexDirection.ROW
                flexWrap = FlexWrap.WRAP
            }
            isNestedScrollingEnabled = false
        }
        binding.itemRecView.apply {
            adapter = ServantFilterItemRecViewAdapter(context!!,
                    this@ServantFilterFragment,
                    binding.viewModel!!)
            layoutManager = FlexboxLayoutManager(context!!).apply {
                flexDirection = FlexDirection.ROW
                flexWrap = FlexWrap.WRAP
            }
            isNestedScrollingEnabled = false
        }
        binding.viewModel?.apply {
            start {
                callback?.onRequestCostItems(it) ?: listOf()
            }
            filtered.observe(this@ServantFilterFragment, Observer {
                callback?.onFilter(it ?: listOf())
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

    companion object {
        private const val ARG_ORIGIN_SERVANT_IDS = "originServantIDs"
    }
}