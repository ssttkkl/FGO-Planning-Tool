package com.ssttkkl.fgoplanningtool.ui.editplan

import android.arch.lifecycle.LifecycleOwner
import android.os.Bundle
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.data.plan.Plan
import com.ssttkkl.fgoplanningtool.ui.changeplanwarning.ChangePlanWarningDialogFragment
import com.ssttkkl.fgoplanningtool.ui.editplan.container.EditPlanContainerFragment
import com.ssttkkl.fgoplanningtool.ui.servantlist.ServantListFragment
import com.ssttkkl.fgoplanningtool.ui.utils.BackHandlerActivity
import com.ssttkkl.fgoplanningtool.ui.utils.setStatusBarColor

class EditPlanActivity : BackHandlerActivity(),
        LifecycleOwner,
        EditPlanContainerFragment.Callback,
        ServantListFragment.OnServantSelectedListener,
        ChangePlanWarningDialogFragment.OnActionListener {
    private lateinit var presenter: EditPlanActivityPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarColor()
        setContentView(R.layout.activity_editplan)

        presenter = EditPlanActivityPresenter(this)
    }

    // callback of ChangePlanWarningDialogFragment
    override fun onAction(mode: ChangePlanWarningDialogFragment.Companion.Mode, plans: Collection<Plan>, deductItems: Boolean) {
        presenter.onWarningFragmentAction(mode, plans.first(), deductItems)
    }

    // callback of EditPlanContainerFragment
    override fun onSaveAction() {
        presenter.onSaveAction()
    }

    override fun onRemoveAction() {
        presenter.onRemoveAction()
    }

    // callback of ServantListFragment
    override fun onServantSelected(servantId: Int) {
        presenter.onServantSelected(servantId)
    }

    companion object {
        const val ARG_MODE = "mode"
        const val ARG_PLAN = "plan"

        enum class Mode {
            New, Edit
        }
    }
}