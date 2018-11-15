package com.ssttkkl.fgoplanningtool.ui.editplan

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.ssttkkl.fgoplanningtool.data.plan.Plan
import com.ssttkkl.fgoplanningtool.ui.costitemlist.CostItemListFragment
import com.ssttkkl.fgoplanningtool.ui.utils.databinding.TitlesHolder

class EditPlanFragmentPagerAdapter(fm: androidx.fragment.app.FragmentManager) : androidx.fragment.app.FragmentPagerAdapter(fm), TitlesHolder {
    override var titles: List<String> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var plan: Plan? = null
        set(value) {
            field = value
            _costItemList?.plans = listOfNotNull(value)
        }

    private var _editPlanDetail: EditPlanDetailFragment? = null
    private var _costItemList: CostItemListFragment? = null

    val editPlanDetail: EditPlanDetailFragment
        get() {
            if (_editPlanDetail == null) {
                synchronized(this) {
                    if (_editPlanDetail == null)
                        _editPlanDetail = EditPlanDetailFragment()
                }
            }
            return _editPlanDetail!!
        }

    val costItemList: CostItemListFragment
        get() {
            if (_costItemList == null) {
                synchronized(this) {
                    if (_costItemList == null)
                        _costItemList = CostItemListFragment().apply {
                            plans = listOfNotNull(plan)
                        }
                }
            }
            return _costItemList!!
        }

    override fun getPageTitle(pos: Int): String? = titles.getOrNull(pos)

    override fun getItem(pos: Int): androidx.fragment.app.Fragment =
            if (pos == 0)
                editPlanDetail
            else
                costItemList

    override fun getCount() = 2
}