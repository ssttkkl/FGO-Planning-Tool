package com.ssttkkl.fgoplanningtool.ui.eventlist.editevent

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.ui.eventlist.editevent.lottery.EditEventLotteryItemsFragment
import com.ssttkkl.fgoplanningtool.ui.eventlist.editevent.total.EditEventTotalItemsFragment
import com.ssttkkl.fgoplanningtool.ui.eventlist.editevent.point.EditEventPointItemsFragment
import com.ssttkkl.fgoplanningtool.ui.eventlist.editevent.shop.EditEventShopItemsFragment
import com.ssttkkl.fgoplanningtool.ui.eventlist.editevent.story.EditEventStoryItemsFragment

class EditEventPagerAdapter(private val viewModel: EditEventFragmentViewModel,
                            fm: FragmentManager) : FragmentPagerAdapter(fm) {
    val tabs: List<Pair<String, Int>>

    init {
        tabs = ArrayList()
        viewModel.event.descriptor?.apply {
            if (shopItems.isNotEmpty())
                tabs.add(Pair(SHOP, 0))
            lotteries.indices.forEach { idx ->
                tabs.add(Pair(LOTTERY, idx))
            }
            pointPools.indices.forEach { idx ->
                tabs.add(Pair(POINT, idx))
            }
            if (storyItems.isNotEmpty() || storyItemsIfNotParticipated.isNotEmpty() || storyItemsIfParticipated.isNotEmpty())
                tabs.add(Pair(STORY, 0))
        }
        tabs.add(Pair(TOTAL, 0))
    }

    override fun getItem(position: Int): Fragment {
        return when (tabs[position].first) {
            SHOP -> EditEventShopItemsFragment()
            LOTTERY -> EditEventLotteryItemsFragment.newInstance(tabs[position].second)
            POINT -> EditEventPointItemsFragment.newInstance(tabs[position].second)
            STORY -> EditEventStoryItemsFragment()
            TOTAL -> EditEventTotalItemsFragment()
            else -> throw Exception("Unknown position.")
        }
    }

    override fun getCount(): Int = tabs.size

    override fun getPageTitle(position: Int): CharSequence? {
        return when (tabs[position].first) {
            SHOP -> MyApp.context.getString(R.string.eventShop)
            LOTTERY -> viewModel.event.descriptor?.lotteries?.get(tabs[position].second)?.localizedName
            POINT -> viewModel.event.descriptor?.pointPools?.get(tabs[position].second)?.localizedName
            STORY -> MyApp.context.getString(R.string.storyGift)
            TOTAL -> MyApp.context.getString(R.string.totalItems)
            else -> ""
        }
    }

    companion object {
        private const val STORY = "story"
        private const val SHOP = "shop"
        private const val LOTTERY = "lottery"
        private const val POINT = "point"
        private const val TOTAL = "total"
    }
}