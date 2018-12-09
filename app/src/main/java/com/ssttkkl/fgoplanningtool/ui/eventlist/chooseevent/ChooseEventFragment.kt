package com.ssttkkl.fgoplanningtool.ui.eventlist.chooseevent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.data.event.LotteryEvent
import com.ssttkkl.fgoplanningtool.data.event.NormalEvent
import com.ssttkkl.fgoplanningtool.databinding.FragmentChooseeventBinding
import com.ssttkkl.fgoplanningtool.ui.MainActivity
import com.ssttkkl.fgoplanningtool.ui.eventlist.editevent.Mode

class ChooseEventFragment : Fragment() {
    private lateinit var binding: FragmentChooseeventBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentChooseeventBinding.inflate(inflater, container, false)
        binding.setLifecycleOwner(this)
        binding.viewModel = ViewModelProviders.of(this)[ChooseEventFragmentViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as? MainActivity)?.apply {
            drawerState = false
            title = getString(R.string.title_chooseevent)
        }

        binding.viewPager.adapter = ChooseEventFragmentPagerAdapter(childFragmentManager, this, binding.viewModel!!)
        binding.tabLayout.setupWithViewPager(binding.viewPager)
        binding.viewModel?.apply {
            gotoEditNormalEventUIEvent.observe(this@ChooseEventFragment, Observer {
                gotoEditNormalEventUI(it ?: return@Observer)
            })
            gotoEditLotteryEventUIEvent.observe(this@ChooseEventFragment, Observer {
                gotoEditLotteryEventUI(it ?: return@Observer)
            })
        }
    }

    private fun gotoEditNormalEventUI(event: NormalEvent) {
        findNavController().navigate(ChooseEventFragmentDirections.actionChooseEventFragmentToEditNormalEventFragment(Mode.New, event))
    }

    private fun gotoEditLotteryEventUI(event: LotteryEvent) {
        findNavController().navigate(ChooseEventFragmentDirections.actionChooseEventFragmentToEditLotteryEventFragment(Mode.New, event))
    }
}