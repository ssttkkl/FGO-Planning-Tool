package com.ssttkkl.fgoplanningtool.ui.servantinfo

import android.os.Parcelable
import com.ssttkkl.fgoplanningtool.data.item.Item
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ServantInfoLevelListEntity(val start: String,
                                      val to: String,
                                      val isHorizontalArrowVisible: Boolean,
                                      val items: List<Item>) : Parcelable