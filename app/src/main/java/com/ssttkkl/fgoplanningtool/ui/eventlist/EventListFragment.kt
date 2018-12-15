package com.ssttkkl.fgoplanningtool.ui.eventlist

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.data.event.Event
import com.ssttkkl.fgoplanningtool.data.event.LotteryEvent
import com.ssttkkl.fgoplanningtool.data.event.NormalEvent
import com.ssttkkl.fgoplanningtool.databinding.FragmentEventlistBinding
import com.ssttkkl.fgoplanningtool.ui.MainActivity
import com.ssttkkl.fgoplanningtool.ui.eventlist.editevent.Mode
import com.ssttkkl.fgoplanningtool.ui.utils.CommonRecViewItemDecoration

class EventListFragment : Fragment() {
    private lateinit var binding: FragmentEventlistBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentEventlistBinding.inflate(inflater, container, false)
        binding.setLifecycleOwner(this)
        binding.viewModel = ViewModelProviders.of(this)[EventListFragmentViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as? MainActivity)?.apply {
            drawerState = true
            title = getString(R.string.title_eventlist)
        }
        setHasOptionsMenu(true)

        binding.recView.apply {
            adapter = EventListFragmentRecViewAdapter(context!!, this@EventListFragment, binding.viewModel!!)
            addItemDecoration(CommonRecViewItemDecoration(context!!))
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        }
        binding.viewModel?.apply {
            gotoEditEventUIEvent.observe(this@EventListFragment, Observer {
                gotoEditEventUI(it ?: return@Observer)
            })
            gotoChooseEventUIEvent.observe(this@EventListFragment, Observer {
                gotoChooseEventUI()
            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.eventlist, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add -> binding.viewModel?.onClickAdd()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun gotoEditEventUI(event: Event) {
        findNavController().navigate(EventListFragmentDirections.actionEventListFragmentToEditEventFragment(Mode.Edit, event))
    }

    private fun gotoChooseEventUI() {
        findNavController().navigate(EventListFragmentDirections.actionEventListFragmentToChooseEventFragment())
    }
}