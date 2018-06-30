package com.ssttkkl.fgoplanningtool.ui.editplan.servantlist.filterpresenters

import com.ssttkkl.fgoplanningtool.ui.editplan.servantlist.ServantListFragment
import kotlinx.android.synthetic.main.content_servantlist_star.*

class StarFilterPresenter(view: ServantListFragment) : MultipleSelectFilterPresenter(view) {
    init {
        view.apply {
            // Setup the Star RecView
            star_linearLayout.setOnClickListener { expandLayout(star_arrow_imageView, star_expLayout) }
            star_recView.setupMultipleSelectRecView(listOf(1, 2, 3, 4, 5),
                    { it.toString() },
                    viewModel.starSelectedPosition,
                    { viewModel.setStarPositionSelected(it) },
                    { viewModel.setStarPositionUnselected(it) })
        }
    }
}