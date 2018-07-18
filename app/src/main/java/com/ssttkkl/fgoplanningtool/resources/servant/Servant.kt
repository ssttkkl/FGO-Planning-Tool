package com.ssttkkl.fgoplanningtool.resources.servant

import com.ssttkkl.fgoplanningtool.data.item.Item
import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider
import java.io.File
import java.util.*

data class Servant(val id: Int,
                   val jaName: String,
                   val zhName: String,
                   val enName: String,
                   val star: Int,
                   val theClass: ServantClass,
                   val nickname: Collection<String>,
                   val ascensionItems: List<Collection<Item>>,
                   val skillItems: List<Collection<Item>>) {
    val avatarFile
        get() = File(ResourcesProvider.instance.avatarDir, "$id.jpg")

    val localizedName
        get() = when (Locale.getDefault().language) {
            Locale.CHINESE.language -> zhName
            Locale.JAPANESE.language -> jaName
            else -> enName
        }

    val ascensionQP: List<Long>
        get() = if (id == 1) listOf(0, 0, 0, 0) else ResourcesProvider.instance.ascensionQPInfo[star]

    val skillQP: List<Long>
        get() = ResourcesProvider.instance.skillQPInfo[star]

    val palingenesisQP: List<Long>
        get() = ResourcesProvider.instance.palingenesisQPInfo[star]
}