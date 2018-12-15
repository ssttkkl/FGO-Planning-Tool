package com.ssttkkl.fgoplanningtool.ui.eventlist.editevent

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.data.event.Event
import com.ssttkkl.fgoplanningtool.data.event.LotteryEvent
import com.ssttkkl.fgoplanningtool.data.event.NormalEvent
import com.ssttkkl.fgoplanningtool.databinding.FragmentEditeventBinding
import com.ssttkkl.fgoplanningtool.ui.MainActivity
import com.ssttkkl.fgoplanningtool.ui.eventlist.editevent.lottery.EditLotteryEventFragmentViewModel
import com.ssttkkl.fgoplanningtool.ui.eventlist.editevent.lottery.EditLotteryEventPagerAdapter
import com.ssttkkl.fgoplanningtool.ui.eventlist.editevent.normal.EditNormalEventFragmentViewModel
import com.ssttkkl.fgoplanningtool.ui.eventlist.editevent.normal.EditNormalEventPagerAdapter

class EditEventFragment : Fragment() {
    private lateinit var binding: FragmentEditeventBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentEditeventBinding.inflate(inflater, container, false)
        binding.setLifecycleOwner(this)
        val event = arguments!!["event"] as Event
        binding.viewModel = ViewModelProviders.of(this)[when (event) {
            is NormalEvent -> EditNormalEventFragmentViewModel::class.java
            is LotteryEvent -> EditLotteryEventFragmentViewModel::class.java
            else -> throw Exception("Unknown event type.")
        }].apply {
            start(arguments!!["mode"] as Mode, event)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as? MainActivity)?.apply {
            drawerState = false
            title = binding.viewModel?.event?.descriptor?.localizedName ?: ""
        }
        setHasOptionsMenu(true)

        binding.viewPager.adapter = when (binding.viewModel) {
            is EditNormalEventFragmentViewModel -> EditNormalEventPagerAdapter(childFragmentManager)
            is EditLotteryEventFragmentViewModel -> EditLotteryEventPagerAdapter(childFragmentManager)
            else -> throw Exception("Unknown event type.")
        }
        binding.tabLayout.setupWithViewPager(binding.viewPager)

        binding.viewModel?.apply {
            finishEvent.observe(this@EditEventFragment, Observer {
                finish()
            })
            showMessageEvent.observe(this@EditEventFragment, Observer {
                showMessage(it ?: "")
            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.editevent, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.remove -> binding.viewModel?.onClickRemove()
            R.id.save -> binding.viewModel?.onClickSave()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun finish() {
        findNavController().popBackStack(R.id.eventListFragment, false)
    }

    private fun showMessage(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }
}