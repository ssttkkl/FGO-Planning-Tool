package com.ssttkkl.fgoplanningtool.ui.servantlist.filterpresenter.itemfilter.additem

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider
import com.ssttkkl.fgoplanningtool.resources.itemdescriptor.ItemType
import com.ssttkkl.fgoplanningtool.ui.utils.CommonRecViewItemDecoration
import com.ssttkkl.fgoplanningtool.ui.utils.NoInterfaceImplException
import kotlinx.android.synthetic.main.fragment_servantlist_additem_itemlist.*

class ItemListFragment : androidx.fragment.app.Fragment() {
    interface OnItemClickListener {
        fun onItemClick(codename: String)
    }

    private var listener: OnItemClickListener? = null

    private lateinit var type: ItemType

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        type = ItemType.valueOf(arguments!!.getString(ARG_TYPE))
        listener = when {
            parentFragment is OnItemClickListener -> parentFragment as OnItemClickListener
            activity is OnItemClickListener -> activity as OnItemClickListener
            else -> throw NoInterfaceImplException(OnItemClickListener::class)
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_servantlist_additem_itemlist, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recView.apply {
            adapter = ItemListRecViewAdapter(context!!).apply {
                setNewData(ResourcesProvider.instance.itemDescriptors.values.filter { it.type == type }
                        .sortedBy { ResourcesProvider.instance.itemRank[it.codename] })
                setOnItemClickListener { listener?.onItemClick(it) }
            }
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context, androidx.recyclerview.widget.LinearLayoutManager.VERTICAL, false)
            addItemDecoration(CommonRecViewItemDecoration(context!!))
            setHasFixedSize(true)
        }
    }

    companion object {
        fun newInstance(type: ItemType) = ItemListFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_TYPE, type.name)
            }
        }

        private const val ARG_TYPE = "type"
    }
}