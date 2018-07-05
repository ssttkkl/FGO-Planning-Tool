package com.ssttkkl.fgoplanningtool.ui.ownitemlist

import com.ssttkkl.fgoplanningtool.data.Repo
import com.ssttkkl.fgoplanningtool.data.item.Item
import com.ssttkkl.fgoplanningtool.ui.ownitemlist.edititem.EditItemDialogFragment
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking

class OwnItemListFragmentPresenter(val view: OwnItemListFragment) {
    fun onItemClick(codename: String) {
        launch(UI) {
            gotoChangeItemUi(Repo.itemRepo[codename]
                    ?: throw Exception("Item $codename not found. "))
        }
    }

    fun updateItem(item: Item) {
        Repo.itemRepo.updateAsync(item)
    }

    private fun gotoChangeItemUi(item: Item) {
        EditItemDialogFragment.newInstance(item)
                .show(view.childFragmentManager, EditItemDialogFragment.tag)
    }
}