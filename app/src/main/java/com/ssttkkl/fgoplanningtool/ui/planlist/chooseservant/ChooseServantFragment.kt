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
import com.ssttkkl.fgoplanningtool.databinding.FragmentEditplanChooseservantBinding
import com.ssttkkl.fgoplanningtool.ui.MainActivity
import com.ssttkkl.fgoplanningtool.ui.planlist.editplan.Mode
import com.ssttkkl.fgoplanningtool.ui.servantlist.ServantListFragment
import com.ssttkkl.fgoplanningtool.ui.utils.replaceFragment

class ChooseServantFragment : Fragment(), ServantListFragment.OnClickServantListener {
    private lateinit var binding: FragmentEditplanChooseservantBinding

    private val servantListFragment
        get() = childFragmentManager.findFragmentByTag(ServantListFragment::class.qualifiedName) as? ServantListFragment

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentEditplanChooseservantBinding.inflate(inflater, container, false)
        binding.setLifecycleOwner(this)
        binding.viewModel = ViewModelProviders.of(this)[ChooseServantFragmentViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as? MainActivity)?.apply {
            title = getString(R.string.title_editplan_chooseservant)
            drawerState = false
        }
        setHasOptionsMenu(true)

        replaceFragment(R.id.frameLayout, ServantListFragment(), ServantListFragment::class.qualifiedName)

        binding.viewModel?.hiddenServantIDs?.observe(this, Observer {
            servantListFragment?.hiddenServantIDs = it ?: setOf()
        })
    }

    override fun onClickServant(servantId: Int) {
        findNavController().navigate(R.id.action_chooseServantFragment_to_editPlanDetailFragment, Bundle().apply {
            putParcelable("plan", Plan(servantId))
            putParcelable("mode", Mode.New)
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            findNavController().navigateUp()
            true
        } else super.onOptionsItemSelected(item)
    }
}