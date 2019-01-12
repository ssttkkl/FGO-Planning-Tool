package com.ssttkkl.fgoplanningtool.ui.eventlist.confirmchangeevent

import androidx.lifecycle.*
import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.data.Repo
import com.ssttkkl.fgoplanningtool.data.event.Event
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

    val finishEvent = SingleLiveEvent<Unit>()

    fun onClickYes() {
        when (mode.value) {
            Mode.Remove -> Repo.EventRepo.remove(event.value?.codename ?: "")
            Mode.Change -> Repo.EventRepo.insert(event.value!!)
        }
        finishEvent.call()
    }
}