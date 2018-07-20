package com.ssttkkl.fgoplanningtool.resources.servant

import android.preference.PreferenceManager
import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.PreferenceKeys
import com.ssttkkl.fgoplanningtool.data.item.Item
import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider

data class Dress(val zhName: String,
                 val jaName: String,
                 val enName: String,
                 val items: Collection<Item>) {
    val localizedName: String
        get() = when (PreferenceManager.getDefaultSharedPreferences(MyApp.context).getString(PreferenceKeys.KEY_NAME_LANGUAGE, "zh")) {
            "ja" -> jaName
            "en" -> enName
            else -> zhName
        }

    val qp: Long
        get() = ResourcesProvider.instance.qpInfo.dress
}