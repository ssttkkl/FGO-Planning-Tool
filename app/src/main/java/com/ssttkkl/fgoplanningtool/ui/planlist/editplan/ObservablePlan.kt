package com.ssttkkl.fgoplanningtool.ui.planlist.editplan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.ssttkkl.fgoplanningtool.data.plan.Plan
import com.ssttkkl.fgoplanningtool.resources.LevelValues
import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider
import com.ssttkkl.fgoplanningtool.resources.servant.Dress
import com.ssttkkl.fgoplanningtool.resources.servant.Servant

class ObservablePlan(_plan: Plan) {
    enum class SkillLevel {
        One, Two, Three, Four, Five, Six, Seven, Eight, Nine, Ten
    }

    val servantId = MutableLiveData<Int>().apply { value = _plan.servantId }
    val nowExp = MutableLiveData<Int>().apply { value = _plan.nowExp }
    val planExp = MutableLiveData<Int>().apply { value = _plan.planExp }
    val ascendedOnNowStage = MutableLiveData<Boolean>().apply { value = _plan.ascendedOnNowStage }
    val ascendedOnPlanStage = MutableLiveData<Boolean>().apply { value = _plan.ascendedOnPlanStage }
    val nowSkill1 = MutableLiveData<SkillLevel>().apply { value = SkillLevel.values()[_plan.nowSkill1 - 1] }
    val nowSkill2 = MutableLiveData<SkillLevel>().apply { value = SkillLevel.values()[_plan.nowSkill2 - 1] }
    val nowSkill3 = MutableLiveData<SkillLevel>().apply { value = SkillLevel.values()[_plan.nowSkill3 - 1] }
    val planSkill1 = MutableLiveData<SkillLevel>().apply { value = SkillLevel.values()[_plan.planSkill1 - 1] }
    val planSkill2 = MutableLiveData<SkillLevel>().apply { value = SkillLevel.values()[_plan.planSkill2 - 1] }
    val planSkill3 = MutableLiveData<SkillLevel>().apply { value = SkillLevel.values()[_plan.planSkill3 - 1] }
    val dress = MutableLiveData<Set<Dress>>().apply { value = _plan.dress }

    val servant: LiveData<Servant> = Transformations.map(servantId) {
        ResourcesProvider.instance.servants[it]
    }

    val nowLevel: LiveData<Int> = Transformations.map(nowExp) {
        if (it != null)
            LevelValues.expToLevel(it)
        else
            null
    }

    val planLevel: LiveData<Int> = Transformations.map(planExp) {
        if (it != null)
            LevelValues.expToLevel(it)
        else
            null
    }

    val nowLevelProgress = MediatorLiveData<Int>().apply {
        val generator = { servant: Servant?, nowExp: Int?, ascendedOnNowStage: Boolean? ->
            if (servant == null || nowExp == null)
                null
            else
                LevelValues.expToCurLevelExp(servant.star, nowExp, ascendedOnNowStage == true)
        }
        addSource(servant) { value = generator(servant.value, nowExp.value, ascendedOnNowStage.value) }
        addSource(nowExp) { value = generator(servant.value, nowExp.value, ascendedOnNowStage.value) }
        addSource(ascendedOnNowStage) { value = generator(servant.value, nowExp.value, ascendedOnNowStage.value) }
    }

    val nowLevelMaxProgress = MediatorLiveData<Int>().apply {
        val generator = { servant: Servant?, nowExp: Int?, ascendedOnNowStage: Boolean? ->
            if (servant == null || nowExp == null)
                null
            else
                LevelValues.expToCurLevelMaxExp(servant.star, nowExp, ascendedOnNowStage == true)
        }
        addSource(servant) { value = generator(servant.value, nowExp.value, ascendedOnNowStage.value) }
        addSource(nowExp) { value = generator(servant.value, nowExp.value, ascendedOnNowStage.value) }
        addSource(ascendedOnNowStage) { value = generator(servant.value, nowExp.value, ascendedOnNowStage.value) }
    }

    val planLevelProgress = MediatorLiveData<Int>().apply {
        val generator = { servant: Servant?, planExp: Int?, ascendedOnPlanStage: Boolean? ->
            if (servant == null || planExp == null)
                null
            else
                LevelValues.expToCurLevelExp(servant.star, planExp, ascendedOnPlanStage == true)
        }
        addSource(servant) { value = generator(servant.value, planExp.value, ascendedOnPlanStage.value) }
        addSource(planExp) { value = generator(servant.value, planExp.value, ascendedOnPlanStage.value) }
        addSource(ascendedOnPlanStage) { value = generator(servant.value, planExp.value, ascendedOnPlanStage.value) }
    }

    val planLevelMaxProgress = MediatorLiveData<Int>().apply {
        val generator = { servant: Servant?, planExp: Int?, ascendedOnPlanStage: Boolean? ->
            if (servant == null || planExp == null)
                null
            else
                LevelValues.expToCurLevelMaxExp(servant.star, planExp, ascendedOnPlanStage == true)
        }
        addSource(servant) { value = generator(servant.value, planExp.value, ascendedOnPlanStage.value) }
        addSource(planExp) { value = generator(servant.value, planExp.value, ascendedOnPlanStage.value) }
        addSource(ascendedOnPlanStage) { value = generator(servant.value, planExp.value, ascendedOnPlanStage.value) }
    }

    val plan: Plan?
        get() {
            return Plan(servantId = servantId.value ?: return null,
                    nowExp = nowExp.value ?: return null,
                    planExp = planExp.value ?: return null,
                    ascendedOnNowStage = ascendedOnNowStage.value == true,
                    ascendedOnPlanStage = ascendedOnPlanStage.value == true,
                    nowSkill1 = nowSkill1.value?.ordinal?.plus(1) ?: return null,
                    nowSkill2 = nowSkill2.value?.ordinal?.plus(1) ?: return null,
                    nowSkill3 = nowSkill3.value?.ordinal?.plus(1) ?: return null,
                    planSkill1 = planSkill1.value?.ordinal?.plus(1) ?: return null,
                    planSkill2 = planSkill2.value?.ordinal?.plus(1) ?: return null,
                    planSkill3 = planSkill3.value?.ordinal?.plus(1) ?: return null,
                    dressID = setOf()).apply {
                dress = this@ObservablePlan.dress.value ?: setOf()
            }
        }
}