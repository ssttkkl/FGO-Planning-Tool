package com.ssttkkl.fgoplanningtool.ui.editplan.servantlist.filterpresenters

import com.ssttkkl.fgoplanningtool.ui.editplan.servantlist.Order
import com.ssttkkl.fgoplanningtool.ui.editplan.servantlist.OrderBy
import com.ssttkkl.fgoplanningtool.ui.editplan.servantlist.ServantListFragment
import kotlinx.android.synthetic.main.content_servantlist_orderby.*

class OrderFilterPresenter(view: ServantListFragment) : SingleSelectFilterPresenter(view) {
    init {
        view.apply {
            // Setup the Order RecView
            orderBy_linearLayout.setOnClickListener { expandLayout(orderBy_arrow_imageView, orderby_expLayout) }
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