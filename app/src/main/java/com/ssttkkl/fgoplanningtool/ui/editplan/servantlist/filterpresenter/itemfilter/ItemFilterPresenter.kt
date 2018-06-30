package com.ssttkkl.fgoplanningtool.ui.editplan.servantlist.filterpresenters.itemfilter

import android.view.View
import android.widget.AdapterView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.ssttkkl.fgoplanningtool.ui.editplan.servantlist.ServantListFragment
import com.ssttkkl.fgoplanningtool.ui.editplan.servantlist.filterpresenters.FilterPresenter
import com.ssttkkl.fgoplanningtool.ui.editplan.servantlist.filterpresenters.itemfilter.additem.AddItemDialogFragment
import kotlinx.android.synthetic.main.content_servantlist_item.*

class ItemFilterPresenter(view: ServantListFragment) : FilterPresenter(view) {
    init {
        view.apply {
            // Setup the CostItem RecView
            item_linearLayout.setOnClickListener { expandLayout(item_arrow_imageView, item_expLayout) }
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
                layoutManager = FlexboxLayoutManager(activity!!).apply {
                    flexDirection = FlexDirection.ROW
                    flexWrap = FlexWrap.WRAP
                }
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