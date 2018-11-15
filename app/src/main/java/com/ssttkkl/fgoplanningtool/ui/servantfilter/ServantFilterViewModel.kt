package com.ssttkkl.fgoplanningtool.ui.servantfilter

import androidx.lifecycle.ViewModel
import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider
import com.ssttkkl.fgoplanningtool.resources.servant.Servant
import com.ssttkkl.fgoplanningtool.resources.servant.ServantClass
import com.ssttkkl.fgoplanningtool.resources.servant.WayToGet
import java.util.concurrent.ConcurrentSkipListSet

class ServantFilterViewModel : ViewModel() {
    var origin: Collection<Servant> = ResourcesProvider.instance.servants.values
        set(value) {
            synchronized(ServantFilterViewModel::class.java) {
                field = if (value.isEmpty()) ResourcesProvider.instance.servants.values else value
            }
        }

    var nameFilter = ""

    var classFilter = ConcurrentSkipListSet<ServantClass>()

    var starFilter = ConcurrentSkipListSet<Int>()

    var wayToGetFilter = ConcurrentSkipListSet<WayToGet>()

    var itemFilter = ConcurrentSkipListSet<String>()

    var itemFilterMode = ItemFilterMode.And

    var orderBy = OrderBy.ID

    var order = Order.Increase

    val isDefaultState: Boolean
        get() {
            var ans = true
            ans = ans && classFilter.isEmpty()
            ans = ans && starFilter.isEmpty()
            ans = ans && wayToGetFilter.isEmpty()
            ans = ans && itemFilter.isEmpty()
            ans = ans && itemFilterMode == ItemFilterMode.And
            ans = ans && orderBy == OrderBy.ID
            ans = ans && order == Order.Increase
            return ans
        }
}