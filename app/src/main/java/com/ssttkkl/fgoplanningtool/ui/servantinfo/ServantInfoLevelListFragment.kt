package com.ssttkkl.fgoplanningtool.ui.servantinfo

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider
import com.ssttkkl.fgoplanningtool.resources.itemdescriptor.ItemType
import androidx.navigation.fragment.findNavController
import com.ssttkkl.fgoplanningtool.ui.utils.CommonRecViewItemDecoration
import kotlinx.android.synthetic.main.item_servantinfo_levellist.*

class ServantInfoLevelListFragment : androidx.fragment.app.Fragment() {
    var data: List<ServantInfoLevelListEntity> = listOf()
        set(value) {
            field = value
            (recView?.adapter as? ServantInfoLevelListRecViewAdapter)?.data = value
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_servantinfo_levellist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recView.apply {
            adapter = ServantInfoLevelListRecViewAdapter(context).apply {
                data = this@ServantInfoLevelListFragment.data
                setOnItemClickListener { gotoItemInfoUi(it) }
                if (savedInstanceState != null)
                    expandedPosition = savedInstanceState.getInt(KEY_EXPANDED, -1)
            }
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            addItemDecoration(CommonRecViewItemDecoration(context))
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_EXPANDED, (recView?.adapter as? ServantInfoLevelListRecViewAdapter)?.expandedPosition
                ?: -1)
    }

    private fun gotoItemInfoUi(codename: String) {
        if (ResourcesProvider.instance.itemDescriptors[codename]?.type != ItemType.General)
            findNavController().navigate(R.id.action_global_itemInfoFragment, bundleOf("codename" to codename))
    }

    companion object {
        private const val KEY_EXPANDED = "expanded"
    }
}