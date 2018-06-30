package com.ssttkkl.fgoplanningtool.ui.editplan.servantlist.filterpresenters

import android.support.v7.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.ssttkkl.fgoplanningtool.ui.editplan.servantlist.ServantListFragment

abstract class MultipleSelectFilterPresenter(view: ServantListFragment) : FilterPresenter(view) {
    protected fun <T> RecyclerView.setupMultipleSelectRecView(initData: List<T>,
                                                              toString: (T) -> String,
                                                              initSelectedPositions: Collection<Int>,
                                                              onItemSelectedListener: (pos: Int) -> Unit,
                                                              onItemUnselectedListener: (pos: Int) -> Unit): RecyclerView {
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
            setHasFixedSize(true)
            isNestedScrollingEnabled = false
        }
        return this
    }
}