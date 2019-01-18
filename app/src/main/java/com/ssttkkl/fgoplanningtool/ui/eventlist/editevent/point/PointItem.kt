package com.ssttkkl.fgoplanningtool.ui.eventlist.editevent.point

import android.os.Parcelable
import com.ssttkkl.fgoplanningtool.data.item.Item
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PointItem(val item: Item,
                     val requestPoint: Long,
                     val show: Boolean) : Parcelable