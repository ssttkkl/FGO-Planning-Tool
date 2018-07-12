package com.ssttkkl.fgoplanningtool.ui.editplan.servantlist.filterpresenter

import com.ssttkkl.fgoplanningtool.resources.servant.ServantClass
import com.ssttkkl.fgoplanningtool.ui.editplan.servantlist.ServantListFragment
import kotlinx.android.synthetic.main.content_servantlist_class.*

class ClassFilterPresenter(view: ServantListFragment) : MultipleSelectFilterPresenter(view) {
    init {
        view.apply {
            // Setup the Class RecView
            class_linearLayout.setOnClickListener { expandLayout(class_arrow_imageView, class_expLayout) }
            class_recView.setupMultipleSelectRecView(ServantClass.values().toList(),
                    { it.name },
                    viewModel.classSelectedPosition,
                    { viewModel.setClassPositionSelected(it) },
                    { viewModel.setClassPositionUnselected(it) })
        }
    }
}