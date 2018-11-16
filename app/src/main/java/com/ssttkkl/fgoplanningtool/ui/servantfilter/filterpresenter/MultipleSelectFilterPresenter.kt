package com.ssttkkl.fgoplanningtool.ui.servantfilter.filterpresenter

import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.ssttkkl.fgoplanningtool.ui.servantfilter.ServantFilterFragment

abstract class MultipleSelectFilterPresenter<T>(view: ServantFilterFragment) : FilterPresenter(view) {
    protected fun <T> RecyclerView.setupMultipleSelectRecView(initData: List<T>,
                                                              toString: (T) -> String,
                                                              initSelectedPositions: Collection<Int>,
                                                              onItemSelectedListener: (pos: Int) -> Unit,
                                                              onItemDeselectedListener: (pos: Int) -> Unit): androidx.recyclerview.widget.RecyclerView {
        view.apply {
            adapter = ServantListMultipleSelectAdapter<T>(activity!!, { toString(it) }).apply {
                data = initData
                setOnItemSelectedListener {
                    onItemSelectedListener(it)
                    this@MultipleSelectFilterPresenter.view.postToListener()
                }
                setOnItemDeselectedListener {
                    onItemDeselectedListener(it)
                    this@MultipleSelectFilterPresenter.view.postToListener()
                }
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

    abstract fun setUISelection(selection: Set<T>)
}