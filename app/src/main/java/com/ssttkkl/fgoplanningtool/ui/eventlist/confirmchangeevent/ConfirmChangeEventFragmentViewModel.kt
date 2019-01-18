package com.ssttkkl.fgoplanningtool.ui.eventlist.confirmchangeevent

import androidx.lifecycle.*
import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.data.Repo
import com.ssttkkl.fgoplanningtool.data.event.Event
import com.ssttkkl.fgoplanningtool.data.item.Item
import com.ssttkkl.fgoplanningtool.ui.utils.SingleLiveEvent

class ConfirmChangeEventFragmentViewModel : ViewModel() {
    val mode = MutableLiveData<Mode>()
    val event = MutableLiveData<Event>()

    val title: LiveData<String> = Transformations.map(mode) { mode ->
        if (mode == Mode.Remove)
            MyApp.context.getString(R.string.confirmRemove)
        else
            MyApp.context.getString(R.string.confirmChange)
    }

    val hint = MediatorLiveData<String>().apply {
        val generator = {
            when (mode.value) {
                Mode.Remove -> MyApp.context.getString(R.string.hintOfConfirmRemoveEvent, event.value?.descriptor?.localizedName)
                Mode.Change -> MyApp.context.getString(R.string.hintOfConfirmChangeEvent, event.value?.descriptor?.localizedName)
                else -> ""
            }
        }
        addSource(mode) { value = generator() }
        addSource(event) { value = generator() }
    }

    val showUpdateItems: LiveData<Boolean> = Transformations.map(mode) { mode ->
        mode == Mode.Remove
    }

    val updateItems = MutableLiveData<Boolean>()
    
    val itemsToAdd = MediatorLiveData<List<AddItem>>().apply {
        val generator = {
            event.value?.items?.map { item ->
                val itemInRepo = Repo.ItemRepo[item.codename]
                AddItem(item.codename, itemInRepo.count, itemInRepo.count + item.count)
            }?.sortedBy { it.descriptor?.rank } ?: listOf()
        }
        addSource(Repo.ItemRepo.allAsLiveData) { value = generator() }
        addSource(event) { value = generator() }
    }

    val finishEvent = SingleLiveEvent<Unit>()

    fun onClickYes() {
        when (mode.value) {
            Mode.Remove -> {
                Repo.EventRepo.remove(event.value?.codename ?: "")
                if (updateItems.value == true)
                    Repo.ItemRepo.add(event.value?.items ?: listOf())
            }
            Mode.Change -> Repo.EventRepo.insert(event.value!!)
        }
        finishEvent.call()
    }
}