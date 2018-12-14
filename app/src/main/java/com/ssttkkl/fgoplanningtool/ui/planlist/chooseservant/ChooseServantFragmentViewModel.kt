package com.ssttkkl.fgoplanningtool.ui.planlist.chooseservant

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.ssttkkl.fgoplanningtool.data.Repo

class ChooseServantFragmentViewModel : ViewModel() {
    val hiddenServantIDs: LiveData<Set<Int>> = Transformations.map(Repo.PlanRepo.allAsLiveData) { plans ->
        plans.keys
    }
}
