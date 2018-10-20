package com.ssttkkl.fgoplanningtool.ui.editplan

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import com.ssttkkl.fgoplanningtool.data.item.Item
import com.ssttkkl.fgoplanningtool.data.item.costItems
import com.ssttkkl.fgoplanningtool.data.plan.Plan
import com.ssttkkl.fgoplanningtool.resources.ConstantValues
import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider
import com.ssttkkl.fgoplanningtool.resources.servant.Servant

class ObservablePlan(_plan: Plan) {
    enum class SkillLevel {
        One, Two, Three, Four, Five, Six, Seven, Eight, Nine, Ten
    }

    var servantId = MutableLiveData<Int>().apply { value = _plan.servantId }
    var nowExp = MutableLiveData<Int>().apply { value = _plan.nowExp }
    var planExp = MutableLiveData<Int>().apply { value = _plan.planExp }
    var ascendedOnNowStage = MutableLiveData<Boolean>().apply { value = _plan.ascendedOnNowStage }
    var ascendedOnPlanStage = MutableLiveData<Boolean>().apply { value = _plan.ascendedOnPlanStage }
    var nowSkill1 = MutableLiveData<SkillLevel>().apply { value = SkillLevel.values()[_plan.nowSkill1 - 1] }
    var nowSkill2 = MutableLiveData<SkillLevel>().apply { value = SkillLevel.values()[_plan.nowSkill2 - 1] }
    var nowSkill3 = MutableLiveData<SkillLevel>().apply { value = SkillLevel.values()[_plan.nowSkill3 - 1] }
    var planSkill1 = MutableLiveData<SkillLevel>().apply { value = SkillLevel.values()[_plan.planSkill1 - 1] }
    var planSkill2 = MutableLiveData<SkillLevel>().apply { value = SkillLevel.values()[_plan.planSkill2 - 1] }
    var planSkill3 = MutableLiveData<SkillLevel>().apply { value = SkillLevel.values()[_plan.planSkill3 - 1] }
    var dress = MutableLiveData<Set<Int>>().apply { value = _plan.dress }

    val servant: LiveData<Servant> = Transformations.map(servantId) {
        ResourcesProvider.instance.servants[it]
    }

    val nowLevel: LiveData<Int> = Transformations.map(nowExp) {
        if (it != null)
            ConstantValues.getLevel(it)
        else
            null
    }

    val planLevel: LiveData<Int> = Transformations.map(planExp) {
        if (it != null)
            ConstantValues.getLevel(it)
        else
            null
    }

    val nowLevelProgress = object : LiveData<Int>() {
        private fun generateValue(servant: Servant?, nowExp: Int?, ascendedOnNowStage: Boolean?): Int? {
            if (servant == null || nowExp == null)
                return null
            val level = ConstantValues.getLevel(nowExp)
            return if (!servant.stageMapToMaxLevel.contains(level) || ascendedOnNowStage == true)
                ConstantValues.nextLevelExp[level] - (ConstantValues.getExp(level + 1) - nowExp)
            else
                ConstantValues.nextLevelExp[level - 1]
        }

        private fun onChanged() {
            value = generateValue(servant.value, nowExp.value, ascendedOnNowStage.value)
        }

        init {
            servant.observeForever { onChanged() }
            nowExp.observeForever { onChanged() }
            ascendedOnNowStage.observeForever { onChanged() }
        }
    }

    val nowLevelMaxProgress = object : LiveData<Int>() {
        private fun generateValue(servant: Servant?, nowLevel: Int?, ascendedOnNowStage: Boolean?): Int? {
            if (servant == null || nowLevel == null)
                return null
            return if (!servant.stageMapToMaxLevel.contains(nowLevel) || ascendedOnNowStage == true)
                ConstantValues.nextLevelExp[nowLevel]
            else
                ConstantValues.nextLevelExp[nowLevel - 1]
        }

        private fun onChanged() {
            value = generateValue(servant.value, nowLevel.value, ascendedOnNowStage.value)
        }

        init {
            servant.observeForever { onChanged() }
            nowLevel.observeForever { onChanged() }
            ascendedOnNowStage.observeForever { onChanged() }
        }
    }

    val planLevelProgress = object : LiveData<Int>() {
        private fun generateValue(servant: Servant?, planExp: Int?, ascendedOnPlanStage: Boolean?): Int? {
            if (servant == null || planExp == null)
                return null
            val level = ConstantValues.getLevel(planExp)
            return if (!servant.stageMapToMaxLevel.contains(level) || ascendedOnPlanStage == true)
                ConstantValues.nextLevelExp[level] - (ConstantValues.getExp(level + 1) - planExp)
            else
                ConstantValues.nextLevelExp[level - 1]
        }

        private fun onChanged() {
            value = generateValue(servant.value, planExp.value, ascendedOnPlanStage.value)
        }

        init {
            servant.observeForever { onChanged() }
            planExp.observeForever { onChanged() }
            ascendedOnPlanStage.observeForever { onChanged() }
        }
    }

    val planLevelMaxProgress = object : LiveData<Int>() {
        private fun generateValue(servant: Servant?, planLevel: Int?, ascendedOnPlanStage: Boolean?): Int? {
            if (servant == null || planLevel == null)
                return null
            return if (!servant.stageMapToMaxLevel.contains(planLevel) || ascendedOnPlanStage == true)
                ConstantValues.nextLevelExp[planLevel]
            else
                ConstantValues.nextLevelExp[planLevel - 1]
        }

        private fun onChanged() {
            value = generateValue(servant.value, planLevel.value, ascendedOnPlanStage.value)
        }

        init {
            servant.observeForever { onChanged() }
            planLevel.observeForever { onChanged() }
            ascendedOnPlanStage.observeForever { onChanged() }
        }
    }

    val plan = object : LiveData<Plan>() {
        private fun generatePlan(): Plan? {
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
                    dress = dress.value ?: setOf())
        }

        init {
            servantId.observeForever { value = generatePlan() }
            nowExp.observeForever { value = generatePlan() }
            planExp.observeForever { value = generatePlan() }
            ascendedOnNowStage.observeForever { value = generatePlan() }
            ascendedOnPlanStage.observeForever { value = generatePlan() }
            nowSkill1.observeForever { value = generatePlan() }
            nowSkill2.observeForever { value = generatePlan() }
            nowSkill3.observeForever { value = generatePlan() }
            planSkill1.observeForever { value = generatePlan() }
            planSkill2.observeForever { value = generatePlan() }
            planSkill3.observeForever { value = generatePlan() }
            dress.observeForever { value = generatePlan() }
        }
    }

    val costItems: LiveData<Collection<Item>> = Transformations.map(plan) {
        it?.costItems ?: listOf()
    }
}