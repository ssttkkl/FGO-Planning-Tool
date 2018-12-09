package com.ssttkkl.fgoplanningtool.ui.eventlist.editevent

import android.view.View
import androidx.databinding.ObservableArrayMap
import androidx.databinding.ObservableMap
import androidx.lifecycle.*
import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.data.Repo
import com.ssttkkl.fgoplanningtool.data.event.Event
import com.ssttkkl.fgoplanningtool.data.event.NormalEvent
import com.ssttkkl.fgoplanningtool.data.item.Item
import com.ssttkkl.fgoplanningtool.ui.utils.SingleLiveEvent

class EditNormalEventFragmentViewModel : ViewModel(), EditEventBaseViewModel {
    override val mode = MutableLiveData<Mode>()

    override val event = MutableLiveData<ObservableNormalEvent>()

    override val shopExpanded = MutableLiveData<Boolean>()
    override val storyExpanded = MutableLiveData<Boolean>()

    init {
        shopExpanded.value = true
        storyExpanded.value = true
    }

    override val shopEventItemCount = ObservableArrayMap<String, Long>().apply {
        event.observeForever {
            it?.checkedShopItems?.observeForever { checkedShopItems ->
                clear()
                checkedShopItems.forEach { item ->
                    this[item.item.codename] = item.count
                }
            }
        }
    }

    private var firstCreate = true

    @Synchronized
    override fun start(mode: Mode, event: Event) {
        if (firstCreate) {
            this.mode.value = mode
            this.event.value = ObservableNormalEvent(event as NormalEvent)

            firstCreate = false
        }
    }

    override val showMessageEvent = SingleLiveEvent<String>()
    override val finishEvent = SingleLiveEvent<Unit>()

    private fun prepareEvent(): NormalEvent? {
        val indexed = event.value?.checkedShopItems?.value
                ?.filter { it.checked }
                ?.associateTo(HashMap()) { it.item.codename to it }
                ?: mapOf<String, CheckableItem>()
        shopEventItemCount.forEach { (key, value) ->
            val item = indexed[key]
            if (item != null && item.checked && (value == null || value !in 0..item.item.count)) {
                showMessageEvent.call(MyApp.context.getString(R.string.illegalValue_editevent, item.item.descriptor?.localizedName, 0, item.item.count))
                return null
            }
        }
        val checkedShopItems = shopEventItemCount.filterKeys { indexed.contains(it) }
                .map { Item(it.key, it.value) }
        return NormalEvent(event.value?.codename?.value ?: return null,
                event.value?.rerunAndParticipated?.value == true,
                checkedShopItems)
    }

    override fun onClickRemove() {
        if (mode.value == Mode.Edit) {
            Repo.eventRepo.remove(event.value?.codename?.value ?: return)
        }
        finishEvent.call()
    }

    override fun onClickSave() {
        Repo.eventRepo.insert(prepareEvent() ?: return)
        finishEvent.call()
    }

    override fun onClickSelectAllShopItems() {
        event.value?.checkedShopItems?.value = event.value?.checkedShopItems?.value?.toMutableList()?.apply {
            indices.forEach { idx ->
                if (!this[idx].checked)
                    this[idx] = CheckableItem(this[idx].item, true, this[idx].item.count)
            }
        }
    }

    override fun onClickDeselectAllShopItems() {
        event.value?.checkedShopItems?.value = event.value?.checkedShopItems?.value?.toMutableList()?.apply {
            indices.forEach { idx ->
                if (this[idx].checked)
                    this[idx] = CheckableItem(this[idx].item, false, this[idx].item.count)
            }
        }
    }

    override fun onClickResetShopItems() {
        event.value?.checkedShopItems?.value = event.value?.originEvent?.value?.descriptor?.shopItems?.map {
            CheckableItem(it, true, it.count)
        }
    }

    override fun onClickShopItemCheckBox(codename: String) {
        event.value?.checkedShopItems?.value = event.value?.checkedShopItems?.value?.toMutableList()?.apply {
            val idx = indexOfFirst { it.item.codename == codename }
            this[idx] = CheckableItem(this[idx].item, !this[idx].checked, this[idx].item.count)
        }
    }

    override fun onClickHeader(view: View) {
        when (view.id) {
            R.id.shopHeader_textView -> shopExpanded.value = shopExpanded.value != true
            R.id.storyHeader_textView -> storyExpanded.value = storyExpanded.value != true
        }
    }
}