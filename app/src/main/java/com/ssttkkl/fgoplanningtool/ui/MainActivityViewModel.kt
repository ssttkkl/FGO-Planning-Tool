package com.ssttkkl.fgoplanningtool.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ssttkkl.fgoplanningtool.data.Repo
import com.ssttkkl.fgoplanningtool.ui.utils.SingleLiveEvent

class MainActivityViewModel : ViewModel() {
    val drawerState = MutableLiveData<Boolean>()
    val title = MutableLiveData<String>()
    val databaseDescriptor get() = Repo.databaseDescriptor

    val gotoDatabaseManageUIEvent = SingleLiveEvent<Unit>()

    fun onClickManageDatabases() {
        gotoDatabaseManageUIEvent.call()
    }
}