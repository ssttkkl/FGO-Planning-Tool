package com.ssttkkl.fgoplanningtool.ui.ownitemlist.itemlist


import android.arch.lifecycle.Observer
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.data.Repo
import com.ssttkkl.fgoplanningtool.data.item.Item
import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider
import com.ssttkkl.fgoplanningtool.resources.itemdescriptor.ItemType
import com.ssttkkl.fgoplanningtool.ui.utils.CommonRecViewItemDecoration
import kotlinx.android.synthetic.main.fragment_ownitemlist_itemlist.*

class ItemListFragment : Fragment() {
    interface OnItemClickListener {
        fun onItemClick(codename: String)
    }

    private var onItemClickListener: OnItemClickListener? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        onItemClickListener = when {
            parentFragment is OnItemClickListener -> parentFragment as OnItemClickListener
            activity is OnItemClickListener -> activity as OnItemClickListener
            else -> throw Exception("Either parent fragment or activity must impl OnItemClickListener interface.")
        }
    }

    override fun onDetach() {
        super.onDetach()
        onItemClickListener = null
    }

    private lateinit var type: ItemType

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        type = arguments!!.getSerializable("type") as ItemType
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_ownitemlist_itemlist, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recView.apply {
            adapter = ItemListRecViewAdapter(context!!).apply {
                setOnButtonClickListener { onItemClickListener?.onItemClick(it) }
            }
            layoutManager = LinearLayoutManager(context!!, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(CommonRecViewItemDecoration(context!!))
        }
        Repo.itemListLiveData.observe(this, Observer {
            onDataChanged(it ?: listOf())
        })
    }

    private fun onDataChanged(data: List<Item>) {
        val map = data.associate { Pair(it.codename, it) }
        val newData = ResourcesProvider.instance.itemDescriptors.values.filter { it.type == type }
                .map { Item(it.codename, map[it.codename]?.count ?: 0) }
                .sortedBy { ResourcesProvider.instance.itemRank[it.codename] }

        (recView?.adapter as? ItemListRecViewAdapter)?.setNewData(newData)
        Log.d("OwnItemList", "DataSet Changed. ($type)")
    }

    companion object {
        fun getInstance(type: ItemType) = ItemListFragment().apply {
            arguments = Bundle().apply {
                putSerializable(ARG_TYPE, type)
            }
        }

        private const val ARG_TYPE = "type"
    }
}
