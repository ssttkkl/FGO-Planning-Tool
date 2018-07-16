package com.ssttkkl.fgoplanningtool.ui.ownitemlist.iteminfo

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.ui.costitemlist.requirementlist.RequirementListEntity
import com.ssttkkl.fgoplanningtool.ui.costitemlist.requirementlist.RequirementListRecViewAdapter
import com.ssttkkl.fgoplanningtool.ui.utils.CommonRecViewItemDecoration
import kotlinx.android.synthetic.main.fragment_iteminfo_requirementlist.*

class RequirementListFragment : Fragment() {
    var data: List<RequirementListEntity> = listOf()
        set(value) {
            field = value
            (recView?.adapter as? RequirementListRecViewAdapter)?.data = value
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_iteminfo_requirementlist, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recView.apply {
            adapter = RequirementListRecViewAdapter(context).apply {
                data = this@RequirementListFragment.data
            }
            layoutManager = LinearLayoutManager(context!!, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(CommonRecViewItemDecoration(context!!))
            isNestedScrollingEnabled = false
        }
    }
}