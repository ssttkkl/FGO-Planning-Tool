package com.ssttkkl.fgoplanningtool.ui.servantlist.filterpresenter

import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.ssttkkl.fgoplanningtool.ui.servantlist.ServantListFragment

abstract class MultipleSelectFilterPresenter(view: ServantListFragment) : FilterPresenter(view) {
    protected fun <T> RecyclerView.setupMultipleSelectRecView(initData: List<T>,
                                                              toString: (T) -> String,
                                                              initSelectedPositions: Collection<Int>,
                                                              onItemSelectedListener: (pos: Int) -> Unit,
                                                              onItemUnselectedListener: (pos: Int) -> Unit): androidx.recyclerview.widget.RecyclerView {
        view.apply {
            adapter = ServantListMultipleSelectAdapter<T>(activity!!, { toString(it) }).apply {
                data = initData
                setOnItemSelectedListener(onItemSelectedListener)
                setOnItemUnselectedListener(onItemUnselectedListener)
                initSelectedPositions.forEach { setPositionSelected(it) }
            }
            layoutManager = FlexboxLayoutManager(activity!!).apply {
                flexDirection = FlexDirection.ROW
                flexWrap = FlexWrap.WRAP
            }
            isNestedScrollingEnabled = false
        }
        return this
    }
}