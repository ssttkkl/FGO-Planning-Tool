package com.ssttkkl.fgoplanningtool.ui.eventlist.editevent

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.ui.eventlist.editevent.lottery.EditEventLotteryItemsFragment
import com.ssttkkl.fgoplanningtool.ui.eventlist.editevent.point.EditEventPointItemsFragment
import com.ssttkkl.fgoplanningtool.ui.eventlist.editevent.shop.EditEventShopItemsFragment
import com.ssttkkl.fgoplanningtool.ui.eventlist.editevent.story.EditEventStoryItemsFragment

class EditEventPagerAdapter(private val viewModel: EditEventFragmentViewModel,
                            fm: FragmentManager) : FragmentPagerAdapter(fm) {
    val tabs: List<Pair<String, String>>

    init {
        tabs = ArrayList()
        viewModel.event.descriptor?.apply {
            if (shopItems.isNotEmpty())
                tabs.add(Pair(SHOP, ""))
            lotteries.keys.forEach { lotteryCodename ->
                tabs.add(Pair(LOTTERY, lotteryCodename))
            }
            pointPools.keys.forEach { pointCodename ->
                tabs.add(Pair(POINT, pointCodename))
            }
            if (storyItems.isNotEmpty() || storyItemsIfNotParticipated.isNotEmpty() || storyItemsIfParticipated.isNotEmpty())
                tabs.add(Pair(STORY, ""))
        }
    }

    override fun getItem(position: Int): Fragment {
        return when (tabs[position].first) {
            SHOP -> EditEventShopItemsFragment()
            LOTTERY -> EditEventLotteryItemsFragment.newInstance(tabs[position].second)
            POINT -> EditEventPointItemsFragment.newInstance(tabs[position].second)
            STORY -> EditEventStoryItemsFragment()
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
            else -> ""
        }
    }

    companion object {
        private const val STORY = "story"
        private const val SHOP = "shop"
        private const val LOTTERY = "lottery"
        private const val POINT = "point"
    }
}