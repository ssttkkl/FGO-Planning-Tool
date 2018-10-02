package com.ssttkkl.fgoplanningtool.ui.servantfilter.filterpresenter

import android.support.v7.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.ssttkkl.fgoplanningtool.ui.servantfilter.ServantFilterFragment


abstract class SingleSelectFilterPresenter<T>(view: ServantFilterFragment) : FilterPresenter(view) {
    protected fun <T> RecyclerView.setupSingleSelectRecView(initData: List<T>,
                                                            toString: (T) -> String,
                                                            initSelectedPosition: Int,
                                                            onItemSelectedListener: (pos: Int) -> Unit): RecyclerView {
        view.apply {
            adapter = ServantListSingleSelectAdapter<T>(activity!!, { toString(it) }).apply {
                data = initData
                setOnItemSelectedListener {
                    onItemSelectedListener(it)
                    this@SingleSelectFilterPresenter.view.postFiltered()
                }
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

    abstract fun setSelection(selection: T)
}