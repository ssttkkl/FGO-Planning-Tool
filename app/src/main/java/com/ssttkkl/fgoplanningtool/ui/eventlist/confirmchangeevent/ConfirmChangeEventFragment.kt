package com.ssttkkl.fgoplanningtool.ui.eventlist.confirmchangeevent

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
import com.ssttkkl.fgoplanningtool.databinding.FragmentConfirmchangeeventBinding
import com.ssttkkl.fgoplanningtool.ui.MainActivity

class ConfirmChangeEventFragment : Fragment() {
    private lateinit var binding: FragmentConfirmchangeeventBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentConfirmchangeeventBinding.inflate(inflater, container, false)
        binding.viewModel = ViewModelProviders.of(this)[ConfirmChangeEventFragmentViewModel::class.java].apply {
            mode.value = arguments?.getParcelable(ARG_MODE) as? Mode
            event.value = arguments?.getParcelable(ARG_EVENT) as? Event
        }
        binding.setLifecycleOwner(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as? MainActivity)?.apply {
            drawerState = false
        }
        setHasOptionsMenu(true)

        binding.recView.apply {
            adapter = AddItemListRecViewAdapter(context!!, this@ConfirmChangeEventFragment, binding.viewModel!!)
            layoutManager = LinearLayoutManager(context!!, RecyclerView.VERTICAL, false)
        }

        binding.viewModel?.apply {
            finishEvent.observe(this@ConfirmChangeEventFragment, Observer {
                finish()
            })
            title.observe(this@ConfirmChangeEventFragment, Observer {
                onTitleChanged(it ?: "")
            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.confirmchangeevent, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.yes -> binding.viewModel?.onClickYes()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun finish() {
        findNavController().popBackStack(R.id.eventListFragment, false)
    }

    private fun onTitleChanged(title: String) {
        (activity as? MainActivity)?.title = title
    }

    companion object {
        private const val ARG_MODE = "mode"
        private const val ARG_EVENT = "event"
    }
}