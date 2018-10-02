package com.ssttkkl.fgoplanningtool.ui.servantfilter.filterpresenter

import com.ssttkkl.fgoplanningtool.ui.servantfilter.Order
import com.ssttkkl.fgoplanningtool.ui.servantfilter.OrderBy
import com.ssttkkl.fgoplanningtool.ui.servantfilter.ServantFilterFragment
import kotlinx.android.synthetic.main.content_servantlist_orderby.*

class OrderFilterPresenter(view: ServantFilterFragment) : SingleSelectFilterPresenter<Order>(view) {
    init {
        // Setup the Order RecView
        view.orderByHeader_textView.setOnClickListener { expandLayout(view.orderByHeader_textView, view.orderby_expLayout) }
        view.order_recView.setupSingleSelectRecView(Order.values().toList(),
                { it.localizedName },
                viewModel.order.ordinal,
                { viewModel.order = Order.values()[it] })
        view.orderby_recView.setupSingleSelectRecView(OrderBy.values().toList(),
                { it.localizedName },
                viewModel.orderBy.ordinal,
                { viewModel.orderBy = OrderBy.values()[it] })
    }

    override fun setUISelection(selection: Order) {
        (view.order_recView.adapter as ServantListSingleSelectAdapter<Order>).setPositionSelected(selection.ordinal)
    }

    fun setUIOrderBySelection(selection: OrderBy) {
        (view.orderby_recView.adapter as ServantListSingleSelectAdapter<OrderBy>).setPositionSelected(selection.ordinal)
    }
}