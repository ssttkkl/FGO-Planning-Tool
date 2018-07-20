package com.ssttkkl.fgoplanningtool.ui.servantlist.filterpresenter

import com.ssttkkl.fgoplanningtool.resources.servant.ServantClass
import com.ssttkkl.fgoplanningtool.ui.servantlist.ServantListFragment
import kotlinx.android.synthetic.main.content_servantlist_class.*

class ClassFilterPresenter(view: ServantListFragment) : MultipleSelectFilterPresenter(view) {
    init {
        view.apply {
            // Setup the Class RecView
            classHeader_textView.setOnClickListener { expandLayout(classHeader_textView, class_expLayout) }
            class_recView.setupMultipleSelectRecView(ServantClass.values().toList(),
                    { it.name },
                    viewModel.classSelectedPosition,
                    { viewModel.setClassPositionSelected(it) },
                    { viewModel.setClassPositionUnselected(it) })
        }
    }
}