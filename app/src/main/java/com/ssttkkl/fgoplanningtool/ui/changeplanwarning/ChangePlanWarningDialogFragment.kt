package com.ssttkkl.fgoplanningtool.ui.changeplanwarning

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.data.Repo
import com.ssttkkl.fgoplanningtool.data.item.costItems
import com.ssttkkl.fgoplanningtool.data.plan.Plan
import com.ssttkkl.fgoplanningtool.ui.costitemlist.CostItemListActivity
import kotlinx.android.synthetic.main.fragment_changeplanwarning.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.runBlocking

class ChangePlanWarningDialogFragment : DialogFragment() {
    interface OnActionListener {
        fun onAction(mode: Mode, plans: Collection<Plan>, deductItems: Boolean)
    }

    private var listener: OnActionListener? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = when {
            parentFragment is OnActionListener -> parentFragment as OnActionListener
            activity is OnActionListener -> activity as OnActionListener
            else -> throw Exception("Either parent fragment or activity must impl OnActionListener interface.")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    private lateinit var plans: List<Plan>

    private lateinit var mode: Mode

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            plans = getParcelableArray(ARG_PLANS).map { it as Plan }
            mode = getSerializable(ARG_MODE) as Mode
        }
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AppTheme_Dialog_NoTitle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_changeplanwarning, container)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        deductItems_checkBox.isEnabled = runBlocking(CommonPool) {
            plans.costItems.all { (Repo.itemRepo.get(it.codename)?.count ?: 0) >= it.count }
        }

        viewItems_button.setOnClickListener {
            startActivity(Intent(activity, CostItemListActivity::class.java).apply {
                putExtra(CostItemListActivity.ARG_PLANS, plans.toTypedArray())
            })
        }

        preform_button.apply {
            setText(if (mode == Mode.Remove) R.string.remove_changeplanwarning else R.string.edit_changeplanwarning)
            setOnClickListener {
                listener?.onAction(mode, plans, dialog.deductItems_checkBox.isChecked)
                dismiss()
            }
        }

        cancel_button.setOnClickListener { dialog.cancel() }
    }

    companion object {
        private const val ARG_PLANS = "plans"
        private const val ARG_MODE = "mode"

        enum class Mode { Remove, Edit }

        @JvmStatic
        fun newInstanceForRemove(plan: Plan) = newInstanceForRemove(listOf(plan))

        @JvmStatic
        fun newInstanceForRemove(plans: Collection<Plan>) = newInstance(Mode.Remove, plans)

        @JvmStatic
        fun newInstanceForEdit(old: Plan, new: Plan): ChangePlanWarningDialogFragment {
            if (old.servantId != new.servantId)
                throw Exception("servantId of the old and the new must be same.")
            return newInstance(Mode.Edit, listOf(Plan(servantId = old.servantId,
                    nowStage = old.nowStage,
                    planStage = new.nowStage,
                    nowSkill1 = old.nowSkill1,
                    planSkill1 = new.nowSkill1,
                    nowSkill2 = old.nowSkill2,
                    planSkill2 = new.nowSkill2,
                    nowSkill3 = old.nowSkill3,
                    planSkill3 = new.nowSkill3
            )))
        }

        private fun newInstance(mode: Mode, plans: Collection<Plan>) =
                ChangePlanWarningDialogFragment().apply {
                    arguments = Bundle().apply {
                        putParcelableArray(ARG_PLANS, plans.toTypedArray())
                        putSerializable(ARG_MODE, mode)
                    }
                }

        val tag
            get() = ChangePlanWarningDialogFragment::class.qualifiedName
    }
}