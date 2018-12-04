package com.ssttkkl.fgoplanningtool.ui.servantinfo

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ssttkkl.fgoplanningtool.databinding.ItemServantinfoLevellistBinding
import com.ssttkkl.fgoplanningtool.ui.utils.CommonRecViewItemDecoration

class ServantInfoLevelListRecViewAdapter(val context: Context,
                                         private val lifecycleOwner: LifecycleOwner,
                                         private val viewModel: ServantInfoLevelListViewModel)
    : ListAdapter<ServantInfoLevelListEntity, ServantInfoLevelListRecViewAdapter.ViewHolder>(diffCallback) {

    private var listener: ((codename: String) -> Unit)? = null

    fun setOnItemClickListener(newListener: ((codename: String) -> Unit)?) {
        listener = newListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(ItemServantinfoLevellistBinding.inflate(LayoutInflater.from(context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        holder.bind(getItem(pos))
    }

    inner class ViewHolder(val binding: ItemServantinfoLevellistBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.viewModel = viewModel
            binding.setLifecycleOwner(lifecycleOwner)

            binding.recView.apply {
                adapter = ServantInfoItemListRecViewAdapter(context).apply {
                    setOnItemClickListener { listener?.invoke(it.codename) }
                }
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                addItemDecoration(CommonRecViewItemDecoration(context))
            }
        }

        fun bind(entity: ServantInfoLevelListEntity) {
            binding.entity = entity
            binding.executePendingBindings()
        }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<ServantInfoLevelListEntity>() {
            override fun areContentsTheSame(oldItem: ServantInfoLevelListEntity, newItem: ServantInfoLevelListEntity) = oldItem == newItem
            override fun areItemsTheSame(oldItem: ServantInfoLevelListEntity, newItem: ServantInfoLevelListEntity) = oldItem.start == newItem.start && oldItem.to == newItem.to && oldItem.isHorizontalArrowVisible == newItem.isHorizontalArrowVisible
        }
    }
}