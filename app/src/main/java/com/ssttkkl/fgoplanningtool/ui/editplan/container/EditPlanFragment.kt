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
import android.widget.AdapterView
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

            listOf(Triple(nowStage_spinner, nowStage, 0),
                    Triple(planStage_spinner, planStage, 0),
                    Triple(nowSkill1_spinner, nowSkillI, 1),
                    Triple(nowSkill2_spinner, nowSkillII, 1),
                    Triple(nowSkill3_spinner, nowSkillIII, 1),
                    Triple(planSkill1_spinner, planSkillI, 1),
                    Triple(planSkill2_spinner, planSkillII, 1),
                    Triple(planSkill3_spinner, planSkillIII, 1)
            ).forEach { (spinner, data, offset) ->
                // first fill the values in spinners
                spinner.setSelection(data.value!! - offset)

                // observe values changing
                data.observe(this@EditPlanFragment, Observer {
                    // check if value really changed
                    if (spinner.selectedItemPosition != it!! - offset)
                        spinner.setSelection(it - offset)
                })

                // setup spinners' listeners
                spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                        // check if value really changed
                        if (data.value != pos + offset) {
                            data.value = pos + offset
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
            }
        }
    }
}