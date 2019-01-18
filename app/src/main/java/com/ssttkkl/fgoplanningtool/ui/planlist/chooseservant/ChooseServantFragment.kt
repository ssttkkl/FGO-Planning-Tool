package com.ssttkkl.fgoplanningtool.ui.planlist.chooseservant

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.data.plan.Plan
import com.ssttkkl.fgoplanningtool.ui.MainActivity
import com.ssttkkl.fgoplanningtool.ui.planlist.editplan.Mode
import com.ssttkkl.fgoplanningtool.ui.servantlist.ServantListFragment

class ChooseServantFragment : Fragment(), ServantListFragment.OnClickServantListener {
    private lateinit var viewModel: ChooseServantFragmentViewModel

    private val servantListFragment
        get() = childFragmentManager.findFragmentById(R.id.servantListFragment) as? ServantListFragment

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewModel = ViewModelProviders.of(this)[ChooseServantFragmentViewModel::class.java]
        return inflater.inflate(R.layout.fragment_editplan_chooseservant, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as? MainActivity)?.apply {
            title = getString(R.string.chooseServant)
            drawerState = false
        }
        setHasOptionsMenu(true)

        viewModel.hiddenServantIDs.observe(this, Observer {
            servantListFragment?.hiddenServantIDs = it ?: setOf()
        })
    }

    override fun onClickServant(servantID: Int) {
        findNavController().navigate(ChooseServantFragmentDirections.actionChooseServantFragmentToEditPlanDetailFragment(Plan(servantID), Mode.New))
    }
}