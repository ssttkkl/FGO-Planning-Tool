package com.ssttkkl.fgoplanningtool.ui.servantlist

import android.content.Context
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.preference.PreferenceManager
import com.ssttkkl.fgoplanningtool.data.item.Item
import com.ssttkkl.fgoplanningtool.data.item.costItems
import com.ssttkkl.fgoplanningtool.data.plan.Plan
import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider
import com.ssttkkl.fgoplanningtool.resources.servant.Servant
import com.ssttkkl.fgoplanningtool.ui.utils.SingleLiveEvent
import java.lang.ref.WeakReference

class ServantListFragmentViewModel : ViewModel() {
    private var context: WeakReference<Context>? = null

    val data = MutableLiveData<List<Servant>>()
    val hiddenServantIDs = MutableLiveData<Set<Int>>()
    val originServantIDs: LiveData<Set<Int>> = Transformations.map(hiddenServantIDs) { hiddenServantIDs ->
        ResourcesProvider.instance.servants.keys - hiddenServantIDs
    }
    val viewType = MutableLiveData<ViewType>().apply {
        observeForever {
            PreferenceManager.getDefaultSharedPreferences(context?.get()
                    ?: return@observeForever)?.edit {
                putString(KEY_VIEW_TYPE, (value ?: DEFAULT_VIEW_TYPE).name)
            }
        }
    }
    val isDefaultFilterState = MutableLiveData<Boolean>()

    val informClickServantEvent = SingleLiveEvent<Int>()

    fun start(context: Context) {
        this.context = WeakReference(context)

        PreferenceManager.getDefaultSharedPreferences(context)?.apply {
            viewType.value = ViewType.valueOf(getString(KEY_VIEW_TYPE, DEFAULT_VIEW_TYPE.name)!!)
        }
    }

    fun onClickServant(servantID: Int) {
        informClickServantEvent.call(servantID)
    }

    fun onFiltered(filtered: List<Servant>, isDefaultState: Boolean) {
        isDefaultFilterState.value = isDefaultState
        data.value = filtered
    }

    fun onRequestCostItems(servant: Servant): Collection<Item> {
        return Plan(servant.id,
                0, 0, false, false,
                1, 1, 1, 10, 10, 10,
                servant.dress.indices.toSet()).costItems
    }

    fun onClickSwitchToListView() {
        viewType.value = ViewType.List
    }

    fun onClickSwitchToGridView() {
        viewType.value = ViewType.Grid
    }

    companion object {
        private const val KEY_VIEW_TYPE = "servant_list_view_type"

        private val DEFAULT_VIEW_TYPE = ViewType.List
    }
}