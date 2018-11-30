package com.ssttkkl.fgoplanningtool.ui.servantinfo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ServantInfoLevelListViewModel : ViewModel() {
    val data = MutableLiveData<List<ServantInfoLevelListEntity>>()
    val expandedEntities = MutableLiveData<Set<ServantInfoLevelListEntity>>()

    fun onClickHeader(entity: ServantInfoLevelListEntity) {
        expandedEntities.value = expandedEntities.value?.toMutableSet()?.apply {
            if (contains(entity))
                remove(entity)
            else
                add(entity)
        } ?: setOf(entity)
    }
}