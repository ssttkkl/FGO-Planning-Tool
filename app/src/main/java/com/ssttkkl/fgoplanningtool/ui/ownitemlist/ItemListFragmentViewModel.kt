package com.ssttkkl.fgoplanningtool.ui.ownitemlist

import androidx.databinding.ObservableArrayMap
import androidx.lifecycle.*
import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.data.HowToPerform
import com.ssttkkl.fgoplanningtool.data.Repo
import com.ssttkkl.fgoplanningtool.data.item.Item
import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider
import com.ssttkkl.fgoplanningtool.resources.itemdescriptor.ItemType
import com.ssttkkl.fgoplanningtool.ui.utils.SingleLiveEvent

class ItemListFragmentViewModel : ViewModel() {
    val showItemInfoEvent = SingleLiveEvent<String>()
    val showMessageEvent = SingleLiveEvent<String>()

    val type = MutableLiveData<ItemType>()

    private val indexedData = MutableLiveData<Map<String, EditableItem>>()

    val data: LiveData<List<EditableItem>> = Transformations.map(indexedData) { indexedData ->
        indexedData.values.sortedBy { ResourcesProvider.instance.itemRank[it.item.codename] }
    }

    val editedCount = ObservableArrayMap<String, Long>()

    private fun setItemEditing(codename: String, editing: Boolean) {
        indexedData.value = indexedData.value?.toMutableMap()?.apply {
            val oldItem = this[codename] ?: return
            this[codename] = EditableItem(oldItem.item, editing)
        }
        setItemEditedCount(codename, null)
    }

    private fun setItemEditedCount(codename: String, editedCount: Long?) {
        this.editedCount[codename] = editedCount
    }

    private val generator = { oldData: Map<String, EditableItem>?, origin: List<Item>?, type: ItemType? ->
        val map = origin?.associate { Pair(it.codename, it) } ?: mapOf()
        ResourcesProvider.instance.itemDescriptors.values.filter { it.type == type }
                .associate {
                    Pair(it.codename,
                            EditableItem(Item(it.codename, map[it.codename]?.count ?: 0),
                                    oldData?.get(it.codename)?.editing == true))
                }
    }

    private val observer = Observer<Any> {
        indexedData.value = generator(indexedData.value, Repo.itemListLiveData.value, type.value)
    }

    init {
        Repo.itemListLiveData.observeForever(observer)
        type.observeForever(observer)
    }

    override fun onCleared() {
        super.onCleared()
        Repo.itemListLiveData.removeObserver(observer)
        type.removeObserver(observer)
    }

    val showInfoButton: LiveData<Boolean> = Transformations.map(type) { type ->
        type != ItemType.General
    }

    fun onItemClickEdit(codename: String) {
        setItemEditing(codename, true)
    }

    fun onItemClickInfo(codename: String) {
        showItemInfoEvent.call(codename)
    }

    fun onItemClickReset(codename: String) {
        setItemEditedCount(codename, null)
    }

    fun onItemClickSave(codename: String) {
        val editedCount = this.editedCount[codename]
        if (editedCount != null) {
            if (editedCount !in (MIN_VALUE..MAX_VALUE)) {
                val message = MyApp.context.getString(R.string.exc_outOfBounds_edititem, MIN_VALUE, MAX_VALUE)
                showMessageEvent.call(message)
            } else {
                val newItem = Item(codename, editedCount)
                Repo.itemRepo.update(newItem, HowToPerform.Launch)
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