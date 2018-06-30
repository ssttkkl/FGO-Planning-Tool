package com.ssttkkl.fgoplanningtool.ui.editplan

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.ssttkkl.fgoplanningtool.data.plan.Plan

class EditPlanViewModel : ViewModel() {
    var firstCreate = true

    val servantId = MutableLiveData<Int>()
    val nowStage = MutableLiveData<Int>()
    val planStage = MutableLiveData<Int>()
    val nowSkillI = MutableLiveData<Int>()
    val planSkillI = MutableLiveData<Int>()
    val nowSkillII = MutableLiveData<Int>()
    val planSkillII = MutableLiveData<Int>()
    val nowSkillIII = MutableLiveData<Int>()
    val planSkillIII = MutableLiveData<Int>()

    init {
        servantId.value = 0
        nowStage.value = 0
        planStage.value = 4
        nowSkillI.value = 1
        planSkillI.value = 10
        nowSkillII.value = 1
        planSkillII.value = 10
        nowSkillIII.value = 1
        planSkillIII.value = 10
    }

    val plan: Plan
        get() = Plan(servantId.value!!,
                nowStage.value!!,
                planStage.value!!,
                nowSkillI.value!!,
                nowSkillII.value!!,
                nowSkillIII.value!!,
                planSkillI.value!!,
                planSkillII.value!!,
                planSkillIII.value!!)

    var oldPlan: Plan? = null
}