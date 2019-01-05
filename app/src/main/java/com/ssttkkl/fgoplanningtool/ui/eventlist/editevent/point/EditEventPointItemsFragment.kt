package com.ssttkkl.fgoplanningtool.ui.eventlist.editevent.point

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssttkkl.fgoplanningtool.databinding.FragmentEditeventPointitemsBinding
import com.ssttkkl.fgoplanningtool.ui.eventlist.editevent.EditEventFragmentViewModel
import com.ssttkkl.fgoplanningtool.ui.utils.CommonRecViewItemDecoration

class EditEventPointItemsFragment : Fragment() {
    private lateinit var binding: FragmentEditeventPointitemsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentEditeventPointitemsBinding.inflate(inflater, container, false)
        binding.viewModel = ViewModelProviders.of(parentFragment!!)[EditEventFragmentViewModel::class.java]
        binding.setLifecycleOwner(this)
        binding.poolCodename = arguments?.getString(ARG_CODENAME)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.recView.apply {
            adapter = PointItemRecViewAdapter(context!!, this@EditEventPointItemsFragment, binding.viewModel!!)
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            addItemDecoration(CommonRecViewItemDecoration(context!!, false, true))
        }
    }

    companion object {
        private const val ARG_CODENAME = "codename"
        fun newInstance(poolCodename: String) = EditEventPointItemsFragment().apply {
            arguments = bundleOf(ARG_CODENAME to poolCodename)
        }
    }
}