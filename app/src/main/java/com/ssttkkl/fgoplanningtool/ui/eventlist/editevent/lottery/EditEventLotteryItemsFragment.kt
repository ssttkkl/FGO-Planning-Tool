package com.ssttkkl.fgoplanningtool.ui.eventlist.editevent.lottery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssttkkl.fgoplanningtool.databinding.FragmentEditeventLotteryitemsBinding
import com.ssttkkl.fgoplanningtool.ui.eventlist.editevent.EditEventFragmentViewModel
import com.ssttkkl.fgoplanningtool.ui.eventlist.editevent.ItemRecViewAdapter
import com.ssttkkl.fgoplanningtool.ui.utils.CommonRecViewItemDecoration

class EditEventLotteryItemsFragment : Fragment() {
    private lateinit var binding: FragmentEditeventLotteryitemsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentEditeventLotteryitemsBinding.inflate(inflater, container, false)
        binding.viewModel = ViewModelProviders.of(parentFragment!!)[EditEventFragmentViewModel::class.java]
        binding.setLifecycleOwner(this)
        binding.lotteryIndex = arguments?.getInt(ARG_INDEX)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.recView.apply {
            adapter = ItemRecViewAdapter(context!!, this@EditEventLotteryItemsFragment)
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            addItemDecoration(CommonRecViewItemDecoration(context!!, false, true))
        }
    }

    companion object {
        private const val ARG_INDEX = "lotteryIndex"
        fun newInstance(lotteryIndex: Int) = EditEventLotteryItemsFragment().apply {
            arguments = bundleOf(ARG_INDEX to lotteryIndex)
        }
    }
}