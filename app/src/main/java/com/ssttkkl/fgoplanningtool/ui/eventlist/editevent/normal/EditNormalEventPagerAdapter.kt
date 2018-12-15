package com.ssttkkl.fgoplanningtool.ui.eventlist.editevent.normal

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.R

class EditNormalEventPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> EditNormalEventShopItemsFragment()
            1 -> EditNormalEventStoryItemsFragment()
            else -> throw Exception("Unknown position.")
        }
    }

    override fun getCount(): Int = 2

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> MyApp.context.getString(R.string.shop_editevent)
            1 -> MyApp.context.getString(R.string.storyGift_editevent)
            else -> ""
        }
    }
}