package com.ssttkkl.fgoplanningtool.ui.servantlist.filterpresenter.itemfilter

import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.AdapterView
import com.ssttkkl.fgoplanningtool.ui.servantlist.ServantListFragment
import com.ssttkkl.fgoplanningtool.ui.servantlist.filterpresenter.FilterPresenter
import com.ssttkkl.fgoplanningtool.ui.servantlist.filterpresenter.itemfilter.additem.AddItemDialogFragment
import kotlinx.android.synthetic.main.content_servantlist_item.*

class ItemFilterPresenter(view: ServantListFragment) : FilterPresenter(view) {
    init {
        view.apply {
            // Setup the CostItem RecView
            itemHeader_textView.setOnClickListener { expandLayout(itemHeader_textView, item_expLayout) }
            item_add_button.setOnClickListener { gotoAddItemFilterUi() }
            item_mode_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                    viewModel.itemFilterModeSpinnerPosition = pos
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
            item_recView.apply {
                adapter = ItemFilterRecViewAdapter(context!!).apply {
                    setNewData(viewModel.items)
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
        viewModel.addItem(codename)
        (view.item_recView.adapter as ItemFilterRecViewAdapter).addItem(codename)
    }

    private fun onRemoveItemAction(codename: String) {
        viewModel.removeItem(codename)
        (view.item_recView.adapter as ItemFilterRecViewAdapter).removeItem(codename)
    }
}