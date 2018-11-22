package com.ssttkkl.fgoplanningtool.ui.editplan

import android.os.Bundle
import androidx.lifecycle.LifecycleOwner
import androidx.databinding.DataBindingUtil
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.data.Repo
import com.ssttkkl.fgoplanningtool.data.plan.Plan
import com.ssttkkl.fgoplanningtool.databinding.ActivityEditplanBinding
import com.ssttkkl.fgoplanningtool.ui.servantlist.ServantListFragment
import com.ssttkkl.fgoplanningtool.ui.utils.BackHandlerActivity
import com.ssttkkl.fgoplanningtool.ui.utils.replaceFragment
import com.ssttkkl.fgoplanningtool.ui.utils.setStatusBarColor

class EditPlanActivity : BackHandlerActivity(),
        LifecycleOwner,
        ServantListFragment.OnClickServantListener {
    private lateinit var mode: Mode
    private var plan: Plan? = null

    private lateinit var binding: ActivityEditplanBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarColor()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_editplan)

        // setup the Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mode = intent.extras[ARG_MODE] as Mode
        if (mode == Mode.Edit)
            plan = intent.extras[ARG_PLAN] as Plan

        if (supportFragmentManager.fragments.isEmpty()) {
            when (mode) {
                Mode.New -> gotoServantListUI()
                Mode.Edit -> gotoEditPlanUI(plan!!)
            }
        }
    }

    private fun gotoServantListUI() {
        val existingServantIDs = Repo.planRepo.all.map { it.servantId }.toSet()
        replaceFragment(R.id.frameLayout,
                ServantListFragment.newInstance(existingServantIDs),
                ServantListFragment::class.qualifiedName)
    }

    private fun gotoEditPlanUI(plan: Plan) {
        replaceFragment(R.id.frameLayout,
                EditPlanFragment.newInstance(mode, plan),
                EditPlanFragment::class.qualifiedName)
    }

    override fun onBackPressed() {
        if (supportFragmentManager.findFragmentByTag(EditPlanFragment::class.qualifiedName) != null && mode == Mode.New)
            gotoServantListUI()
        else
            super.onBackPressed()
    }

    // callback of ServantListFragment
    override fun onClickServant(servantId: Int) {
        gotoEditPlanUI(Plan(servantId))
    }

    companion object {
        const val ARG_MODE = "mode"
        const val ARG_PLAN = "plan"
    }
}