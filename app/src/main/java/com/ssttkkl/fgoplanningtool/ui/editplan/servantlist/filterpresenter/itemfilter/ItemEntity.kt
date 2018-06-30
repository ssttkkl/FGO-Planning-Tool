package com.ssttkkl.fgoplanningtool.ui.editplan.servantlist.filterpresenters.itemfilter

import com.ssttkkl.fgoplanningtool.resources.itemdescriptor.ItemDescriptor

data class ItemEntity(val codename: String,
                      val name: String,
                      val imgUri: String) {
    constructor(descriptor: ItemDescriptor) : this(descriptor.codename,
            descriptor.localizedName,
            descriptor.imgUri)
}