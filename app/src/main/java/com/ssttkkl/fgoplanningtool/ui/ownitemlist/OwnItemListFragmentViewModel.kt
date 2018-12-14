package com.ssttkkl.fgoplanningtool.ui.ownitemlist

import android.view.View
import androidx.databinding.ObservableArrayMap
import androidx.lifecycle.*
import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.data.Repo
import com.ssttkkl.fgoplanningtool.data.item.Item
import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider
import com.ssttkkl.fgoplanningtool.resources.itemdescriptor.ItemType
import com.ssttkkl.fgoplanningtool.ui.utils.SingleLiveEvent
import java.util.HashMap

class OwnItemListFragmentViewModel : ViewModel() {
    val showItemInfoEvent = SingleLiveEvent<String>()
    val showMessageEvent = SingleLiveEvent<String>()

    private val indexedData = MutableLiveData<Map<String, EditableItem>>()

    val data: LiveData<Map<ItemType?, List<EditableItem>>> = Transformations.map(indexedData) { indexedData ->
        indexedData.values.sortedBy { ResourcesProvider.instance.itemRank[it.item.codename] }
                .groupBy { it.item.descriptor?.type }
    }

    val editedCount = ObservableArrayMap<String, Long>()

    private fun setItemEditing(codename: String, editing: Boolean) {
        indexedData.value = indexedData.value?.toMutableMap()?.apply {
            val oldItem = this[codename] ?: return
            this[codename] = EditableItem(oldItem.item, editing)
            setItemEditedCount(codename, oldItem.item.count)
        }
    }

    private fun setItemEditedCount(codename: String, editedCount: Long?) {
        this.editedCount[codename] = editedCount
    }

    val withEventItems = MutableLiveData<Boolean>()
    val indexedEventItems: LiveData<Map<String, Item>> = Transformations.map(Repo.EventRepo.allAsLiveData) { events ->
        val map = HashMap<String, Long>()
        events.values.forEach { event ->
            event.items.forEach { (codename, count) ->
                map[codename] = (map[codename] ?: 0) + count
            }
        }
        map.mapValues { Item(it.key, it.value) }
    }

    private val observer = Observer<Any> { _ ->
        val map = Repo.ItemRepo.allAsLiveData.value ?: mapOf()
        indexedData.value = Repo.ItemRepo.allAsLiveData.value?.mapValues { (_, item) ->
            EditableItem(Item(item.codename, map[item.codename]?.count ?: 0),
                    indexedData.value?.get(item.codename)?.editing == true)
        }
    }

    init {
        Repo.ItemRepo.allAsLiveData.observeForever(observer)
    }

    override fun onCleared() {
        super.onCleared()
        Repo.ItemRepo.allAsLiveData.removeObserver(observer)
    }

    fun getInfoButtonVisibility(item: Item?): Int {
        return if (item?.descriptor?.type == ItemType.General)
            View.GONE
        else
            View.VISIBLE
    }

    fun onItemClickEdit(codename: String) {
        setItemEditing(codename, true)
    }

    fun onItemClickInfo(codename: String) {
        showItemInfoEvent.call(codename)
    }

    fun onItemClickReset(codename: String) {
        setItemEditedCount(codename, indexedData.value?.get(codename)?.item?.count)
    }

    fun onItemClickSave(codename: String) {
        val editedCount = this.editedCount[codename]
        if (editedCount != null) {
            if (editedCount !in (MIN_VALUE..MAX_VALUE)) {
                val message = MyApp.context.getString(R.string.exc_outOfBounds_edititem, MIN_VALUE, MAX_VALUE)
                showMessageEvent.call(message)
            } else {
                val newItem = Item(codename, editedCount)
                Repo.ItemRepo.update(newItem)
                setItemEditing(codename, false)
            }
        } else
            setItemEditing(codename, false)
    }

    companion object {
        private const val MAX_VALUE = 999_999_999
        private const val MIN_VALUE = 0
    }
}