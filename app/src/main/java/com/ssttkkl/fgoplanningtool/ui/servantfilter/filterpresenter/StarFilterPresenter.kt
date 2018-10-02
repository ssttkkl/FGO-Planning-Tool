package com.ssttkkl.fgoplanningtool.ui.servantfilter.filterpresenter

import com.ssttkkl.fgoplanningtool.ui.servantfilter.ServantFilterFragment
import kotlinx.android.synthetic.main.content_servantlist_star.*

class StarFilterPresenter(view: ServantFilterFragment) : MultipleSelectFilterPresenter<Int>(view) {
    init {
        view.apply {
            // Setup the Star RecView
            starHeader_textView.setOnClickListener { expandLayout(starHeader_textView, star_expLayout) }
            star_recView.setupMultipleSelectRecView(listOf(1, 2, 3, 4, 5),
                    { it.toString() },
                    viewModel.starFilter.map { it - 1 },
                    { viewModel.starFilter.add(it + 1) },
                    { viewModel.starFilter.remove(it + 1) })
        }
    }

    override fun setSelection(selection: Set<Int>) {
        (view.star_recView.adapter as ServantListMultipleSelectAdapter<Int>).apply {
            for (it in 0 until data.size)
                setPositionDeselected(it)
            selection.forEach {
                setPositionSelected(it - 1)
            }
        }
    }
}