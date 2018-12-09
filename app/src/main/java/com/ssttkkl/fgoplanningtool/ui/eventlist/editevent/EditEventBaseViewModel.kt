package com.ssttkkl.fgoplanningtool.ui.eventlist.editevent

import android.view.View
import androidx.databinding.ObservableArrayMap
import androidx.lifecycle.MutableLiveData
import com.ssttkkl.fgoplanningtool.data.event.Event
import com.ssttkkl.fgoplanningtool.ui.utils.SingleLiveEvent

interface EditEventBaseViewModel {
    val mode: MutableLiveData<Mode>
    val event: MutableLiveData<out ObservableEvent>

    val shopExpanded: MutableLiveData<Boolean>
    val storyExpanded: MutableLiveData<Boolean>
    val shopEventItemCount: ObservableArrayMap<String, Long>

    fun start(mode: Mode, event: Event)

    val showMessageEvent: SingleLiveEvent<String>
    val finishEvent: SingleLiveEvent<Unit>

    fun onClickRemove()
    fun onClickSave()
    fun onClickSelectAllShopItems()
    fun onClickDeselectAllShopItems()
    fun onClickResetShopItems()
    fun onClickShopItemCheckBox(codename: String)
    fun onClickHeader(view: View)
}