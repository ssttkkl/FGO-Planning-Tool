package com.ssttkkl.fgoplanningtool.ui.eventlist.editevent

import androidx.databinding.ObservableArrayMap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.data.Repo
import com.ssttkkl.fgoplanningtool.data.event.Event
import com.ssttkkl.fgoplanningtool.ui.utils.SingleLiveEvent
import kotlin.collections.Collection
import kotlin.collections.forEach
import kotlin.collections.indexOfFirst
import kotlin.collections.indices
import kotlin.collections.map
import kotlin.collections.set
import kotlin.collections.toMutableList

abstract class EditEventBaseViewModel : ViewModel() {
    val mode = MutableLiveData<Mode>()
    abstract val event: ObservableEvent

    val shopEventItemCount = ObservableArrayMap<String, Long>()

    private val checkedShopItemsObserver = Observer<Collection<CheckableItem>> { checkedShopItems ->
        shopEventItemCount.clear()
        checkedShopItems.forEach { item ->
            shopEventItemCount[item.item.codename] = item.count
        }
    }

    protected var initialized: Boolean = false
        set(value) {
            if (!field && value) {
                event.checkedShopItems.observeForever(checkedShopItemsObserver)
            }
            field = value
        }

    abstract fun start(mode: Mode, event: Event)

    val showMessageEvent = SingleLiveEvent<String>()
    val finishEvent = SingleLiveEvent<Unit>()

    protected abstract fun prepareEvent(): Event?

    fun onClickShopItemsSpeedDialItem(actionItem: SpeedDialActionItem?): Boolean {
        when (actionItem?.id) {
            R.id.select_all -> onClickSelectAllShopItems()
            R.id.reset -> onClickResetShopItems()
            else -> return false
        }
        return true
    }

    fun onClickRemove() {
        if (mode.value == Mode.Edit) {
            Repo.EventRepo.remove(event.codename)
        }
        finishEvent.call()
    }

    fun onClickSave() {
        Repo.EventRepo.insert(prepareEvent() ?: return)
        finishEvent.call()
    }

    fun onClickSelectAllShopItems() {
        event.checkedShopItems.value = event.checkedShopItems.value?.toMutableList()?.apply {
            val allSelected = all { it.checked }
            indices.forEach { idx ->
                if (allSelected == this[idx].checked)
                    this[idx] = CheckableItem(this[idx].item, !allSelected, this[idx].item.count)
            }
        }
    }

    fun onClickResetShopItems() {
        event.checkedShopItems.value = event.descriptor?.shopItems?.map {
            CheckableItem(it, true, it.count)
        }
    }

    fun onClickShopItemCheckBox(codename: String) {
        event.checkedShopItems.value = event.checkedShopItems.value?.toMutableList()?.apply {
            val idx = indexOfFirst { it.item.codename == codename }
            this[idx] = CheckableItem(this[idx].item, !this[idx].checked, this[idx].item.count)
        }
    }
}