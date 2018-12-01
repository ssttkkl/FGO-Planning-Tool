package com.ssttkkl.fgoplanningtool.resources.servant

import android.preference.PreferenceManager
import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.PreferenceKeys
import com.ssttkkl.fgoplanningtool.data.item.Item
import com.ssttkkl.fgoplanningtool.resources.ConstantValues
import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider
import com.ssttkkl.fgoplanningtool.utils.Localizable
import java.io.File

data class Servant(val id: Int,
                   val jaName: String,
                   val zhName: String,
                   val enName: String,
                   val hideRealName: Boolean,
                   val realJaName: String,
                   val realZhName: String,
                   val realEnName: String,
                   val star: Int,
                   val theClass: ServantClass,
                   val nickname: Collection<String>,
                   val wayToGet: WayToGet,
                   val ascensionItems: List<Collection<Item>>,
                   val skillItems: List<Collection<Item>>,
                   val dress: List<Dress>,
                   val wikiLinks: Map<String, String>) : Localizable {
    val avatarFile
        get() = File(ResourcesProvider.instance.avatarDir, "$id.jpg")

    override val localizedName: String
        get() {
            val pref = PreferenceManager.getDefaultSharedPreferences(MyApp.context)
            val unlockRealName = pref.getBoolean(PreferenceKeys.KEY_UNLOCK_REAL_NAME, false) && hideRealName
            return when (pref.getString(PreferenceKeys.KEY_NAME_LANGUAGE, "zh")) {
                "ja" -> if (unlockRealName) realJaName else jaName
                "en" -> if (unlockRealName) realEnName else enName
                else -> if (unlockRealName) realZhName else zhName
            }
        }

    val ascensionQP: List<Long>
        get() =
            if (id == 1) listOf(0, 0, 0, 0) else ConstantValues.ascensionQP[star]

    val skillQP: List<Long>
        get() =
            ConstantValues.skillQP[star]

    val stageMapToMaxLevel: List<Int>
        get() =
            if (id == 1)
                ConstantValues.stageMapToMaxLevel[star].slice(0..4)
            else
                ConstantValues.stageMapToMaxLevel[star]

    fun calcExpCardQP(curLevel: Int): Long {
        val (a, b) = ConstantValues.expCardQP[star]
        return a + b * curLevel
    }
}