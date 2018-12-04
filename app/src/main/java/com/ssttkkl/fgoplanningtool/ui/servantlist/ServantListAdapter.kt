package com.ssttkkl.fgoplanningtool.ui.servantlist

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ssttkkl.fgoplanningtool.BR
import com.ssttkkl.fgoplanningtool.databinding.ItemServantlistGridBinding
import com.ssttkkl.fgoplanningtool.databinding.ItemServantlistListBinding
import com.ssttkkl.fgoplanningtool.resources.servant.Servant

class ServantListAdapter(val context: Context,
                         private val lifecycleOwner: LifecycleOwner,
                         private val viewModel: ServantListFragmentViewModel)
    : ListAdapter<Servant, ServantListAdapter.ViewHolder>(diffCallback) {

    override fun getItemViewType(position: Int): Int {
        return (viewModel.viewType.value ?: ViewType.List).ordinal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(if (viewType == ViewType.Grid.ordinal)
                ItemServantlistGridBinding.inflate(LayoutInflater.from(context), parent, false)
            else
                ItemServantlistListBinding.inflate(LayoutInflater.from(context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        holder.bind(getItem(pos))
    }

    inner class ViewHolder(val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.setVariable(BR.viewModel, viewModel)
            binding.setLifecycleOwner(lifecycleOwner)
        }

        fun bind(servant: Servant) {
            binding.setVariable(BR.servant, servant)
        }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Servant>() {
            override fun areContentsTheSame(oldItem: Servant, newItem: Servant) = oldItem == newItem
            override fun areItemsTheSame(oldItem: Servant, newItem: Servant) = oldItem.id == newItem.id
        }
    }
}