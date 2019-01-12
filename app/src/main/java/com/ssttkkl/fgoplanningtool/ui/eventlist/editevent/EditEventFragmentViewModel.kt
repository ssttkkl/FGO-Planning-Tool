package com.ssttkkl.fgoplanningtool.ui.eventlist.editevent

import androidx.databinding.ObservableArrayMap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.data.Repo
import com.ssttkkl.fgoplanningtool.data.event.Event
import com.ssttkkl.fgoplanningtool.data.item.Item
import com.ssttkkl.fgoplanningtool.resources.itemdescriptor.ItemType
import com.ssttkkl.fgoplanningtool.ui.eventlist.editevent.point.PointItem
import com.ssttkkl.fgoplanningtool.ui.eventlist.editevent.shop.CheckableItem
import com.ssttkkl.fgoplanningtool.ui.utils.SingleLiveEvent
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

class EditEventFragmentViewModel : ViewModel() {
    val mode = MutableLiveData<Mode>()

    lateinit var oldEvent: Event
    lateinit var event: ObservableEvent

    private var initialized: Boolean = false
        set(value) {
            if (!field && value) {
                event.checkedShopItems.observeForever(checkedShopItemsObserver)
            }
            field = value
        }

    @Synchronized
    fun start(mode: Mode, event: Event) {
        if (!initialized) {
            this.mode.value = mode
            this.oldEvent = event
            this.event = ObservableEvent(event)
            initialized = true
        }
    }

    val shopEventItemCount = ObservableArrayMap<String, Long>()

    private val checkedShopItemsObserver = Observer<Collection<CheckableItem>> { checkedShopItems ->
        shopEventItemCount.clear()
        checkedShopItems.forEach { item ->
            shopEventItemCount[item.item.codename] = item.count
        }
    }

    private fun prepareEvent(): Event? {
        val indexed = event.checkedShopItems.value
                ?.filter { it.checked }
                ?.associateTo(HashMap()) { it.item.codename to it }
                ?: mapOf<String, CheckableItem>()
        shopEventItemCount.forEach { (key, value) ->
            val item = indexed[key]
            if (item != null && item.checked && value !in 0..item.item.count) {
                showMessageEvent.call(MyApp.context.getString(R.string.illegalValueOfItemCount, item.item.descriptor?.localizedName, 0, item.item.count))
                return null
            }
        }
        val checkedShopItems = shopEventItemCount.filterKeys { indexed.contains(it) }
                .map { Item(it.key, it.value) }
        return Event(event.codename,
                event.rerunAndParticipated.value == true,
                checkedShopItems,
                event.lotteryBoxCount.toMap(),
                event.point.toMap())
    }

    val showMessageEvent = SingleLiveEvent<String>()
    val gotoConfirmChangeEventUIEvent = SingleLiveEvent<Event>()
    val gotoConfirmRemoveEventUIEvent = SingleLiveEvent<Event>()
    val finishEvent = SingleLiveEvent<Unit>()

    fun onClickShopItemsSpeedDialItem(actionItem: SpeedDialActionItem?): Boolean {
        when (actionItem?.id) {
            R.id.select_all -> onClickSelectAllShopItems()
            R.id.reset -> onClickResetShopItems()
            R.id.select_all_expect_pieces -> onClickSelectAllShopItemsExpectPiece()
            else -> return false
        }
        return true
    }

    fun onClickRemove() {
        if (mode.value == Mode.Edit)
            gotoConfirmRemoveEventUIEvent.call(event.originEvent.value)
        else
            finishEvent.call()
    }

    fun onClickSave() {
        val newEvent = prepareEvent()!!
        when (mode.value) {
            Mode.New -> {
                Repo.EventRepo.insert(newEvent)
                finishEvent.call()
            }
            Mode.Edit -> {
                if (oldEvent != newEvent)
                    gotoConfirmChangeEventUIEvent.call(newEvent)
                else
                    finishEvent.call()
            }
        }
    }

    private fun onClickSelectAllShopItems() {
        event.checkedShopItems.value = event.checkedShopItems.value?.toMutableList()?.apply {
            val allSelected = all { it.checked }
            indices.forEach { idx ->
                if (allSelected == this[idx].checked)
                    this[idx] = CheckableItem(this[idx].item, !allSelected, this[idx].item.count)
            }
        }
    }

    private fun onClickSelectAllShopItemsExpectPiece() {
        event.checkedShopItems.value = event.checkedShopItems.value?.toMutableList()?.apply {
            indices.forEach { idx ->
                if (this[idx].item.descriptor?.type == ItemType.Piece && this[idx].checked)
                    this[idx] = CheckableItem(this[idx].item, false, this[idx].item.count)
                else if (this[idx].item.descriptor?.type != ItemType.Piece && !this[idx].checked)
                    this[idx] = CheckableItem(this[idx].item, true, this[idx].item.count)
            }
        }
    }

    private fun onClickResetShopItems() {
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

    fun onClickMaxPoint(poolCodename: String) {
        event.point[poolCodename] = event.descriptor?.pointPools?.get(poolCodename)?.items?.keys?.max()
    }

    fun onClickPointItem(poolCodename: String, pointItem: PointItem) {
        event.point[poolCodename] = pointItem.requestPoint
    }
}