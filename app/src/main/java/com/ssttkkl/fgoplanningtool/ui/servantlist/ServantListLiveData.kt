package com.ssttkkl.fgoplanningtool.ui.servantlist

import android.arch.lifecycle.LiveData
import com.ssttkkl.fgoplanningtool.resources.servant.Servant
import com.ssttkkl.fgoplanningtool.resources.servant.ServantClass
import com.ssttkkl.fgoplanningtool.resources.servant.WayToGet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*

class ServantListLiveData(private val origin: Collection<Servant>) : LiveData<List<Servant>>() {
    val nameFilter: MutableSet<CharSequence> = HashSet()

    val classFilter: MutableSet<ServantClass> = HashSet()

    val starFilter: MutableSet<Int> = HashSet()

    val wayToGetFilter: MutableSet<WayToGet> = HashSet()

    val itemFilter: MutableSet<String> = HashSet()

    var itemFilterMode: ItemFilterMode = ItemFilterMode.And

    var orderBy: OrderBy = OrderBy.ID

    var order: Order = Order.Increase

    fun notifyFiltersChanged() {
        GlobalScope.launch {
            value = GlobalScope.async(Dispatchers.Default) { perform(origin) }.await()
        }
    }

    init {
        notifyFiltersChanged()
    }

    private fun perform(dataSet: Collection<Servant>): List<Servant> {
        var list = dataSet.toList()
        if (nameFilter.isNotEmpty())
            list = list.filter { cur ->
                nameFilter.all { filter ->
                    if (Locale.getDefault().language == Locale.CHINESE.language) {
                        cur.localizedName.contains(filter, true) ||
                                (cur.nickname.isNotEmpty() &&
                                        cur.nickname.any { it.contains(filter, true) })
                    } else
                        cur.localizedName.contains(filter, true)
                }
            }
        if (classFilter.isNotEmpty())
            list = list.filter { cur -> classFilter.any { cur.theClass == it } }
        if (wayToGetFilter.isNotEmpty())
            list = list.filter { cur -> wayToGetFilter.any { cur.wayToGet == it } }
        if (starFilter.isNotEmpty())
            list = list.filter { cur -> starFilter.any { cur.star == it } }
        if (itemFilter.isNotEmpty()) {
            list = when (itemFilterMode) {
                ItemFilterMode.And -> list.filter { cur ->
                    itemFilter.all { filter ->
                        cur.ascensionItems.any { lv -> lv.any { it.codename == filter } } ||
                                cur.skillItems.any { lv -> lv.any { it.codename == filter } }
                    }
                }
                ItemFilterMode.Or -> list.filter { cur ->
                    itemFilter.any { filter ->
                        cur.ascensionItems.any { lv -> lv.any { it.codename == filter } } ||
                                cur.skillItems.any { lv -> lv.any { it.codename == filter } }
                    }
                }
            }
        }
        list = when (orderBy) {
            OrderBy.ID -> list.sortedBy { it.id }
            OrderBy.Class -> list.sortedBy { it.theClass }
            OrderBy.Star -> list.sortedBy { it.star }
        }
        return if (order == Order.Increase) list else list.reversed()
    }
}