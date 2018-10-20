package com.ssttkkl.fgoplanningtool.ui.utils.databinding

import android.databinding.BindingAdapter
import android.support.v4.view.ViewPager

interface TitlesHolder {
    var titles: List<String>
}

object ViewPagerBindingAdapter {
    @BindingAdapter("titles")
    @JvmStatic
    fun setTitles(pager: ViewPager, titles: List<String>) {
        (pager.adapter as TitlesHolder).titles = titles
    }

    @BindingAdapter("titles")
    @JvmStatic
    fun setTitles(pager: ViewPager, titles: Array<String>) {
        (pager.adapter as TitlesHolder).titles = titles.toList()
    }
}