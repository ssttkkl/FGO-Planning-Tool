package com.ssttkkl.fgoplanningtool.ui.eventlist.chooseevent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssttkkl.fgoplanningtool.databinding.FragmentChooseeventListBinding
import com.ssttkkl.fgoplanningtool.ui.utils.CommonRecViewItemDecoration

class ChooseEventListFragment : Fragment() {
    private lateinit var binding: FragmentChooseeventListBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentChooseeventListBinding.inflate(inflater, container, false)
        binding.setLifecycleOwner(this)
        binding.viewModel = ViewModelProviders.of(parentFragment!!)[ChooseEventFragmentViewModel::class.java]
        binding.year = arguments!![ARG_YEAR] as Int
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.recView.apply {
            adapter = ChooseEventListRecViewAdapter(context!!, this@ChooseEventListFragment, binding.viewModel!!)
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            addItemDecoration(CommonRecViewItemDecoration(context!!))
        }
    }

    companion object {
        private const val ARG_YEAR = "year"
        fun newInstance(year: Int) = ChooseEventListFragment().apply {
            arguments = bundleOf(ARG_YEAR to year)
        }
    }
}