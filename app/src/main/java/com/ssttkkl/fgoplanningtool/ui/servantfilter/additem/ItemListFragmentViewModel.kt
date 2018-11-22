package com.ssttkkl.fgoplanningtool.ui.servantfilter.additem

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider
import com.ssttkkl.fgoplanningtool.resources.itemdescriptor.ItemDescriptor
import com.ssttkkl.fgoplanningtool.resources.itemdescriptor.ItemType
import com.ssttkkl.fgoplanningtool.ui.utils.SingleLiveEvent

class ItemListFragmentViewModel : ViewModel() {
    val itemType = MutableLiveData<ItemType>()

    val data: LiveData<List<ItemDescriptor>> = Transformations.map(itemType) { itemType ->
        ResourcesProvider.instance.itemDescriptors.filterValues { it.type == itemType }.values.sortedBy { it.rank }
    }

    val returnValueEvent = SingleLiveEvent<String>()

    fun onClickItem(codename: String) {
        returnValueEvent.call(codename)
    }
}