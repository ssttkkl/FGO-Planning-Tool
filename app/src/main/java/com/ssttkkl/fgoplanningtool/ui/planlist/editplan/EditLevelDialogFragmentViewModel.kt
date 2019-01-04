package com.ssttkkl.fgoplanningtool.ui.planlist.editplan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.resources.LevelValues
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
    val enableAscendedOnStageCheckBox = MediatorLiveData<Boolean>().apply {
        val generator = { level: Int?, servant: Servant? ->
            if (servant == null || level == null)
                false
            else
                servant.stageMapToMaxLevel.contains(level)
                        && servant.stageMapToMaxLevel.indexOf(level) != servant.stageMapToMaxLevel.size - 1
        }
        addSource(level) { value = generator(level.value, servant.value) }
        addSource(servant) { value = generator(level.value, servant.value) }
    }

    // when enableAscendedOnStageCheckBox changed to false, set it to false
    val ascendedOnStage = MutableLiveData<Boolean>().apply {
        enableAscendedOnStageCheckBox.observeForever {
            if (it != true)
                value = false
        }
    }

    // enable only when current level isn't a border or ascendedOnStage is true
    val enableRemainedExpEditText = MediatorLiveData<Boolean>().apply {
        val generator = { level: Int?, servant: Servant?, ascendedOnStage: Boolean? ->
            if (level == null || servant == null)
                false
            else
                !(servant.stageMapToMaxLevel.contains(level)
                        && servant.stageMapToMaxLevel.indexOf(level) != servant.stageMapToMaxLevel.size - 1)
                        || ascendedOnStage == true
        }
        addSource(level) { value = generator(level.value, servant.value, ascendedOnStage.value) }
        addSource(servant) { value = generator(level.value, servant.value, ascendedOnStage.value) }
        addSource(ascendedOnStage) { value = generator(level.value, servant.value, ascendedOnStage.value) }
    }

    // change value when level or ascendedOnStage updated
    val remainedExp = MutableLiveData<Int>().apply {
        val generator = { level: Int?, servant: Servant?, ascendedOnStage: Boolean? ->
            val minLevel = minLevel
            val mmaxLevel = servant?.stageMapToMaxLevel?.lastOrNull()

            if (level != null && servant != null && mmaxLevel != null) {
                if (level in (minLevel until mmaxLevel) && (!servant.stageMapToMaxLevel.contains(level) || ascendedOnStage == true))
                    LevelValues.nextLevelExp[level]
                else
                    0
            } else
                null
        }

        level.observeForever { value = generator(level.value, servant.value, ascendedOnStage.value) }
        servant.observeForever { value = generator(level.value, servant.value, ascendedOnStage.value) }
        ascendedOnStage.observeForever { value = generator(level.value, servant.value, ascendedOnStage.value) }
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
                throw Exception(MyApp.context.getString(R.string.illegalLevel))
            if (level.value!! < 100 && remainedExp.value!! > LevelValues.nextLevelExp[level.value!!])
                throw Exception(MyApp.context.getString(R.string.illegalRemainedExp))

            var exp = LevelValues.levelToExp(level.value!!)
            if (!servant.value!!.stageMapToMaxLevel.contains(level.value!!) || ascendedOnStage.value == true)
                exp += LevelValues.nextLevelExp[level.value!!] - remainedExp.value!!

            saveEvent.call(Triple(exp, ascendedOnStage.value == true, extraTag))
        } catch (exc: Exception) {
            messageEvent.call(exc.toString())
        }
    }

    fun onClickCancelButton() {
        cancelEvent.call()
    }
}