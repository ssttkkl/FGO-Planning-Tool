package com.ssttkkl.fgoplanningtool.ui.editplan.container

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ProgressBar
import com.bumptech.glide.Glide
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.resources.ConstantValues
import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider
import com.ssttkkl.fgoplanningtool.ui.editplan.EditPlanViewModel
import com.ssttkkl.fgoplanningtool.ui.servantinfo.ServantInfoDialogFragment
import kotlinx.android.synthetic.main.fragment_editplan.*

class EditPlanFragment : Fragment(), LifecycleOwner, EditLevelDialogFragment.OnSaveListener {
    private lateinit var viewModel: EditPlanViewModel

    private val servant
        get() = ResourcesProvider.instance.servants[viewModel.servantId.value]

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        viewModel = ViewModelProviders.of(activity!!).get(EditPlanViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_editplan, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.apply {
            name_textView.text = servant?.localizedName
            class_textView.text = servant?.theClass?.name
            Glide.with(this@EditPlanFragment).load(servant?.avatarFile).into(avatar_imageView)

            // level
            nowExp.observe(this@EditPlanFragment, Observer {
                onExpChanged(nowLevel_button, nowExp_progressBar, nowExp.value!!, ascendedOnNowStage.value == true)
            })
            ascendedOnNowStage.observe(this@EditPlanFragment, Observer {
                onExpChanged(nowLevel_button, nowExp_progressBar, nowExp.value!!, ascendedOnNowStage.value == true)
            })
            nowLevel_button.setOnClickListener {
                EditLevelDialogFragment.newInstance(servant!!.star, nowExp.value!!, ascendedOnNowStage.value == true, "now")
                        .show(childFragmentManager, EditLevelDialogFragment::class.qualifiedName)
            }
            planExp.observe(this@EditPlanFragment, Observer {
                onExpChanged(planLevel_button, planExp_progressBar, planExp.value!!, ascendedOnPlanStage.value == true)
            })
            ascendedOnPlanStage.observe(this@EditPlanFragment, Observer {
                onExpChanged(planLevel_button, planExp_progressBar, planExp.value!!, ascendedOnPlanStage.value == true)
            })
            planLevel_button.setOnClickListener {
                EditLevelDialogFragment.newInstance(servant!!.star, planExp.value!!, ascendedOnPlanStage.value == true, "plan")
                        .show(childFragmentManager, EditLevelDialogFragment::class.qualifiedName)
            }

            // skill
            listOf(Pair(nowSkill1_spinner, nowSkillI),
                    Pair(nowSkill2_spinner, nowSkillII),
                    Pair(nowSkill3_spinner, nowSkillIII),
                    Pair(planSkill1_spinner, planSkillI),
                    Pair(planSkill2_spinner, planSkillII),
                    Pair(planSkill3_spinner, planSkillIII))
                    .forEach { (spinner, data) ->
                        // first fill the values in spinners
                        spinner.adapter = ArrayAdapter<String>(context,
                                android.R.layout.simple_spinner_dropdown_item,
                                resources.getStringArray(R.array.skill_editplan))
                        spinner.setSelection(data.value!! - 1)

                        // observe values' changing
                        data.observe(this@EditPlanFragment, Observer {
                            // check if value really changed
                            if (spinner.selectedItemPosition != it!! - 1)
                                spinner.setSelection(it - 1)
                        })

                        // setup spinners' listeners
                        spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                                // check if value really changed
                                if (data.value != pos + 1) {
                                    data.value = pos + 1
                                }
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {}
                        }
                    }

            // dress
            if (servant?.dress?.isNotEmpty() == true) {
                dress_recView.apply {
                    adapter = EditPlanDressListRecViewAdapter(context!!).apply {
                        data = servant!!.dress
                        setOnCheckedChangeListener { pos, isChecked ->
                            if (isChecked)
                                dress.value = dress.value?.plus(pos) ?: setOf(pos)
                            else
                                dress.value = dress.value?.minus(pos)
                        }
                    }
                    layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    isNestedScrollingEnabled = false
                }
                dress.observe(this@EditPlanFragment, Observer {
                    (dress_recView.adapter as EditPlanDressListRecViewAdapter).setChecked(it
                            ?: setOf())
                })
            } else {
                dress_recView.visibility = View.GONE
                dressLabel_textView.visibility = View.GONE
            }
        }

        info_button.setOnClickListener {
            val servantID = viewModel.servantId.value
            if (servantID != null && servantID > 0)
                ServantInfoDialogFragment.newInstance(servantID)
                        .show(childFragmentManager, ServantInfoDialogFragment::class.qualifiedName)
        }
    }

    private fun onExpChanged(button: Button,
                             progressBar: ProgressBar,
                             exp: Int,
                             ascendOnStage: Boolean) {
        val level = ConstantValues.getLevel(exp)
        button.text = level.toString()
        progressBar.apply {
            if (servant?.stageMapToMaxLevel?.contains(level) == true && !ascendOnStage) {
                max = 100
                progress = 100
            } else {
                max = ConstantValues.nextLevelExp[level]
                progress = max - (ConstantValues.getExp(level + 1) - exp)
            }
        }
    }

    override fun onSave(exp: Int, ascendedOnStage: Boolean, extraTag: String) {
        when (extraTag) {
            "now" -> {
                viewModel.nowExp.value = exp
                viewModel.ascendedOnNowStage.value = ascendedOnStage
            }
            "plan" -> {
                viewModel.planExp.value = exp
                viewModel.ascendedOnPlanStage.value = ascendedOnStage
            }
        }
    }
}