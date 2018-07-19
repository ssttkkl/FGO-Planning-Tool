package com.ssttkkl.fgoplanningtool.ui.servantlist.filterpresenter

import com.ssttkkl.fgoplanningtool.resources.servant.WayToGet
import com.ssttkkl.fgoplanningtool.ui.servantlist.ServantListFragment
import kotlinx.android.synthetic.main.content_servantlist_waytoget.*

class WayToGetFilterPresenter(view: ServantListFragment) : MultipleSelectFilterPresenter(view) {
    init {
        view.apply {
            // Setup the Star RecView
            wayToGetHeader_textView.setOnClickListener { expandLayout(wayToGetHeader_textView, wayToGet_expLayout) }
            wayToGet_recView.setupMultipleSelectRecView(WayToGet.values().toList(),
                    { it.localizedName },
                    viewModel.wayToGetSelectedPosition,
                    { viewModel.setWayToGetPositionSelected(it) },
                    { viewModel.setWayToGetPositionUnselected(it) })
        }
    }
}