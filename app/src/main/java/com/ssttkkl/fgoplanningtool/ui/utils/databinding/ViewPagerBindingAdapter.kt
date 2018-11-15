package com.ssttkkl.fgoplanningtool.ui.utils.databinding

import androidx.databinding.BindingAdapter
import androidx.viewpager.widget.ViewPager

interface TitlesHolder {
    var titles: List<String>
}

object ViewPagerBindingAdapter {
    @BindingAdapter("titles")
    @JvmStatic
    fun setTitles(pager: androidx.viewpager.widget.ViewPager, titles: List<String>) {
        (pager.adapter as TitlesHolder).titles = titles
    }

    @BindingAdapter("titles")
    @JvmStatic
    fun setTitles(pager: androidx.viewpager.widget.ViewPager, titles: Array<String>) {
        (pager.adapter as TitlesHolder).titles = titles.toList()
    }
}