package com.ssttkkl.fgoplanningtool.ui.editplan

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.ssttkkl.fgoplanningtool.data.plan.Plan

class EditPlanViewModel : ViewModel() {
    var firstCreate = true
    var mode: EditPlanActivity.Mode = EditPlanActivity.Mode.New

    val servantId = MutableLiveData<Int>()
    val nowExp = MutableLiveData<Int>()
    val planExp = MutableLiveData<Int>()
    val ascendedOnNowStage = MutableLiveData<Boolean>()
    val ascendedOnPlanStage = MutableLiveData<Boolean>()
    val nowSkillI = MutableLiveData<Int>()
    val planSkillI = MutableLiveData<Int>()
    val nowSkillII = MutableLiveData<Int>()
    val planSkillII = MutableLiveData<Int>()
    val nowSkillIII = MutableLiveData<Int>()
    val planSkillIII = MutableLiveData<Int>()
    val dress = MutableLiveData<Set<Int>>()

    init {
        servantId.value = 0
        nowExp.value = 0
        planExp.value = 0
        ascendedOnNowStage.value = false
        ascendedOnPlanStage.value = false
        nowSkillI.value = 1
        nowSkillII.value = 1
        nowSkillIII.value = 1
        planSkillI.value = 10
        planSkillII.value = 10
        planSkillIII.value = 10
        dress.value = setOf()
    }

    val plan: Plan
        get() = Plan(servantId = servantId.value!!,
                nowExp = nowExp.value!!,
                planExp = planExp.value!!,
                ascendedOnNowStage = ascendedOnNowStage.value!!,
                ascendedOnPlanStage = ascendedOnPlanStage.value!!,
                nowSkill1 = nowSkillI.value!!,
                nowSkill2 = nowSkillII.value!!,
                nowSkill3 = nowSkillIII.value!!,
                planSkill1 = planSkillI.value!!,
                planSkill2 = planSkillII.value!!,
                planSkill3 = planSkillIII.value!!,
                dress = dress.value!!)

    var oldPlan: Plan? = null
}