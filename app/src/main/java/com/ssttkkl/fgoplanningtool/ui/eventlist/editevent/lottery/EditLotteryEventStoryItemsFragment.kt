package com.ssttkkl.fgoplanningtool.ui.eventlist.editevent.lottery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssttkkl.fgoplanningtool.databinding.FragmentEditeventLotteryStoryitemsBinding
import com.ssttkkl.fgoplanningtool.ui.eventlist.editevent.ItemRecViewAdapter
import com.ssttkkl.fgoplanningtool.ui.utils.CommonRecViewItemDecoration

class EditLotteryEventStoryItemsFragment : Fragment() {
    private lateinit var binding: FragmentEditeventLotteryStoryitemsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentEditeventLotteryStoryitemsBinding.inflate(inflater, container, false)
        binding.viewModel = ViewModelProviders.of(parentFragment!!)[EditLotteryEventFragmentViewModel::class.java]
        binding.setLifecycleOwner(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.storyGiftRecView.apply {
            adapter = ItemRecViewAdapter(context!!, this@EditLotteryEventStoryItemsFragment)
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            addItemDecoration(CommonRecViewItemDecoration(context!!))
        }
    }
}