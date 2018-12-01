package com.ssttkkl.fgoplanningtool.ui.planlist.editplan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
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
    val ascendedOnStage = MutableLiveData<Boolean>().apply {
        enableAscendedOnStageCheckBox.observeForever {
            if (it == false)
                value = false
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
    val remainedExp = MutableLiveData<Int>().apply {
        val generator = {
            val level = level.value
            val servant = servant.value
            val minLevel = minLevel
            val mmaxLevel = servant?.stageMapToMaxLevel?.lastOrNull()

            if (level != null && servant != null && mmaxLevel != null) {
                if (level in (minLevel until mmaxLevel) && (!servant.stageMapToMaxLevel.contains(level) || ascendedOnStage.value == true))
                    ConstantValues.nextLevelExp[level]
                else
                    0
            } else
                null
        }

        level.observeForever { value = generator() }
        servant.observeForever { value = generator() }
        ascendedOnStage.observeForever { value = generator() }
    }

    val saveEvent = SingleLiveEvent<Triple<Int, Boolean, String?>>()
    val messageEvent = SingleLiveEvent<String>()
    val cancelEvent = SingleLiveEvent<Void>()

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
            if (level.value!! < 100 && remainedExp.value!! > ConstantValues.nextLevelExp[level.value!!])
                throw Exception(MyApp.context.getString(R.string.illegalRemainedExp_editplan_editlevel))

            var exp = ConstantValues.getExp(level.value!!)
            if (!servant.value!!.stageMapToMaxLevel.contains(level.value!!) || ascendedOnStage.value == true)
                exp += ConstantValues.nextLevelExp[level.value!!] - remainedExp.value!!

            saveEvent.call(Triple(exp, ascendedOnStage.value == true, extraTag))
        } catch (exc: Exception) {
            messageEvent.call(exc.message)
        }
    }

    fun onClickCancelButton() {
        cancelEvent.call()
    }
}