package com.ssttkkl.fgoplanningtool.ui.databasemanage

import android.content.Context
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.data.databasedescriptor.DatabaseDescriptor
import com.ssttkkl.fgoplanningtool.ui.utils.RecViewAdapterDataSetChanger
import kotlinx.android.synthetic.main.item_databasemanage.view.*
import kotlinx.android.synthetic.main.item_editmode_databasemanage.view.*
import java.util.concurrent.ConcurrentHashMap

class DatabaseManageRecViewAdapter(val context: Context) : RecyclerView.Adapter<DatabaseManageRecViewAdapter.ViewHolder>() {
    val data: List<DatabaseDescriptor> = ArrayList()

    fun setNewData(newData: List<DatabaseDescriptor>) {
        synchronized(data) {
            var equal = data.size == newData.size
            if (equal)
                data.indices.forEach {
                    equal = equal && data[it] == newData[it]
                }
            if (equal)
                return

            inEditModePositions.filter { it.value }.keys.forEach { setPositionInNormalMode(it) }
            RecViewAdapterDataSetChanger.perform(this, data as ArrayList<DatabaseDescriptor>, newData) { it.uuid }
        }
    }

    interface Callback {
        fun onSelectedPositionChanged(newPos: Int, newItem: DatabaseDescriptor)
        fun onItemNameChange(pos: Int, item: DatabaseDescriptor, newName: String)
        fun onItemRemove(pos: Int, item: DatabaseDescriptor)
        fun onImportPlans(pos: Int, item: DatabaseDescriptor)
        fun onImportItems(pos: Int, item: DatabaseDescriptor)
        fun onExportPlans(pos: Int, item: DatabaseDescriptor)
        fun onExportItems(pos: Int, item: DatabaseDescriptor)
    }

    private var callback: Callback? = null

    fun setCallback(newCallback: Callback?) {
        callback = newCallback
    }

    private fun onMenuItemClick(pos: Int, menuId: Int) {
        when (menuId) {
            R.id.rename_action -> setPositionInEditMode(pos, data[pos].name)
            R.id.remove_action -> callback?.onItemRemove(pos, data[pos])
            R.id.importPlans_action -> callback?.onImportPlans(pos, data[pos])
            R.id.importItems_action -> callback?.onImportItems(pos, data[pos])
            R.id.exportPlans_action -> callback?.onExportPlans(pos, data[pos])
            R.id.exportItems_action -> callback?.onExportItems(pos, data[pos])
        }
    }

    val inEditModePositions = ConcurrentHashMap<Int, Boolean>()
    val editedNames = ConcurrentHashMap<Int, String>()

    fun setPositionInEditMode(pos: Int, editedName: String) {
        inEditModePositions[pos] = true
        editedNames[pos] = editedName
        notifyItemChanged(pos)
    }

    fun setPositionInNormalMode(pos: Int) {
        inEditModePositions[pos] = false
        notifyItemChanged(pos)
    }

    var selectedPosition: Int = 0
        set(value) {
            if (field == value)
                return
            val old = field
            field = value
            if (old in 0 until itemCount)
                notifyItemChanged(old)
            notifyItemChanged(value)
            callback?.onSelectedPositionChanged(value, data[value])
        }

    override fun getItemViewType(pos: Int) = if (inEditModePositions[pos] == true) ITEM_TYPE_EDIT else ITEM_TYPE_NORMAL

    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        ITEM_TYPE_NORMAL -> ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_databasemanage, parent, false)).apply {
            itemView.setOnClickListener { selectedPosition = adapterPosition }
            itemView.more_button.setOnClickListener {
                PopupMenu(context, it).apply {
                    inflate(R.menu.item_databasemanage)
                    setOnMenuItemClickListener { onMenuItemClick(adapterPosition, it.itemId);true }
                }.show()
            }
        }
        else -> ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_editmode_databasemanage, parent, false)).apply {
            itemView.name_editText.addTextChangedListener(object : TextWatcher {
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (itemView.name_editText.isFocused)
                        editedNames[adapterPosition] = s?.toString() ?: ""
                }

                override fun afterTextChanged(s: Editable?) {}
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            })
            itemView.ok_button.setOnClickListener {
                if (editedNames[adapterPosition] != "")
                    callback?.onItemNameChange(adapterPosition, data[adapterPosition], editedNames[adapterPosition]
                            ?: "")
                setPositionInNormalMode(adapterPosition)
            }
            itemView.abort_button.setOnClickListener { setPositionInNormalMode(adapterPosition) }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        val item = data[pos]
        holder.itemView.apply {
            findViewById<ImageView>(R.id.selectedFlag_imageView).visibility = if (selectedPosition == pos) View.VISIBLE else View.INVISIBLE
            when (getItemViewType(pos)) {
                ITEM_TYPE_NORMAL -> {
                    name_textView.text = item.name
                }
                ITEM_TYPE_EDIT -> {
                    name_editText.hint = item.name
                    name_editText.setText(editedNames[pos])
                }
            }
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    companion object {
        private const val ITEM_TYPE_NORMAL = 1
        private const val ITEM_TYPE_EDIT = 2
    }
}