package com.ssttkkl.fgoplanningtool.ui.editplan

import android.content.Context
import androidx.databinding.Observable
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.ssttkkl.fgoplanningtool.BR
import com.ssttkkl.fgoplanningtool.databinding.ItemEditplanDressBinding
import com.ssttkkl.fgoplanningtool.resources.servant.Dress
import com.ssttkkl.fgoplanningtool.ui.utils.databinding.MultipleSelectableAdapter

class EditPlanDressListRecViewAdapter(val context: Context) : androidx.recyclerview.widget.RecyclerView.Adapter<EditPlanDressListRecViewAdapter.ViewHolder>()
        , MultipleSelectableAdapter {
    var data: List<Dress> = listOf()
        set(value) {
            field = value
            selection = selection.filter { it < value.size }.toSet()
            notifyDataSetChanged()
        }

    private var onSelectionChangedListener: ((selection: Set<Int>) -> Unit)? = null

    override fun setOnSelectionChangedListener(newListener: ((selection: Set<Int>) -> Unit)?) {
        onSelectionChangedListener = newListener
    }

    override var selection: Set<Int> = setOf()
        set(value) {
            if (field == value)
                return
            val old = field
            field = value
            old.forEach {
                if (!value.contains(it))
                    notifyItemChanged(it)
            }
            value.forEach {
                if (!old.contains(it))
                    notifyItemChanged(it)
            }
            onSelectionChangedListener?.invoke(value)
        }

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(ItemEditplanDressBinding.inflate(LayoutInflater.from(context), parent, false)).apply {
                binding.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
                    override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                        if (propertyId == BR.checked) {
                            val checked = binding.checked == true
                            synchronized(selection) {
                                if (checked && !selection.contains(adapterPosition))
                                    selection += adapterPosition
                                else if (!checked && selection.contains(adapterPosition))
                                    selection -= adapterPosition
                                else
                                    Log.w("EditPlanDressList", "item $adapterPosition was already ${if (checked) "checked" else "unchecked"}")
                            }
                        }
                    }
                })
            }

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        holder.binding.apply {
            text = data[pos].localizedName
            checked = selection.contains(pos)
        }
    }

    inner class ViewHolder(val binding: ItemEditplanDressBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root)
}
