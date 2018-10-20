package com.ssttkkl.fgoplanningtool.ui.editplan

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.resources.ConstantValues
import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider
import com.ssttkkl.fgoplanningtool.resources.servant.Servant
import com.ssttkkl.fgoplanningtool.ui.utils.SingleLiveEvent

class EditLevelDialogFragmentViewModel : ViewModel() {
    var extraTag: String? = null

    val servantID = MutableLiveData<Int>()

    // show only when servant isn't Mash
    val showMmaxLevelButton: LiveData<Boolean> = Transformations.map(servantID) {
        it != 1
    }

    val servant: LiveData<Servant> = Transformations.map(servantID) {
        ResourcesProvider.instance.servants[it]
    }

    val minLevel = 1

    val maxLevel: LiveData<Int> = Transformations.map(servant) {
        it?.stageMapToMaxLevel?.getOrNull(4)
    }

    val mmaxLevel: LiveData<Int> = Transformations.map(servant) {
        it?.stageMapToMaxLevel?.lastOrNull()
    }

    val level = MutableLiveData<Int>()

    // enable only when current level is a border
    val enableAscendedOnStageCheckBox = object : LiveData<Boolean>() {
        fun generateValue(level: Int?, servant: Servant?): Boolean {
            if (servant == null || level == null)
                return false
            return servant.stageMapToMaxLevel.contains(level)
                    && servant.stageMapToMaxLevel.indexOf(level) != servant.stageMapToMaxLevel.size - 1
        }

        init {
            level.observeForever { value = generateValue(level.value, servant.value) }
            servant.observeForever { value = generateValue(level.value, servant.value) }
        }

        override fun setValue(value: Boolean?) {
            if (value != this.value) // to avoid remainedExp resetting itself
                super.setValue(value)
        }
    }

    // when enableAscendedOnStageCheckBox changed to false, set it to false
    val ascendedOnStage = object : MutableLiveData<Boolean>() {
        init {
            enableAscendedOnStageCheckBox.observeForever {
                if (it == false)
                    value = false
            }
        }
    }

    // enable only when current level isn't a border or ascendedOnStage is true
    val enableRemainedExpEditText = object : LiveData<Boolean>() {
        fun generateValue(level: Int?, servant: Servant?, ascendedOnStage: Boolean?): Boolean {
            if (level == null || servant == null)
                return false
            return !(servant.stageMapToMaxLevel.contains(level)
                    && servant.stageMapToMaxLevel.indexOf(level) != servant.stageMapToMaxLevel.size - 1)
                    || ascendedOnStage == true
        }

        init {
            level.observeForever { value = generateValue(level.value, servant.value, ascendedOnStage.value) }
            servant.observeForever { value = generateValue(level.value, servant.value, ascendedOnStage.value) }
            ascendedOnStage.observeForever { value = generateValue(level.value, servant.value, ascendedOnStage.value) }
        }
    }

    // change value when level or ascendedOnStage updated
    val remainedExp = object : MutableLiveData<Int>() {
        fun generateValue(level: Int?, servant: Servant?, ascendedOnStage: Boolean?): Int? {
            if (level == null || servant == null)
                return null
            val minLevel = minLevel
            val mmaxLevel = servant.stageMapToMaxLevel.lastOrNull() ?: return null
            return if (level in (minLevel until mmaxLevel) && (!servant.stageMapToMaxLevel.contains(level) || ascendedOnStage == true))
                ConstantValues.nextLevelExp[level]
            else
                0
        }

        init {
            level.observeForever { value = generateValue(level.value, servant.value, ascendedOnStage.value) }
            servant.observeForever { value = generateValue(level.value, servant.value, ascendedOnStage.value) }
            ascendedOnStage.observeForever { value = generateValue(level.value, servant.value, ascendedOnStage.value) }
        }
    }

    val saveEvent = SingleLiveEvent<Triple<Int, Boolean, String?>>()
    val messageEvent = SingleLiveEvent<String>()

    fun onClickMinButton() {
        level.value = minLevel
    }

    fun onClickMaxButton() {
        level.value = maxLevel.value
    }

    fun onClickMmaxButton() {
        level.value = mmaxLevel.value
    }

    fun onClickSaveButton() {
        try {
            if (level.value!! !in minLevel..mmaxLevel.value!!)
                throw Exception(MyApp.context.getString(R.string.illegalLevel_editplan_editlevel))
            if (remainedExp.value!! > ConstantValues.nextLevelExp[level.value!!])
                throw Exception(MyApp.context.getString(R.string.illegalRemainedExp_editplan_editlevel))

            var exp = ConstantValues.getExp(level.value!!)
            if (!servant.value!!.stageMapToMaxLevel.contains(level.value!!) || ascendedOnStage.value == true)
                exp += ConstantValues.nextLevelExp[level.value!!] - remainedExp.value!!

            saveEvent.call(Triple(exp, ascendedOnStage.value == true, extraTag))
        } catch (exc: Exception) {
            messageEvent.call(exc.message)
        }
    }
}