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
import com.ssttkkl.fgoplanningtool.data.event.LotteryEvent
import com.ssttkkl.fgoplanningtool.data.event.NormalEvent
import com.ssttkkl.fgoplanningtool.databinding.FragmentEditeventLotteryBinding
import com.ssttkkl.fgoplanningtool.ui.MainActivity
import com.ssttkkl.fgoplanningtool.ui.utils.CommonRecViewItemDecoration

class EditLotteryEventFragment : Fragment() {
    private lateinit var binding: FragmentEditeventLotteryBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentEditeventLotteryBinding.inflate(inflater, container, false)
        binding.setLifecycleOwner(this)
        binding.viewModel = ViewModelProviders.of(this)[EditLotteryEventFragmentViewModel::class.java].apply {
            start(arguments!!["mode"] as Mode, arguments!!["event"] as LotteryEvent)
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
            adapter = CheckableItemRecViewAdapter(context!!, this@EditLotteryEventFragment, binding.viewModel!!)
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            addItemDecoration(CommonRecViewItemDecoration(context!!))
        }
        binding.storyGiftRecView.apply {
            adapter = ItemRecViewAdapter(context!!, this@EditLotteryEventFragment)
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            addItemDecoration(CommonRecViewItemDecoration(context!!))
        }
        binding.lotteryRecView.apply {
            adapter = ItemRecViewAdapter(context!!, this@EditLotteryEventFragment)
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            addItemDecoration(CommonRecViewItemDecoration(context!!))
        }
        binding.viewModel?.apply {
            finishEvent.observe(this@EditLotteryEventFragment, Observer {
                finish()
            })
            showMessageEvent.observe(this@EditLotteryEventFragment, Observer {
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