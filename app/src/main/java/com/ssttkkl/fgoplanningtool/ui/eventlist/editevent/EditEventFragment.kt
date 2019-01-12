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
import com.ssttkkl.fgoplanningtool.databinding.FragmentEditeventBinding
import com.ssttkkl.fgoplanningtool.ui.MainActivity

class EditEventFragment : Fragment() {
    private lateinit var binding: FragmentEditeventBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentEditeventBinding.inflate(inflater, container, false)
        binding.setLifecycleOwner(this)
        binding.viewModel = ViewModelProviders.of(this)[EditEventFragmentViewModel::class.java].apply {
            start(arguments!!["mode"] as Mode, arguments!!["event"] as Event)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as? MainActivity)?.apply {
            drawerState = false
            title = binding.viewModel?.event?.descriptor?.localizedName ?: ""
        }
        setHasOptionsMenu(true)

        binding.viewPager.adapter = EditEventPagerAdapter(binding.viewModel!!, childFragmentManager)
        binding.tabLayout.setupWithViewPager(binding.viewPager)

        binding.viewModel?.apply {
            finishEvent.observe(this@EditEventFragment, Observer {
                finish()
            })
            showMessageEvent.observe(this@EditEventFragment, Observer {
                showMessage(it ?: "")
            })
            gotoConfirmChangeEventUIEvent.observe(this@EditEventFragment, Observer {
                gotoConfirmChangeEventUI(it ?: return@Observer)
            })
            gotoConfirmRemoveEventUIEvent.observe(this@EditEventFragment, Observer {
                gotoConfirmRemoveEventUI(it ?: return@Observer)
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

    private fun gotoConfirmChangeEventUI(event: Event) {
        findNavController().navigate(EditEventFragmentDirections.actionEditEventFragmentToConfirmChangeEventFragment(
                com.ssttkkl.fgoplanningtool.ui.eventlist.confirmchangeevent.Mode.Change, event))
    }

    private fun gotoConfirmRemoveEventUI(event: Event) {
        findNavController().navigate(EditEventFragmentDirections.actionEditEventFragmentToConfirmChangeEventFragment(
                com.ssttkkl.fgoplanningtool.ui.eventlist.confirmchangeevent.Mode.Remove, event))
    }
}