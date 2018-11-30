package com.ssttkkl.fgoplanningtool.ui.planlist.editplan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.ssttkkl.fgoplanningtool.data.plan.Plan
import com.ssttkkl.fgoplanningtool.resources.ConstantValues
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