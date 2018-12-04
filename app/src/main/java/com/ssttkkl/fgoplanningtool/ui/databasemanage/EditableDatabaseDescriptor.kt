package com.ssttkkl.fgoplanningtool.ui.databasemanage

import androidx.recyclerview.widget.DiffUtil
import com.ssttkkl.fgoplanningtool.data.databasedescriptor.DatabaseDescriptor

data class EditableDatabaseDescriptor(val databaseDescriptor: DatabaseDescriptor,
                                      val editing: Boolean) {
    companion object ItemCallback : DiffUtil.ItemCallback<EditableDatabaseDescriptor>() {
        override fun areContentsTheSame(oldItem: EditableDatabaseDescriptor, newItem: EditableDatabaseDescriptor) = oldItem == newItem
        override fun areItemsTheSame(oldItem: EditableDatabaseDescriptor, newItem: EditableDatabaseDescriptor) = oldItem.databaseDescriptor.uuid == newItem.databaseDescriptor.uuid
    }
}