package com.ssttkkl.fgoplanningtool.ui.servantfilter.filterpresenter

import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.AdapterView
import com.ssttkkl.fgoplanningtool.ui.servantfilter.ItemFilterMode
import com.ssttkkl.fgoplanningtool.ui.servantfilter.ServantFilterFragment
import com.ssttkkl.fgoplanningtool.ui.servantfilter.filterpresenter.itemfilter.ItemFilterRecViewAdapter
import com.ssttkkl.fgoplanningtool.ui.servantfilter.filterpresenter.itemfilter.additem.AddItemDialogFragment
import kotlinx.android.synthetic.main.content_servantlist_item.*

class ItemFilterPresenter(view: ServantFilterFragment) : FilterPresenter(view) {
    init {
        view.apply {
            // Setup the CostItem RecView
            itemHeader_textView.setOnClickListener { expandLayout(itemHeader_textView, item_expLayout) }
            item_add_button.setOnClickListener { gotoAddItemFilterUi() }
            item_mode_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                    viewModel.itemFilterMode = ItemFilterMode.values()[pos]
                    postFiltered()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
            item_recView.apply {
                adapter = ItemFilterRecViewAdapter(context!!).apply {
                    setNewData(viewModel.itemFilter)
                    setOnRemoveActionListener { onRemoveItemAction(it) }
                }
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                isNestedScrollingEnabled = false
            }
        }
    }

    private fun gotoAddItemFilterUi() {
        AddItemDialogFragment().show(view.childFragmentManager, AddItemDialogFragment.tag)
    }

    fun onAddItemAction(codename: String) {
        viewModel.itemFilter.add(codename)
        view.postFiltered()
        (view.item_recView.adapter as ItemFilterRecViewAdapter).addItem(codename)
    }

    private fun onRemoveItemAction(codename: String) {
        viewModel.itemFilter.remove(codename)
        view.postFiltered()
        (view.item_recView.adapter as ItemFilterRecViewAdapter).removeItem(codename)
    }

    fun setNewData(data: Set<String>) {
        (view.item_recView.adapter as ItemFilterRecViewAdapter).setNewData(data)
    }
}