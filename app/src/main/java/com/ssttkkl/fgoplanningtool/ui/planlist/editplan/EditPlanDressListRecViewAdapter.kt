package com.ssttkkl.fgoplanningtool.ui.planlist.editplan

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ssttkkl.fgoplanningtool.databinding.ItemEditplanDressBinding
import com.ssttkkl.fgoplanningtool.resources.servant.Dress

class EditPlanDressListRecViewAdapter(val context: Context,
                                      private val lifecycleOwner: LifecycleOwner,
                                      private val viewModel: EditPlanFragmentViewModel)
    : ListAdapter<Dress, EditPlanDressListRecViewAdapter.ViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(ItemEditplanDressBinding.inflate(LayoutInflater.from(context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        holder.bind(getItem(pos))
    }

    inner class ViewHolder(val binding: ItemEditplanDressBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.setLifecycleOwner(lifecycleOwner)
            binding.viewModel = viewModel
        }

        fun bind(dress: Dress) {
            binding.dress = dress
            binding.executePendingBindings()
        }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Dress>() {
            override fun areContentsTheSame(oldItem: Dress, newItem: Dress) = oldItem == newItem
            override fun areItemsTheSame(oldItem: Dress, newItem: Dress) = oldItem == newItem
        }
    }
}
