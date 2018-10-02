package com.ssttkkl.fgoplanningtool.ui.servantfilter

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.data.item.costItems
import com.ssttkkl.fgoplanningtool.data.plan.Plan
import com.ssttkkl.fgoplanningtool.resources.servant.Servant
import com.ssttkkl.fgoplanningtool.ui.servantfilter.filterpresenter.*
import com.ssttkkl.fgoplanningtool.ui.servantfilter.filterpresenter.itemfilter.additem.AddItemDialogFragment
import com.ssttkkl.fgoplanningtool.ui.utils.NoInterfaceImplException
import kotlinx.android.synthetic.main.fragment_servantfilter.*

class ServantFilterFragment : Fragment(), AddItemDialogFragment.OnAddItemActionListener {
    interface OnFilterListener {
        fun onFilter(filtered: List<Servant>)
    }

    private var listener: OnFilterListener? = null

    private lateinit var viewModel: ServantFilterViewModel

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        viewModel = ViewModelProviders.of(this).get(ServantFilterViewModel::class.java)
        listener = when {
            parentFragment is OnFilterListener -> parentFragment as OnFilterListener
            activity is OnFilterListener -> activity as OnFilterListener
            else -> throw NoInterfaceImplException(OnFilterListener::class)
        }
    }

    private lateinit var orderFilterPresenter: OrderFilterPresenter
    private lateinit var starFilterPresenter: StarFilterPresenter
    private lateinit var classFilterPresenter: ClassFilterPresenter
    private lateinit var wayToGetFilterPresenter: WayToGetFilterPresenter
    private lateinit var itemFilterPresenter: ItemFilterPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_servantfilter, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        orderFilterPresenter = OrderFilterPresenter(this)
        starFilterPresenter = StarFilterPresenter(this)
        classFilterPresenter = ClassFilterPresenter(this)
        itemFilterPresenter = ItemFilterPresenter(this)
        wayToGetFilterPresenter = WayToGetFilterPresenter(this)

        reset_button.setOnClickListener {
            viewModel.reset()
            orderFilterPresenter.setSelection(viewModel.order)
            orderFilterPresenter.setOrderBySelection(viewModel.orderBy)
            starFilterPresenter.setSelection(viewModel.starFilter)
            classFilterPresenter.setSelection(viewModel.classFilter)
            itemFilterPresenter.setNewData(viewModel.itemFilter)
            wayToGetFilterPresenter.setSelection(viewModel.wayToGetFilter)
            postFiltered()
        }
    }

    var nameFilter
        get() = viewModel.nameFilter
        set(value) {
            viewModel.nameFilter = value
            postFiltered()
        }

    var origin
        get() = viewModel.origin
        set(value) {
            viewModel.origin = value
            postFiltered()
        }

    var planGetter: ((servantID: Int) -> Plan?)? = null // to get a plan for a servant when filter by cost items

    val filtered: List<Servant>
        get() {
            var list = viewModel.origin
            if (viewModel.nameFilter.isNotEmpty() || viewModel.nameFilter.isNotBlank())
                list = list.filter { cur ->
                    viewModel.nameFilter.split(' ').all { filter ->
                        cur.localizedName.contains(filter, true) ||
                                (cur.nickname.isNotEmpty() &&
                                        cur.nickname.any { it.contains(filter, true) })
                    }
                }
            if (viewModel.classFilter.isNotEmpty())
                list = list.filter { cur -> viewModel.classFilter.any { cur.theClass == it } }
            if (viewModel.wayToGetFilter.isNotEmpty())
                list = list.filter { cur -> viewModel.wayToGetFilter.any { cur.wayToGet == it } }
            if (viewModel.starFilter.isNotEmpty())
                list = list.filter { cur -> viewModel.starFilter.any { cur.star == it } }
            if (viewModel.itemFilter.isNotEmpty()) {
                list = when (viewModel.itemFilterMode) {
                    ItemFilterMode.And -> list.filter { cur ->
                        viewModel.itemFilter.all { filter ->
                            planGetter?.invoke(cur.id)?.costItems?.any { it.codename == filter }
                                    ?: cur.ascensionItems.any { lv -> lv.any { it.codename == filter } } ||
                                    cur.skillItems.any { lv -> lv.any { it.codename == filter } }
                        }
                    }
                    ItemFilterMode.Or -> list.filter { cur ->
                        viewModel.itemFilter.any { filter ->
                            planGetter?.invoke(cur.id)?.costItems?.any { it.codename == filter }
                                    ?: cur.ascensionItems.any { lv -> lv.any { it.codename == filter } } ||
                                    cur.skillItems.any { lv -> lv.any { it.codename == filter } }
                        }
                    }
                }
            }
            list = when (viewModel.orderBy) {
                OrderBy.ID -> list.sortedBy { it.id }
                OrderBy.Class -> list.sortedBy { it.theClass }
                OrderBy.Star -> list.sortedBy { it.star }
            }
            return if (viewModel.order == Order.Increase) list else list.reversed()
        }

    fun postFiltered() {
        listener?.onFilter(filtered)
    }

    override fun onAddItemAction(codename: String) {
        itemFilterPresenter.onAddItemAction(codename)
    }
}