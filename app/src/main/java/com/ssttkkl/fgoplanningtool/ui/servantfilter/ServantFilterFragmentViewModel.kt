package com.ssttkkl.fgoplanningtool.ui.servantfilter

import android.preference.PreferenceManager
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.PreferenceKeys
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.data.item.Item
import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider
import com.ssttkkl.fgoplanningtool.resources.itemdescriptor.ItemDescriptor
import com.ssttkkl.fgoplanningtool.resources.servant.Servant
import com.ssttkkl.fgoplanningtool.resources.servant.ServantClass
import com.ssttkkl.fgoplanningtool.resources.servant.WayToGet
import com.ssttkkl.fgoplanningtool.ui.utils.SingleLiveEvent

class ServantFilterFragmentViewModel : ViewModel() {
    private var costItemGetter: ((Servant) -> Collection<Item>)? = null

    val origin = MutableLiveData<Set<Servant>>()
    val searchText = MutableLiveData<String>()
    val order = MutableLiveData<Order>()
    val orderBy = MutableLiveData<OrderBy>()
    val stars = MutableLiveData<Set<Int>>()
    val servantClasses = MutableLiveData<Set<ServantClass>>()
    val waysToGet = MutableLiveData<Set<WayToGet>>()
    val items = MutableLiveData<Set<ItemDescriptor>>()
    val itemFilterMode = MutableLiveData<ItemFilterMode>()

    init {
        searchText.value = ""
        order.value = DEFAULT_ORDER
        orderBy.value = DEFAULT_ORDER_BY
        stars.value = setOf()
        servantClasses.value = setOf()
        waysToGet.value = setOf()
        items.value = setOf()
        itemFilterMode.value = DEFAULT_ITEM_MODE
    }

    fun start(costItemGetter: (Servant) -> Collection<Item>) {
        this.costItemGetter = costItemGetter
    }

    fun setOrigin(servantIDs: Set<Int>?) {
        origin.value = servantIDs?.mapNotNull { ResourcesProvider.instance.servants[it] }?.toSet()
                ?: ResourcesProvider.instance.servants.values.toSet()
    }

    val filtered = object : LiveData<List<Servant>>() {
        val observer = Observer<Any> { _ ->
            var list = origin.value?.toList() ?: listOf()
            searchText.value?.also { searchText ->
                val words = searchText.split(' ').filter { it.isNotEmpty() }
                list = list.filter { cur ->
                    words.all { word ->
                        var contains = false
                        contains = contains || cur.zhName.contains(word, true)
                        contains = contains || cur.enName.contains(word, true)
                        contains = contains || cur.jaName.contains(word, true)
                        if (PreferenceManager.getDefaultSharedPreferences(MyApp.context).getBoolean(PreferenceKeys.KEY_UNLOCK_REAL_NAME, false) && cur.hideRealName) {
                            contains = contains || cur.realZhName.contains(word, true)
                            contains = contains || cur.realEnName.contains(word, true)
                            contains = contains || cur.realJaName.contains(word, true)
                        }
                        contains = contains || cur.nickname.any { it.contains(word, true) }
                        contains
                    }
                }
            }
            servantClasses.value?.also { servantClasses ->
                if (servantClasses.isNotEmpty())
                    list = list.filter { cur -> servantClasses.any { cur.theClass == it } }
            }
            waysToGet.value?.also { waysToGet ->
                if (waysToGet.isNotEmpty())
                    list = list.filter { cur -> waysToGet.any { cur.wayToGet == it } }
            }
            stars.value?.also { stars ->
                if (stars.isNotEmpty())
                    list = list.filter { cur -> stars.any { cur.star == it } }
            }
            items.value?.also { items ->
                if (items.isNotEmpty()) {
                    list = if (itemFilterMode.value == ItemFilterMode.Or)
                        list.filter { cur ->
                            items.any { item -> costItemGetter?.invoke(cur)?.any { it.codename == item.codename } == true }
                        }
                    else
                        list.filter { cur ->
                            items.all { item -> costItemGetter?.invoke(cur)?.any { it.codename == item.codename } == true }
                        }
                }
            }
            orderBy.value?.also { orderBy ->
                list = when (orderBy) {
                    OrderBy.ID -> list.sortedBy { it.id }
                    OrderBy.Class -> list.sortedBy { it.theClass }
                    OrderBy.Star -> list.sortedBy { it.star }
                }
            }
            order.value?.also { order ->
                list = when (order) {
                    Order.Increase -> list
                    Order.Decrease -> list.reversed()
                }
            }
            value = list
        }

        init {
            searchText.observeForever(observer)
            origin.observeForever(observer)
            servantClasses.observeForever(observer)
            waysToGet.observeForever(observer)
            stars.observeForever(observer)
            items.observeForever(observer)
            itemFilterMode.observeForever(observer)
            orderBy.observeForever(observer)
            order.observeForever(observer)
        }
    }

    val expandOrderBy = MutableLiveData<Boolean>()
    val expandStar = MutableLiveData<Boolean>()
    val expandClass = MutableLiveData<Boolean>()
    val expandWayToGet = MutableLiveData<Boolean>()
    val expandItem = MutableLiveData<Boolean>()

    val enableResetButton = object : LiveData<Boolean>() {
        val observer = Observer<Any> {
            var ans = true
            ans = ans && searchText.value.isNullOrEmpty()
            ans = ans && servantClasses.value.isNullOrEmpty()
            ans = ans && stars.value.isNullOrEmpty()
            ans = ans && waysToGet.value.isNullOrEmpty()
            ans = ans && items.value.isNullOrEmpty()
            ans = ans && itemFilterMode.value == DEFAULT_ITEM_MODE
            ans = ans && order.value == DEFAULT_ORDER
            ans = ans && orderBy.value == DEFAULT_ORDER_BY
            value = !ans
        }

        init {
            origin.observeForever(observer)
            searchText.observeForever(observer)
            servantClasses.observeForever(observer)
            waysToGet.observeForever(observer)
            stars.observeForever(observer)
            items.observeForever(observer)
            itemFilterMode.observeForever(observer)
            orderBy.observeForever(observer)
            order.observeForever(observer)
        }
    }

    val showAddItemUIEvent = SingleLiveEvent<Void>()

    fun onClickResetButton() {
        searchText.value = ""
        servantClasses.value = setOf()
        stars.value = setOf()
        waysToGet.value = setOf()
        items.value = setOf()
        itemFilterMode.value = DEFAULT_ITEM_MODE
        order.value = DEFAULT_ORDER
        orderBy.value = DEFAULT_ORDER_BY
    }

    fun onClickHeader(v: View) {
        when (v.id) {
            R.id.orderByHeader_textView -> expandOrderBy.value = expandOrderBy.value != true
            R.id.starHeader_textView -> expandStar.value = expandStar.value != true
            R.id.classHeader_textView -> expandClass.value = expandClass.value != true
            R.id.wayToGetHeader_textView -> expandWayToGet.value = expandWayToGet.value != true
            R.id.itemHeader_textView -> expandItem.value = expandItem.value != true
        }
    }

    fun onClickAddItem() {
        showAddItemUIEvent.call()
    }

    fun onClickRemoveItem(codename: String) {
        items.value = items.value?.toMutableSet()?.apply {
            val descriptor = firstOrNull { it.codename == codename } ?: return
            minus(descriptor)
        }
    }

    fun onAddItem(codename: String) {
        val descriptor = ResourcesProvider.instance.itemDescriptors[codename] ?: return
        items.value = items.value?.plus(descriptor) ?: setOf(descriptor)
    }

    companion object {
        private val DEFAULT_ITEM_MODE = ItemFilterMode.And
        private val DEFAULT_ORDER = Order.Increase
        private val DEFAULT_ORDER_BY = OrderBy.ID
    }
}