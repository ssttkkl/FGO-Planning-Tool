package com.ssttkkl.fgoplanningtool.resources.servant

import android.preference.PreferenceManager
import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.PreferenceKeys
import com.ssttkkl.fgoplanningtool.data.item.Item
import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider
import java.io.File

data class Servant(val id: Int,
                   val jaName: String,
                   val zhName: String,
                   val enName: String,
                   val star: Int,
                   val theClass: ServantClass,
                   val nickname: Collection<String>,
                   val wayToGet: WayToGet,
                   val ascensionItems: List<Collection<Item>>,
                   val skillItems: List<Collection<Item>>,
                   val dress: List<Dress>,
                   val wikiLinks: Map<String, String>) {
    val avatarFile
        get() = File(ResourcesProvider.instance.avatarDir, "$id.jpg")

    val localizedName
        get() = when (PreferenceManager.getDefaultSharedPreferences(MyApp.context).getString(PreferenceKeys.KEY_NAME_LANGUAGE, "zh")) {
            "ja" -> jaName
            "en" -> enName
            else -> zhName
        }

    val ascensionQP: List<Long>
        get() = if (id == 1) listOf(0, 0, 0, 0) else ResourcesProvider.instance.qpInfo.ascension[star]

    val skillQP: List<Long>
        get() = ResourcesProvider.instance.qpInfo.skill[star]

    val palingenesisQP: List<Long>
        get() = ResourcesProvider.instance.qpInfo.palingenesis[star]
}