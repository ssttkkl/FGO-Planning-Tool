package com.ssttkkl.fgoplanningtool.ui.servantinfo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ServantInfoLevelListViewModel : ViewModel() {
    val data = MutableLiveData<List<ServantInfoLevelListEntity>>()
    val expandedEntities = MutableLiveData<Set<ServantInfoLevelListEntity>>()

    @Synchronized
    fun onClickHeader(entity: ServantInfoLevelListEntity) {
        val alreadyExpanded = expandedEntities.value?.contains(entity) == true
        expandedEntities.value = expandedEntities.value?.toMutableSet()?.apply {
            if (alreadyExpanded)
                remove(entity)
            else
                add(entity)
        } ?: if (alreadyExpanded) null else setOf(entity)
    }
}