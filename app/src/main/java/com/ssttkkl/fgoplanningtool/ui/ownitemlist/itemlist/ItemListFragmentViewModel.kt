package com.ssttkkl.fgoplanningtool.ui.ownitemlist.itemlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.databinding.ObservableArrayMap
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

    val data = object : LiveData<List<Item>>() {
        fun generateData(origin: List<Item>?, type: ItemType?): List<Item>? {
            return if (origin == null)
                null
            else {
                val map = origin.associate { Pair(it.codename, it) }
                ResourcesProvider.instance.itemDescriptors.values.filter { it.type == type }
                        .map { Item(it.codename, map[it.codename]?.count ?: 0) }
                        .sortedBy { ResourcesProvider.instance.itemRank[it.codename] }
            }
        }

        init {
            Repo.itemListLiveData.observeForever { value = generateData(it, type.value) }
            type.observeForever { value = generateData(Repo.itemListLiveData.value, it) }
        }
    }

    val showInfoButton = object : LiveData<Boolean>() {
        init {
            type.observeForever { value = (it != null && it != ItemType.General) }
        }
    }

    val inEditMode = MutableLiveData<Set<String>>()

    val editedItems = ObservableArrayMap<String, Long>()

    fun onItemClickEdit(codename: String) {
        inEditMode.value = inEditMode.value?.plus(codename) ?: setOf(codename)
    }

    fun onItemClickInfo(codename: String) {
        showItemInfoEvent.call(codename)
    }

    fun onItemClickReset(codename: String) {
        editedItems[codename] = null
    }

    fun onItemClickSave(codename: String) {
        val newValue = editedItems[codename]
        if (newValue != null && newValue !in (MIN_VALUE..MAX_VALUE)) {
            val message = MyApp.context.getString(R.string.exc_outOfBounds_edititem, MIN_VALUE, MAX_VALUE)
            showMessageEvent.call(message)
        } else {
            inEditMode.value = inEditMode.value?.minus(codename)

            val newItem = Item(codename, newValue ?: return)
            Repo.itemRepo.update(newItem, HowToPerform.Launch)
            editedItems.remove(codename)
        }
    }

    companion object {
        private const val MAX_VALUE = 999_999_999
        private const val MIN_VALUE = 0
    }
}