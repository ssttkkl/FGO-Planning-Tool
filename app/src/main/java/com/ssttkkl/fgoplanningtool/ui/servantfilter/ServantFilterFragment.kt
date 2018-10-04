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
import com.ssttkkl.fgoplanningtool.resources.servant.ServantClass
import com.ssttkkl.fgoplanningtool.resources.servant.WayToGet
import com.ssttkkl.fgoplanningtool.ui.servantfilter.filterpresenter.*
import com.ssttkkl.fgoplanningtool.ui.servantfilter.filterpresenter.itemfilter.additem.AddItemDialogFragment
import com.ssttkkl.fgoplanningtool.ui.utils.NoInterfaceImplException
import kotlinx.android.synthetic.main.fragment_servantfilter.*
import java.util.concurrent.ConcurrentSkipListSet

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
        activity?.getPreferences(Context.MODE_PRIVATE)?.apply {
            viewModel.classFilter = ConcurrentSkipListSet(getStringSet(KEY_CLASS, setOf()).map { ServantClass.valueOf(it) })
            viewModel.starFilter = ConcurrentSkipListSet(getStringSet(KEY_STAR, setOf()).map { it.toInt() })
            viewModel.wayToGetFilter = ConcurrentSkipListSet(getStringSet(KEY_WAY_TO_GET, setOf()).map { WayToGet.valueOf(it) })
            viewModel.itemFilter = ConcurrentSkipListSet(getStringSet(KEY_ITEM, setOf()))
            viewModel.itemFilterMode = ItemFilterMode.valueOf(getString(KEY_ITEM_MODE, ItemFilterMode.And.name))
            viewModel.orderBy = OrderBy.valueOf(getString(KEY_ORDER_BY, OrderBy.ID.name))
            viewModel.order = Order.valueOf(getString(KEY_ORDER, Order.Increase.name))

            postToListener()
            // when presenters are initiating, they will fetch selection from viewModel automatically.
            // so there is no need to post initial data to UI
        }

        orderFilterPresenter = OrderFilterPresenter(this)
        starFilterPresenter = StarFilterPresenter(this)
        classFilterPresenter = ClassFilterPresenter(this)
        itemFilterPresenter = ItemFilterPresenter(this)
        wayToGetFilterPresenter = WayToGetFilterPresenter(this)

        reset_button.setOnClickListener {
            viewModel.nameFilter = ""
            viewModel.classFilter.clear()
            viewModel.starFilter.clear()
            viewModel.wayToGetFilter.clear()
            viewModel.itemFilter.clear()
            viewModel.itemFilterMode = ItemFilterMode.And
            viewModel.orderBy = OrderBy.ID
            viewModel.order = Order.Increase

            postToListener()
            postToUI()
        }
    }

    var nameFilter
        get() = viewModel.nameFilter
        set(value) {
            viewModel.nameFilter = value
            postToListener()
        }

    var origin
        get() = viewModel.origin
        set(value) {
            viewModel.origin = value
            postToListener()
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

    override fun onDestroyView() {
        super.onDestroyView()
        activity?.getPreferences(Context.MODE_PRIVATE)?.edit()
                ?.putStringSet(KEY_CLASS, viewModel.classFilter.asSequence().map { it.name }.toSet())
                ?.putStringSet(KEY_STAR, viewModel.starFilter.asSequence().map { it.toString() }.toSet())
                ?.putStringSet(KEY_WAY_TO_GET, viewModel.wayToGetFilter.asSequence().map { it.name }.toSet())
                ?.putStringSet(KEY_ITEM, viewModel.itemFilter.toSet())
                ?.putString(KEY_ITEM_MODE, viewModel.itemFilterMode.name)
                ?.putString(KEY_ORDER, viewModel.order.name)
                ?.putString(KEY_ORDER_BY, viewModel.orderBy.name)
                ?.apply()
    }

    fun postToListener() {
        reset_button.isEnabled = !viewModel.isDefaultState
        listener?.onFilter(filtered)
    }

    private fun postToUI() {
        orderFilterPresenter.setUISelection(viewModel.order)
        orderFilterPresenter.setUIOrderBySelection(viewModel.orderBy)
        starFilterPresenter.setUISelection(viewModel.starFilter)
        classFilterPresenter.setUISelection(viewModel.classFilter)
        itemFilterPresenter.setUINewData(viewModel.itemFilter)
        wayToGetFilterPresenter.setUISelection(viewModel.wayToGetFilter)
    }

    override fun onAddItemAction(codename: String) {
        itemFilterPresenter.onAddItemAction(codename)
    }

    companion object {
        private const val KEY_CLASS = "class"
        private const val KEY_STAR = "star"
        private const val KEY_WAY_TO_GET = "wayToGet"
        private const val KEY_ITEM = "item"
        private const val KEY_ITEM_MODE = "itemMode"
        private const val KEY_ORDER = "order"
        private const val KEY_ORDER_BY = "orderBy"
    }
}