package com.ssttkkl.fgoplanningtool.ui.ownitemlist

import com.ssttkkl.fgoplanningtool.data.item.Item
import com.ssttkkl.fgoplanningtool.data.item.ItemsRepository
import com.ssttkkl.fgoplanningtool.ui.ownitemlist.edititem.EditItemDialogFragment
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking

class OwnItemListFragmentPresenter(val view: OwnItemListFragment) {
    fun onItemClick(codename: String) {
        launch(UI) {
            gotoChangeItemUi(runBlocking(CommonPool) {
                ItemsRepository.get(codename)
            })
        }
    }

    fun updateItem(item: Item) {
        launch(CommonPool) { ItemsRepository.update(item) }
    }

    private fun gotoChangeItemUi(item: Item) {
        EditItemDialogFragment.newInstance(item)
                .show(view.childFragmentManager, EditItemDialogFragment.tag)
    }
}