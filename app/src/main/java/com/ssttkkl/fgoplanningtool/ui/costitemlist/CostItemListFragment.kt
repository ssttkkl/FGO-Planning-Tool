package com.ssttkkl.fgoplanningtool.ui.costitemlist


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.data.item.Item
import com.ssttkkl.fgoplanningtool.ui.utils.CommonRecViewItemDecoration
import kotlinx.android.synthetic.main.fragment_costitemlist.*

class CostItemListFragment : Fragment() {
    private var dataChangedSinceInit = false

    var data: Collection<Item> = listOf()
        set(value) {
            field = value
            dataChangedSinceInit = true
            (recView?.adapter as? CostItemListAdapter)?.setNewData(value)
            recView?.visibility = if (value.isEmpty()) View.GONE else View.VISIBLE
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!dataChangedSinceInit && savedInstanceState != null && savedInstanceState.containsKey(ARG_DATA)) {
            data = savedInstanceState.getParcelableArray(ARG_DATA).map { it as Item }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_costitemlist, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recView.apply {
            adapter = CostItemListAdapter(context!!).apply {
                setNewData(this@CostItemListFragment.data)
            }
            layoutManager = LinearLayoutManager(context!!, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(CommonRecViewItemDecoration(context!!, false, true))
            visibility = if (data.isEmpty()) View.GONE else View.VISIBLE
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelableArray(ARG_DATA, data.toTypedArray())
    }

    companion object {
        private const val ARG_DATA = "data"

        val tag
            get() = CostItemListFragment::class.qualifiedName
    }
}