package com.ssttkkl.fgoplanningtool.ui.editplan

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ssttkkl.fgoplanningtool.databinding.FragmentEditplanDetailBinding
import com.ssttkkl.fgoplanningtool.ui.servantinfo.ServantInfoDialogFragment

class EditPlanDetailFragment : androidx.fragment.app.Fragment(), LifecycleOwner, EditLevelDialogFragment.OnSaveListener {
    private lateinit var binding: FragmentEditplanDetailBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentEditplanDetailBinding.inflate(inflater, container, false)
        binding.setLifecycleOwner(this)
        if (parentFragment is EditPlanFragment)
            binding.viewModel = ViewModelProviders.of(parentFragment!!)[EditPlanFragmentViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.dressRecView.apply {
            adapter = EditPlanDressListRecViewAdapter(context!!)
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            isNestedScrollingEnabled = false
        }

        binding.apply {
            nowLevelButton.setOnClickListener {
                viewModel?.onClickNowLevel()
            }
            planLevelButton.setOnClickListener {
                viewModel?.onClickPlanLevel()
            }
            infoButton.setOnClickListener {
                viewModel?.onClickShowInfo()
            }
        }

        binding.viewModel?.apply {
            showServantInfoEvent.observe(this@EditPlanDetailFragment, Observer {
                showInfo(it ?: return@Observer)
            })
            showEditNowLevelUIEvent.observe(this@EditPlanDetailFragment, Observer {
                if (it != null)
                    showEditLevelUI(it.first, it.second, it.third, "now")
            })
            showEditPlanLevelUIEvent.observe(this@EditPlanDetailFragment, Observer {
                if (it != null)
                    showEditLevelUI(it.first, it.second, it.third, "plan")
            })
        }
    }

    private fun showEditLevelUI(servantID: Int, exp: Int, ascendedOnStage: Boolean, tag: String) {
        EditLevelDialogFragment.newInstance(servantID, exp, ascendedOnStage, tag)
                .show(childFragmentManager, EditLevelDialogFragment::class.qualifiedName)
    }

    private fun showInfo(servantID: Int) {
        if (servantID > 0)
            ServantInfoDialogFragment.newInstance(servantID)
                    .show(childFragmentManager, ServantInfoDialogFragment::class.qualifiedName)
    }

    override fun onSave(exp: Int, ascendedOnStage: Boolean, extraTag: String?) {
        when (extraTag) {
            "now" -> binding.viewModel?.onEditNowLevelResult(exp, ascendedOnStage)
            "plan" -> binding.viewModel?.onEditPlanLevelResult(exp, ascendedOnStage)
        }
    }
}
