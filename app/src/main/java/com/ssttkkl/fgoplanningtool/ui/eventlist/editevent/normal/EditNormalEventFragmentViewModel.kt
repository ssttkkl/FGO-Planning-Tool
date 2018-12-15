package com.ssttkkl.fgoplanningtool.ui.eventlist.editevent.normal

import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.data.event.Event
import com.ssttkkl.fgoplanningtool.data.event.NormalEvent
import com.ssttkkl.fgoplanningtool.data.item.Item
import com.ssttkkl.fgoplanningtool.ui.eventlist.editevent.CheckableItem
import com.ssttkkl.fgoplanningtool.ui.eventlist.editevent.EditEventBaseViewModel
import com.ssttkkl.fgoplanningtool.ui.eventlist.editevent.Mode
import kotlin.collections.component1
import kotlin.collections.component2

class EditNormalEventFragmentViewModel : EditEventBaseViewModel() {
    override lateinit var event: ObservableNormalEvent

    @Synchronized
    override fun start(mode: Mode, event: Event) {
        if (!initialized) {
            this.mode.value = mode
            this.event = ObservableNormalEvent(event as NormalEvent)
            initialized = true
        }
    }

    override fun prepareEvent(): NormalEvent? {
        val indexed = event.checkedShopItems.value
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
        return NormalEvent(event.codename,
                event.rerunAndParticipated.value == true,
                checkedShopItems)
    }
}