package com.ssttkkl.fgoplanningtool.ui.costitemlist


import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.data.Repo
import com.ssttkkl.fgoplanningtool.data.item.groupedCostItems
import com.ssttkkl.fgoplanningtool.data.plan.Plan
import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider
import com.ssttkkl.fgoplanningtool.ui.requirementlist.RequirementListEntity
import com.ssttkkl.fgoplanningtool.ui.servantinfo.ServantInfoDialogFragment
import com.ssttkkl.fgoplanningtool.ui.utils.CommonRecViewItemDecoration
import kotlinx.android.synthetic.main.fragment_costitemlist.*

class CostItemListFragment : androidx.fragment.app.Fragment() {
    var plans: Collection<Plan> = listOf()
        set(value) {
            field = value
            (recView?.adapter as? CostItemListAdapter)?.data = generateEntities(plans)
            recView?.visibility = if (value.isEmpty()) View.GONE else View.VISIBLE
        }

    private fun generateEntities(plans: Collection<Plan>): List<CostItemListEntity> {
        // key: item's codename
        // value: a set contains pairs, each stands for a servant requires this item.
        //        the first is servantID, the second is requirement.
        val map = plans.groupedCostItems

        val entities = map.map { (codename, req) ->
            var need = 0L
            req.forEach {
                need += it.value
            }

            val descriptor = ResourcesProvider.instance.itemDescriptors[codename]
            CostItemListEntity(name = descriptor?.localizedName ?: codename,
                    type = descriptor?.type,
                    need = need,
                    own = Repo.itemRepo[codename].count,
                    imgFile = descriptor?.imgFile,
                    codename = codename,
                    requirements = req.entries.asSequence().sortedBy { it.key }.map { (servantID, count) ->
                        val servant = ResourcesProvider.instance.servants[servantID]
                        RequirementListEntity(servantID = servantID,
                                name = servant?.localizedName ?: servantID.toString(),
                                requirement = count,
                                avatarFile = servant?.avatarFile)
                    }.toList())
        }
        return entities.asSequence().groupBy { it.type }.toList().sortedBy { it.first } // group items and sort groups by type
                .map { it.second.sortedBy { ResourcesProvider.instance.itemRank[it.codename] } }.toList().flatMap { it } // sort each group's items and flat
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null && savedInstanceState.containsKey(ARG_PLANS))
            plans = savedInstanceState.getParcelableArray(ARG_PLANS).map { it as Plan }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_costitemlist, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recView.apply {
            adapter = CostItemListAdapter(context!!).apply {
                data = generateEntities(plans)
                setOnServantClickListener { gotoServantDetailUi(it) }
                if (savedInstanceState != null && savedInstanceState.containsKey(ARG_EXPANDED))
                    expendedPosition = savedInstanceState.getInt(ARG_EXPANDED)
            }
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context!!, androidx.recyclerview.widget.LinearLayoutManager.VERTICAL, false)
            addItemDecoration(CommonRecViewItemDecoration(context!!, false, true))
            visibility = if (plans.isEmpty()) View.GONE else View.VISIBLE
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelableArray(ARG_PLANS, plans.toTypedArray())
        outState.putInt(ARG_EXPANDED, (recView?.adapter as? CostItemListAdapter)?.expendedPosition
                ?: -1)
    }

    private fun gotoServantDetailUi(servantID: Int) {
        ServantInfoDialogFragment.newInstance(servantID)
                .show(childFragmentManager, ServantInfoDialogFragment::class.qualifiedName)
    }

    companion object {
        private const val ARG_PLANS = "plans"
        private const val ARG_EXPANDED = "expanded"
    }
}