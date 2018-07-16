package com.ssttkkl.fgoplanningtool.ui.ownitemlist

import com.ssttkkl.fgoplanningtool.data.HowToPerform
import com.ssttkkl.fgoplanningtool.data.Repo
import com.ssttkkl.fgoplanningtool.data.item.Item
import com.ssttkkl.fgoplanningtool.ui.ownitemlist.edititem.EditItemDialogFragment
import com.ssttkkl.fgoplanningtool.ui.iteminfo.ItemInfoDialogFragment

class OwnItemListFragmentPresenter(val view: OwnItemListFragment) {
    fun onItemEditAction(codename: String) {
        EditItemDialogFragment.newInstance(Repo.itemRepo[codename])
                .show(view.childFragmentManager, EditItemDialogFragment::class.qualifiedName)
    }

    fun onItemInfoAction(codename: String) {
        ItemInfoDialogFragment.newInstance(codename)
                .show(view.childFragmentManager, ItemInfoDialogFragment::class.qualifiedName)
    }

    fun updateItem(item: Item) {
        Repo.itemRepo.update(item, HowToPerform.Launch)
    }

}