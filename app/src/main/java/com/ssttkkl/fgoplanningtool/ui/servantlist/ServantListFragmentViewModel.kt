package com.ssttkkl.fgoplanningtool.ui.servantlist

import android.content.Context
import androidx.core.content.edit
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.preference.PreferenceManager
import com.ssttkkl.fgoplanningtool.PreferenceKeys
import com.ssttkkl.fgoplanningtool.data.item.Item
import com.ssttkkl.fgoplanningtool.data.item.costItems
import com.ssttkkl.fgoplanningtool.data.plan.Plan
import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider
import com.ssttkkl.fgoplanningtool.resources.servant.Servant
import com.ssttkkl.fgoplanningtool.ui.utils.SingleLiveEvent
import java.lang.ref.WeakReference

class ServantListFragmentViewModel : ViewModel() {
    private var context = WeakReference<Context>(null)
    private var prefLabel = ""

    private val keyViewType
        get() = PreferenceKeys.KEY_SERVANT_LIST_VIEW_TYPE.format(prefLabel)

    val data = MutableLiveData<List<Servant>>()
    val hiddenServantIDs = MutableLiveData<Set<Int>>()
    val originServantIDs: Set<Int> = ResourcesProvider.instance.servants.keys
    val viewType = MutableLiveData<ViewType>()
    val isDefaultFilterState = MutableLiveData<Boolean>()

    init {
        viewType.observeForever {
            PreferenceManager.getDefaultSharedPreferences(context.get()
                    ?: return@observeForever)?.edit {
                putString(keyViewType, (it ?: DEFAULT_VIEW_TYPE).name)
            }
        }
    }

    val informClickServantEvent = SingleLiveEvent<Int>()

    fun start(context: Context, prefLabel: String) {
        this.context = WeakReference(context)
        this.prefLabel = prefLabel

        try {
            PreferenceManager.getDefaultSharedPreferences(context)?.apply {
                viewType.value = ViewType.valueOf(getString(keyViewType, DEFAULT_VIEW_TYPE.name)!!)
            }
        } catch (exc: Exception) {
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
        private val DEFAULT_VIEW_TYPE = ViewType.List
    }
}