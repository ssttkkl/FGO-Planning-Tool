package com.ssttkkl.fgoplanningtool.ui.servantfilter.filterpresenter

import com.ssttkkl.fgoplanningtool.resources.servant.ServantClass
import com.ssttkkl.fgoplanningtool.ui.servantfilter.ServantFilterFragment
import kotlinx.android.synthetic.main.content_servantlist_class.*

class ClassFilterPresenter(view: ServantFilterFragment) : MultipleSelectFilterPresenter<ServantClass>(view) {
    init {
        view.apply {
            // Setup the Class RecView
            classHeader_textView.setOnClickListener { expandLayout(classHeader_textView, class_expLayout) }
            class_recView.setupMultipleSelectRecView(ServantClass.values().toList(),
                    { it.name },
                    viewModel.classFilter.map { it.ordinal },
                    { viewModel.classFilter.add(ServantClass.values()[it]) },
                    { viewModel.classFilter.remove(ServantClass.values()[it]) })
        }
    }

    override fun setUISelection(selection: Set<ServantClass>) {
        (view.class_recView.adapter as ServantListMultipleSelectAdapter<ServantClass>).apply {
            for (it in 0 until data.size)
                setPositionDeselected(it)
            selection.forEach {
                setPositionSelected(it.ordinal)
            }
        }
    }
}