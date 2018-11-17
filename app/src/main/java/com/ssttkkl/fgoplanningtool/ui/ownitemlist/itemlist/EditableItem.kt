package com.ssttkkl.fgoplanningtool.ui.ownitemlist.itemlist

import androidx.recyclerview.widget.DiffUtil
import com.ssttkkl.fgoplanningtool.data.item.Item

data class EditableItem(val item: Item,
                        val editing: Boolean) {
    companion object Differ : DiffUtil.ItemCallback<EditableItem>() {
        override fun areContentsTheSame(oldItem: EditableItem, newItem: EditableItem) = oldItem == newItem
        override fun areItemsTheSame(oldItem: EditableItem, newItem: EditableItem) = oldItem.item.codename == newItem.item.codename
    }
}