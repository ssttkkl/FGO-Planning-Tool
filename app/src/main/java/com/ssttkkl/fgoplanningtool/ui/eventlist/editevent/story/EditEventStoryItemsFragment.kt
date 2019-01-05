package com.ssttkkl.fgoplanningtool.ui.eventlist.editevent.story

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssttkkl.fgoplanningtool.databinding.FragmentEditeventStoryitemsBinding
import com.ssttkkl.fgoplanningtool.ui.eventlist.editevent.EditEventFragmentViewModel
import com.ssttkkl.fgoplanningtool.ui.eventlist.editevent.ItemRecViewAdapter
import com.ssttkkl.fgoplanningtool.ui.utils.CommonRecViewItemDecoration

class EditEventStoryItemsFragment : Fragment() {
    private lateinit var binding: FragmentEditeventStoryitemsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentEditeventStoryitemsBinding.inflate(inflater, container, false)
        binding.viewModel = ViewModelProviders.of(parentFragment!!)[EditEventFragmentViewModel::class.java]
        binding.setLifecycleOwner(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.storyGiftRecView.apply {
            adapter = ItemRecViewAdapter(context!!, this@EditEventStoryItemsFragment)
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            addItemDecoration(CommonRecViewItemDecoration(context!!))
        }
    }
}