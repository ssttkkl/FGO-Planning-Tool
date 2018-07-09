package com.ssttkkl.fgoplanningtool.resources.servant

import com.ssttkkl.fgoplanningtool.data.item.Item
import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider
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
    val avatarUri: String
        get() = AVATAR_URI_PATTERN.format(id)

    val localizedName
        get() = when (Locale.getDefault().language) {
            Locale.CHINESE.language -> zhName
            Locale.JAPANESE.language -> jaName
            else -> enName
        }

    val ascensionQP
        get() = ResourcesProvider.ascensionQPInfo[star]

    val skillQP
        get() = ResourcesProvider.skillQPInfo[star]

    val palingenesisQP
        get() = ResourcesProvider.palingenesisQPInfo[star]

    companion object {
        private const val AVATAR_URI_PATTERN = "file:///android_asset/avatar/%d.jpg"
    }
}