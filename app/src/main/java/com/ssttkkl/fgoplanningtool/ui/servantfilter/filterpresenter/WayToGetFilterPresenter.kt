package com.ssttkkl.fgoplanningtool.ui.servantfilter.filterpresenter

import com.ssttkkl.fgoplanningtool.resources.servant.WayToGet
import com.ssttkkl.fgoplanningtool.ui.servantfilter.ServantFilterFragment
import kotlinx.android.synthetic.main.content_servantlist_waytoget.*

class WayToGetFilterPresenter(view: ServantFilterFragment) : MultipleSelectFilterPresenter<WayToGet>(view) {
    init {
        view.apply {
            // Setup the Star RecView
            wayToGetHeader_textView.setOnClickListener { expandLayout(wayToGetHeader_textView, wayToGet_expLayout) }
            wayToGet_recView.setupMultipleSelectRecView(WayToGet.values().toList(),
                    { it.localizedName },
                    viewModel.wayToGetFilter.map { it.ordinal },
                    { viewModel.wayToGetFilter.add(WayToGet.values()[it]) },
                    { viewModel.wayToGetFilter.remove(WayToGet.values()[it]) })
        }
    }

    override fun setUISelection(selection: Set<WayToGet>) {
        (view.wayToGet_recView.adapter as ServantListMultipleSelectAdapter<WayToGet>).apply {
            for (it in 0 until data.size)
                setPositionDeselected(it)
            selection.forEach {
                setPositionSelected(it.ordinal)
            }
        }
    }
}