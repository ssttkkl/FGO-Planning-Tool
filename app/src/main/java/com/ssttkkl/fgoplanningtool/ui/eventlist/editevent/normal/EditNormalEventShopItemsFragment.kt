package com.ssttkkl.fgoplanningtool.ui.eventlist.editevent.normal

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
import com.ssttkkl.fgoplanningtool.databinding.FragmentEditeventNormalShopitemsBinding
import com.ssttkkl.fgoplanningtool.ui.eventlist.editevent.CheckableItemRecViewAdapter
import com.ssttkkl.fgoplanningtool.ui.utils.CommonRecViewItemDecoration

class EditNormalEventShopItemsFragment : Fragment(), SpeedDialView.OnActionSelectedListener {
    private lateinit var binding: FragmentEditeventNormalShopitemsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentEditeventNormalShopitemsBinding.inflate(inflater, container, false)
        binding.viewModel = ViewModelProviders.of(parentFragment!!)[EditNormalEventFragmentViewModel::class.java]
        binding.setLifecycleOwner(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.shopRecView.apply {
            adapter = CheckableItemRecViewAdapter(context!!, this@EditNormalEventShopItemsFragment, binding.viewModel!!)
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            addItemDecoration(CommonRecViewItemDecoration(context!!, false, true))
        }
        binding.speedDial.inflate(R.menu.editevent_shopitems)
        binding.speedDial.setOnActionSelectedListener(this)
    }

    override fun onActionSelected(actionItem: SpeedDialActionItem?): Boolean {
        return binding.viewModel?.onClickShopItemsSpeedDialItem(actionItem) == true
    }
}