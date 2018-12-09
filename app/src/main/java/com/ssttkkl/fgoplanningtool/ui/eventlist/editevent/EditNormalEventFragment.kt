package com.ssttkkl.fgoplanningtool.ui.eventlist.editevent

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.data.event.NormalEvent
import com.ssttkkl.fgoplanningtool.databinding.FragmentEditeventNormalBinding
import com.ssttkkl.fgoplanningtool.ui.MainActivity
import com.ssttkkl.fgoplanningtool.ui.utils.CommonRecViewItemDecoration

class EditNormalEventFragment : Fragment() {
    private lateinit var binding: FragmentEditeventNormalBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentEditeventNormalBinding.inflate(inflater, container, false)
        binding.setLifecycleOwner(this)
        binding.viewModel = ViewModelProviders.of(this)[EditNormalEventFragmentViewModel::class.java].apply {
            start(arguments!!["mode"] as Mode, arguments!!["event"] as NormalEvent)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as? MainActivity)?.apply {
            drawerState = false
            title = getString(R.string.title_editevent)
        }
        setHasOptionsMenu(true)

        binding.shopRecView.apply {
            adapter = CheckableItemRecViewAdapter(context!!, this@EditNormalEventFragment, binding.viewModel!!)
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            addItemDecoration(CommonRecViewItemDecoration(context!!))
        }
        binding.storyGiftRecView.apply {
            adapter = ItemRecViewAdapter(context!!, this@EditNormalEventFragment)
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            addItemDecoration(CommonRecViewItemDecoration(context!!))
        }
        binding.viewModel?.apply {
            finishEvent.observe(this@EditNormalEventFragment, Observer {
                finish()
            })
            showMessageEvent.observe(this@EditNormalEventFragment, Observer {
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