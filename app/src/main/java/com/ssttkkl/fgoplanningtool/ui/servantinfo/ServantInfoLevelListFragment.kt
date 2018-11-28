package com.ssttkkl.fgoplanningtool.ui.servantinfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssttkkl.fgoplanningtool.databinding.FragmentServantinfoLevellistBinding
import com.ssttkkl.fgoplanningtool.ui.utils.CommonRecViewItemDecoration

class ServantInfoLevelListFragment : Fragment() {
    interface OnClickItemListener {
        fun onClickItem(codename: String)
    }

    private lateinit var binding: FragmentServantinfoLevellistBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentServantinfoLevellistBinding.inflate(inflater, container, false)
        binding.setLifecycleOwner(this)
        binding.viewModel = ViewModelProviders.of(this)[ServantInfoLevelListViewModel::class.java].apply {
            data.value = this@ServantInfoLevelListFragment.data
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.recView.apply {
            adapter = ServantInfoLevelListRecViewAdapter(context, this@ServantInfoLevelListFragment, binding.viewModel!!).apply {
                setOnItemClickListener { (parentFragment as? OnClickItemListener)?.onClickItem(it) }
            }
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            addItemDecoration(CommonRecViewItemDecoration(context))
        }
    }

    var data: List<ServantInfoLevelListEntity>
        get() = arguments?.getParcelableArray(ARG_DATA)?.mapNotNull { it as? ServantInfoLevelListEntity }
                ?: listOf()
        set(value) {
            (arguments ?: Bundle().also { arguments = it })
                    .putParcelableArray(ARG_DATA, value.toTypedArray())
            if (::binding.isInitialized)
                binding.viewModel?.data?.value = value
        }

    companion object {
        private const val ARG_DATA = "data"
    }
}