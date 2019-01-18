package com.ssttkkl.fgoplanningtool.ui.servantfilter

import android.content.Context
import android.preference.PreferenceManager
import android.view.View
import androidx.core.content.edit
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
import java.lang.ref.WeakReference

class ServantFilterFragmentViewModel : ViewModel() {
    private var context = WeakReference<Context>(null)
    private var prefLabel = ""

    private val keySearchText
        get() = PreferenceKeys.KEY_SERVANT_FILTER_SEARCH_TEXT.format(prefLabel)
    private val keyClass
        get() = PreferenceKeys.KEY_SERVANT_FILTER_CLASS.format(prefLabel)
    private val keyStar
        get() = PreferenceKeys.KEY_SERVANT_FILTER_STAR.format(prefLabel)
    private val keyWayToGet
        get() = PreferenceKeys.KEY_SERVANT_FILTER_WAY_TO_GET.format(prefLabel)
    private val keyItem
        get() = PreferenceKeys.KEY_SERVANT_FILTER_ITEM.format(prefLabel)
    private val keyItemMode
        get() = PreferenceKeys.KEY_SERVANT_FILTER_ITEM_MODE.format(prefLabel)
    private val keyOrder
        get() = PreferenceKeys.KEY_SERVANT_FILTER_ORDER.format(prefLabel)
    private val keyOrderBy
        get() = PreferenceKeys.KEY_SERVANT_FILTER_ORDER_BY.format(prefLabel)

    private var costItemGetter: ((Servant) -> Collection<Item>)? = null

    val orderEntities = Order.values().toList()
    val orderByEntities = OrderBy.values().toList()
    val starEntities = Star.values().toList()
    val servantClassEntities = ServantClass.values().toList()
    val wayToGetEntities = WayToGet.values().toList()

    val origin = MutableLiveData<Set<Servant>>()
    val searchText = MutableLiveData<String>()
    val order = MutableLiveData<Order>()
    val orderBy = MutableLiveData<OrderBy>()
    val stars = MutableLiveData<Set<Star>>()
    val servantClasses = MutableLiveData<Set<ServantClass>>()
    val waysToGet = MutableLiveData<Set<WayToGet>>()
    val items = MutableLiveData<Set<ItemDescriptor>>()
    val itemFilterMode = MutableLiveData<ItemFilterMode>()

    init {
        searchText.observeForever {
            PreferenceManager.getDefaultSharedPreferences(context.get()
                    ?: return@observeForever).edit {
                putString(keySearchText, it ?: "")
            }
        }

        order.observeForever {
            PreferenceManager.getDefaultSharedPreferences(context.get()
                    ?: return@observeForever).edit {
                putString(keyOrder, (it ?: DEFAULT_ORDER).name)
            }
        }

        orderBy.observeForever {
            PreferenceManager.getDefaultSharedPreferences(context.get()
                    ?: return@observeForever).edit {
                putString(keyOrderBy, (it ?: DEFAULT_ORDER_BY).name)
            }
        }

        stars.observeForever {
            PreferenceManager.getDefaultSharedPreferences(context.get()
                    ?: return@observeForever).edit {
                putStringSet(keyStar, it?.map { it.name }?.toSet() ?: setOf())
            }
        }

        servantClasses.observeForever {
            PreferenceManager.getDefaultSharedPreferences(context.get()
                    ?: return@observeForever).edit {
                putStringSet(keyClass, it?.map { it.name }?.toSet() ?: setOf())
            }
        }

        waysToGet.observeForever {
            PreferenceManager.getDefaultSharedPreferences(context.get()
                    ?: return@observeForever).edit {
                putStringSet(keyWayToGet, it?.map { it.name }?.toSet() ?: setOf())
            }
        }

        items.observeForever {
            PreferenceManager.getDefaultSharedPreferences(context.get()
                    ?: return@observeForever).edit {
                putStringSet(keyItem, it?.map { it.codename }?.toSet() ?: setOf())
            }
        }

        itemFilterMode.observeForever {
            PreferenceManager.getDefaultSharedPreferences(context.get()
                    ?: return@observeForever).edit {
                putString(keyItemMode, (it ?: DEFAULT_ITEM_MODE).name)
            }
        }
    }

    fun start(context: Context, prefLabel: String, costItemGetter: (Servant) -> Collection<Item>) {
        this.context = WeakReference(context)
        this.prefLabel = prefLabel
        this.costItemGetter = costItemGetter

        try {
            PreferenceManager.getDefaultSharedPreferences(context).apply {
                searchText.value = getString(keySearchText, "")
                order.value = Order.valueOf(getString(keyOrder, DEFAULT_ORDER.name)!!)
                orderBy.value = OrderBy.valueOf(getString(keyOrderBy, DEFAULT_ORDER_BY.name)!!)
                stars.value = getStringSet(keyStar, setOf())?.map { Star.valueOf(it) }?.toSet()
                servantClasses.value = getStringSet(keyClass, setOf())?.map { ServantClass.valueOf(it) }?.toSet()
                waysToGet.value = getStringSet(keyWayToGet, setOf())?.map { WayToGet.valueOf(it) }?.toSet()
                items.value = getStringSet(keyItem, setOf())?.mapNotNull { ResourcesProvider.instance.itemDescriptors[it] }?.toSet()
                itemFilterMode.value = ItemFilterMode.valueOf(getString(keyItemMode, DEFAULT_ITEM_MODE.name)!!)
            }
        } catch (exc: Exception) {
        }
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
                    list = list.filter { cur -> stars.any { cur.star == it.num } }
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

    val isDefaultState = object : LiveData<Boolean>() {
        val observer = Observer<Any> {
            var ans = true
            ans = ans && searchText.value?.isNotEmpty() != true
            ans = ans && servantClasses.value?.isNotEmpty() != true
            ans = ans && stars.value?.isNotEmpty() != true
            ans = ans && waysToGet.value?.isNotEmpty() != true
            ans = ans && items.value?.isNotEmpty() != true
            ans = ans && itemFilterMode.value == DEFAULT_ITEM_MODE
            ans = ans && order.value == DEFAULT_ORDER
            ans = ans && orderBy.value == DEFAULT_ORDER_BY
            value = ans
        }

        init {
            value = false

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

    fun onClickRemoveItem(descriptor: ItemDescriptor) {
        items.value = items.value?.minus(descriptor)
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