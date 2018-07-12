package com.ssttkkl.fgoplanningtool.resources.itemdescriptor

import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider
import java.io.File
import java.io.Serializable
import java.util.*

data class ItemDescriptor(val codename: String,
                          val cnName: String,
                          val jpName: String,
                          val enName: String,
                          val type: ItemType) : Serializable {
    val imgFile
        get() = File(ResourcesProvider.instance.itemImgDir, "$codename.jpg")

    val localizedName
        get() = when (Locale.getDefault().language) {
            Locale.CHINESE.language -> cnName
            Locale.JAPANESE.language -> jpName
            else -> enName
        }
}