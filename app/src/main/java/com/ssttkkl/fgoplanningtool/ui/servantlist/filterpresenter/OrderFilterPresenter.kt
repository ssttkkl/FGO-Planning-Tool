package com.ssttkkl.fgoplanningtool.ui.servantlist.filterpresenter

import com.ssttkkl.fgoplanningtool.ui.servantlist.Order
import com.ssttkkl.fgoplanningtool.ui.servantlist.OrderBy
import com.ssttkkl.fgoplanningtool.ui.servantlist.ServantListFragment
import kotlinx.android.synthetic.main.content_servantlist_orderby.*

class OrderFilterPresenter(view: ServantListFragment) : SingleSelectFilterPresenter(view) {
    init {
        view.apply {
            // Setup the Order RecView
            orderByHeader_textView.setOnClickListener { expandLayout(orderByHeader_textView, orderby_expLayout) }
            order_recView.setupSingleSelectRecView(Order.values().toList(),
                    { it.localizedName },
                    viewModel.orderSelectedPosition,
                    { viewModel.orderSelectedPosition = it })
            orderby_recView.setupSingleSelectRecView(OrderBy.values().toList(),
                    { it.localizedName },
                    viewModel.orderBySelectedPosition,
                    { viewModel.orderBySelectedPosition = it })
        }
    }
}