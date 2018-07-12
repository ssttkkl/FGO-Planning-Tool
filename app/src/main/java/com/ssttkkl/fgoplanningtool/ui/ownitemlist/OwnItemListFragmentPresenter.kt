package com.ssttkkl.fgoplanningtool.ui.ownitemlist

import com.ssttkkl.fgoplanningtool.data.HowToPerform
import com.ssttkkl.fgoplanningtool.data.Repo
import com.ssttkkl.fgoplanningtool.data.item.Item
import com.ssttkkl.fgoplanningtool.ui.ownitemlist.edititem.EditItemDialogFragment
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

class OwnItemListFragmentPresenter(val view: OwnItemListFragment) {
    fun onItemClick(codename: String) {
        gotoChangeItemUi(Repo.itemRepo[codename])
    }

    fun updateItem(item: Item) {
        Repo.itemRepo.update(item, HowToPerform.Launch)
    }

    private fun gotoChangeItemUi(item: Item) {
        EditItemDialogFragment.newInstance(item)
                .show(view.childFragmentManager, EditItemDialogFragment.tag)
    }
}