package com.ssttkkl.fgoplanningtool.ui.editplan.servantlist.filterpresenter.itemfilter

import com.ssttkkl.fgoplanningtool.resources.itemdescriptor.ItemDescriptor
import java.io.File

data class ItemEntity(val codename: String,
                      val name: String,
                      val imgFile: File?) {
    constructor(descriptor: ItemDescriptor) : this(descriptor.codename,
            descriptor.localizedName,
            descriptor.imgFile)
}