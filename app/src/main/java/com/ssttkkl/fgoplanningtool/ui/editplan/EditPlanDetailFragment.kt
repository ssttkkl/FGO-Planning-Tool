package com.ssttkkl.fgoplanningtool.ui.editplan

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ssttkkl.fgoplanningtool.databinding.FragmentEditplanDetailBinding
import com.ssttkkl.fgoplanningtool.ui.servantinfo.ServantInfoDialogFragment

class EditPlanDetailFragment : Fragment(), LifecycleOwner, EditLevelDialogFragment.OnSaveListener {
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
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
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
