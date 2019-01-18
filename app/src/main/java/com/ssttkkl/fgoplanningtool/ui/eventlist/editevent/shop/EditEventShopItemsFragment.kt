package com.ssttkkl.fgoplanningtool.ui.eventlist.editevent.shop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.databinding.FragmentEditeventShopitemsBinding
import com.ssttkkl.fgoplanningtool.ui.eventlist.editevent.EditEventFragmentViewModel
import com.ssttkkl.fgoplanningtool.ui.utils.CommonRecViewItemDecoration

class EditEventShopItemsFragment : Fragment(), SpeedDialView.OnActionSelectedListener {
    private lateinit var binding: FragmentEditeventShopitemsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentEditeventShopitemsBinding.inflate(inflater, container, false)
        binding.viewModel = ViewModelProviders.of(parentFragment!!)[EditEventFragmentViewModel::class.java]
        binding.setLifecycleOwner(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.shopRecView.apply {
            adapter = CheckableItemRecViewAdapter(context!!, this@EditEventShopItemsFragment, binding.viewModel!!)
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            addItemDecoration(CommonRecViewItemDecoration(context!!))
        }
        binding.speedDial.inflate(R.menu.editevent_shopitems)
        binding.speedDial.setOnActionSelectedListener(this)
    }

    override fun onActionSelected(actionItem: SpeedDialActionItem?): Boolean {
        return binding.viewModel?.onClickShopItemsSpeedDialItem(actionItem) == true
    }
}