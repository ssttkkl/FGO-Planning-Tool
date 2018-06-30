package com.ssttkkl.fgoplanningtool.resources.itemdescriptor

import java.io.Serializable
import java.util.*

data class ItemDescriptor(val codename: String,
                          val cnName: String,
                          val jpName: String,
                          val enName: String,
                          val type: ItemType) : Serializable {
    val imgUri: String
        get() = imgUriPattern.format(codename)

    val localizedName
        get() = when (Locale.getDefault().language) {
            Locale.CHINESE.language -> cnName
            Locale.JAPANESE.language -> jpName
            else -> enName
        }

    companion object {
        private const val imgUriPattern = "file:///android_asset/item/%s.jpg"
    }
}