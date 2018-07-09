package com.ssttkkl.fgoplanningtool.ui.editplan.container

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider
import com.ssttkkl.fgoplanningtool.ui.editplan.EditPlanViewModel
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
            ResourcesProvider.servants[servantId.value]!!.apply {
                name_textView.text = localizedName
                class_textView.text = theClass.toString()
                Glide.with(this@EditPlanFragment).load(avatarUri).into(avatar_imageView)
            }

            listOf(Pair(nowStage_spinner, nowStage),
                    Pair(planStage_spinner, planStage)
            ).forEach { (spinner, data) ->
                spinner.adapter = DrawableAndTextSpinnerAdapter(context, resources.getStringArray(R.array.stage_editplan), R.drawable.holy_grail) { it > 4 }
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
                spinner.adapter = DrawableAndTextSpinnerAdapter(context, resources.getStringArray(R.array.skill_editplan), 0) { false }
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
        }
    }
}