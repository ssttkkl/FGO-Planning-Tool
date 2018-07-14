package com.ssttkkl.fgoplanningtool.ui.editplan.servantlist.filterpresenter

import com.ssttkkl.fgoplanningtool.ui.editplan.servantlist.ServantListFragment
import kotlinx.android.synthetic.main.content_servantlist_star.*

class StarFilterPresenter(view: ServantListFragment) : MultipleSelectFilterPresenter(view) {
    init {
        view.apply {
            // Setup the Star RecView
            starHeader_textView.setOnClickListener { expandLayout(starHeader_textView, star_expLayout) }
            star_recView.setupMultipleSelectRecView(listOf(1, 2, 3, 4, 5),
                    { it.toString() },
                    viewModel.starSelectedPosition,
                    { viewModel.setStarPositionSelected(it) },
                    { viewModel.setStarPositionUnselected(it) })
        }
    }
}