package com.ssttkkl.fgoplanningtool.ui.servantdetaillist

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.ui.utils.CommonRecViewItemDecoration
import kotlinx.android.synthetic.main.item_servantdetail_levellist.*

class ServantDetailLevelListFragment : Fragment() {
    var data: List<ServantDetailLevelListEntity> = listOf()
        set(value) {
            field = value
            (recView?.adapter as? ServantDetailLevelListRecViewAdapter)?.data = value
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_servantdetail_levellist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recView.apply {
            adapter = ServantDetailLevelListRecViewAdapter(context).apply {
                data = this@ServantDetailLevelListFragment.data
                if (savedInstanceState != null)
                    expandedPosition = savedInstanceState.getInt(KEY_EXPANDED, -1)
            }
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(CommonRecViewItemDecoration(context))
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt(KEY_EXPANDED, (recView?.adapter as? ServantDetailLevelListRecViewAdapter)?.expandedPosition
                ?: -1)
    }

    companion object {
        private const val KEY_EXPANDED = "expanded"
    }
}