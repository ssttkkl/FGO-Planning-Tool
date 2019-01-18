package com.ssttkkl.fgoplanningtool.ui.eventlist.confirmchangeevent

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
enum class Mode : Parcelable {
    Change, Remove;
}