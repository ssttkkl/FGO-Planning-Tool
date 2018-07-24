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
import com.bumptech.glide.Glide
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider
import com.ssttkkl.fgoplanningtool.ui.editplan.EditPlanViewModel
import com.ssttkkl.fgoplanningtool.ui.servantinfo.ServantInfoDialogFragment
import com.ssttkkl.fgoplanningtool.ui.utils.DrawableAndTextSpinnerAdapter
import kotlinx.android.synthetic.main.fragment_editplan.*

class EditPlanFragment : Fragment(), LifecycleOwner {
    private lateinit var viewModel: EditPlanViewModel

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        viewModel = ViewModelProviders.of(activity!!).get(EditPlanViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_editplan, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.apply {
            val servant = ResourcesProvider.instance.servants[servantId.value]
            name_textView.text = servant?.localizedName ?: servantId.value?.toString()
            class_textView.text = servant?.theClass?.name
            Glide.with(this@EditPlanFragment).load(servant?.avatarFile).error(R.drawable.avatar_placeholder).into(avatar_imageView)

            var stageArr = resources.getStringArray(R.array.stage_editplan)
            if (servantId.value == 1)
                stageArr = stageArr.sliceArray(0..4)
            else {
                when (ResourcesProvider.instance.servants[servantId.value]?.star) {
                    5 -> stageArr = stageArr.sliceArray(0..9)
                    4 -> stageArr = stageArr.sliceArray(0..11)
                    3 -> stageArr = stageArr.sliceArray(0..13)
                }
            }

            listOf(Pair(nowStage_spinner, nowStage),
                    Pair(planStage_spinner, planStage)
            ).forEach { (spinner, data) ->
                spinner.adapter = DrawableAndTextSpinnerAdapter(context,
                        stageArr, R.drawable.holy_grail) { it > 4 }
                spinner.setSelection(data.value!!)

                // observe values changing
                data.observe(this@EditPlanFragment, Observer {
                    // check if value really changed
                    if (spinner.selectedItemPosition != it!!)
                        spinner.setSelection(it)
                })

                // setup spinners' listeners
                spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                        // check if value really changed
                        if (data.value != pos) {
                            data.value = pos
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
            }

            listOf(Pair(nowSkill1_spinner, nowSkillI),
                    Pair(nowSkill2_spinner, nowSkillII),
                    Pair(nowSkill3_spinner, nowSkillIII),
                    Pair(planSkill1_spinner, planSkillI),
                    Pair(planSkill2_spinner, planSkillII),
                    Pair(planSkill3_spinner, planSkillIII)
            ).forEach { (spinner, data) ->
                // first fill the values in spinners
                spinner.adapter = DrawableAndTextSpinnerAdapter(context,
                        resources.getStringArray(R.array.skill_editplan), 0) { false }
                spinner.setSelection(data.value!! - 1)

                // observe values changing
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

            if (servant?.dress?.isNotEmpty() == true) {
                dress_recView.apply {
                    adapter = EditPlanDressListRecViewAdapter(context!!).apply {
                        data = servant.dress
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
}