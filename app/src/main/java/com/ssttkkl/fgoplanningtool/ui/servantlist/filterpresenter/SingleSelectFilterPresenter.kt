package com.ssttkkl.fgoplanningtool.ui.servantlist.filterpresenter

import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.ssttkkl.fgoplanningtool.ui.servantlist.ServantListFragment


abstract class SingleSelectFilterPresenter(view: ServantListFragment) : FilterPresenter(view) {
    protected fun <T> RecyclerView.setupSingleSelectRecView(initData: List<T>,
                                                            toString: (T) -> String,
                                                            initSelectedPosition: Int,
                                                            onItemSelectedListener: (pos: Int) -> Unit): androidx.recyclerview.widget.RecyclerView {
        view.apply {
            adapter = ServantListSingleSelectAdapter<T>(activity!!, { toString(it) }).apply {
                data = initData
                setOnItemSelectedListener(onItemSelectedListener)
                setPositionSelected(initSelectedPosition)
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