package com.ssttkkl.fgoplanningtool.ui.eventlist.editevent.lottery

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.R

class EditLotteryEventPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> EditLotteryEventShopItemsFragment()
            1 -> EditLotteryEventLotteryItemsFragment()
            2 -> EditLotteryEventStoryItemsFragment()
            else -> throw Exception("Unknown position.")
        }
    }

    override fun getCount(): Int = 3

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> MyApp.context.getString(R.string.eventShop)
            1 -> MyApp.context.getString(R.string.eventLottery)
            2 -> MyApp.context.getString(R.string.storyGift)
            else -> ""
        }
    }
}