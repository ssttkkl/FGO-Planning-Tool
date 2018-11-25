package com.ssttkkl.fgoplanningtool.ui.iteminfo

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.databinding.FragmentIteminfoRequirementlistBinding
import com.ssttkkl.fgoplanningtool.ui.requirementlist.RequirementListEntity
import com.ssttkkl.fgoplanningtool.ui.requirementlist.RequirementListRecViewAdapter
import com.ssttkkl.fgoplanningtool.ui.servantinfo.ServantInfoDialogFragment
import com.ssttkkl.fgoplanningtool.ui.utils.CommonRecViewItemDecoration
import kotlinx.android.synthetic.main.fragment_iteminfo_requirementlist.*

class RequirementListFragment : Fragment() {
    interface OnClickItemListener {
        fun onClickItem(entity: RequirementListEntity)
    }

    private lateinit var binding: FragmentIteminfoRequirementlistBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentIteminfoRequirementlistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.recView.apply {
            adapter = RequirementListRecViewAdapter(context!!).apply {
                submitList(data)
                setOnItemClickListener {
                    (parentFragment as? OnClickItemListener)?.onClickItem(it)
                }
            }
            layoutManager = LinearLayoutManager(context!!, RecyclerView.VERTICAL, false)
            addItemDecoration(CommonRecViewItemDecoration(context!!))
        }
    }

    var data: List<RequirementListEntity>
        get() = arguments?.getParcelableArray(ARG_DATA)?.mapNotNull { it as? RequirementListEntity } ?: listOf()
        set(value) {
            (arguments ?: Bundle().also { arguments = it })
                .putParcelableArray(ARG_DATA, value.toTypedArray())

            if (::binding.isInitialized)
                (binding.recView.adapter as? RequirementListRecViewAdapter)?.submitList(value)
        }

    companion object {
        const val ARG_DATA = "data"
    }
}