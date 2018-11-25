package com.ssttkkl.fgoplanningtool.ui.requirementlist

import android.content.Context
import androidx.recyclerview.widget.*
import android.view.*
import com.bumptech.glide.Glide
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.databinding.ItemRequirementlistBinding
import com.ssttkkl.fgoplanningtool.utils.toStringWithSplitter
import kotlinx.android.synthetic.main.item_requirementlist.view.*

class RequirementListRecViewAdapter(val context: Context) : ListAdapter<RequirementListEntity, RequirementListRecViewAdapter.ViewHolder>(object : DiffUtil.ItemCallback<RequirementListEntity>() {
    override fun areContentsTheSame(oldItem: RequirementListEntity, newItem: RequirementListEntity) = oldItem == newItem
    override fun areItemsTheSame(oldItem: RequirementListEntity, newItem: RequirementListEntity) = oldItem.servantID == newItem.servantID
}) {
    private var listener: ((RequirementListEntity) -> Unit)? = null

    fun setOnItemClickListener(newListener: ((RequirementListEntity) -> Unit)?) {
        listener = newListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(ItemRequirementlistBinding.inflate(LayoutInflater.from(context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(val binding: ItemRequirementlistBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.listener = View.OnClickListener {
                listener?.invoke(getItem(adapterPosition))
            }
        }

        fun bind(item: RequirementListEntity) {
            binding.entity = item
            binding.executePendingBindings()
        }
    }
}