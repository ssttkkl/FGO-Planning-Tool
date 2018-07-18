package com.ssttkkl.fgoplanningtool.ui.servantinfo

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.data.item.Item
import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider
import com.ssttkkl.fgoplanningtool.resources.servant.Servant
import kotlinx.android.synthetic.main.fragment_servantinfo.*

class ServantInfoDialogFragment : DialogFragment() {
    var servant: Servant? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.AppTheme_Dialog_NoTitle)
        val servantID = arguments!![KEY_SERVANT_ID] as Int
        servant = ResourcesProvider.instance.servants[servantID]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_servantinfo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        name_textView.text = servant?.localizedName
        Glide.with(this).load(servant?.avatarFile).error(R.drawable.avatar_placeholder).into(avatar_imageView)

        viewPager.adapter = object : FragmentPagerAdapter(childFragmentManager) {
            override fun getCount(): Int = 2
            override fun getItem(position: Int): Fragment = ServantInfoLevelListFragment()

            override fun getPageTitle(pos: Int): CharSequence = if (pos == 0)
                getString(R.string.ascension_iteminfo)
            else
                getString(R.string.skill_iteminfo)

            override fun instantiateItem(container: ViewGroup, pos: Int): Any {
                return (super.instantiateItem(container, pos) as ServantInfoLevelListFragment).apply {
                    data = if (pos == 0)
                        generateEntities({ it.toString() },
                                { (it + 1).toString() },
                                { it?.ascensionItems ?: listOf() },
                                { it?.ascensionQP ?: listOf() })
                    else
                        generateEntities({ (it + 1).toString() },
                                { (it + 2).toString() },
                                { it?.skillItems ?: listOf() },
                                { it?.skillQP ?: listOf() })
                }
            }
        }
        tabLayout.setupWithViewPager(viewPager)
    }

    private fun generateEntities(getStart: (Int) -> String,
                                 getTo: (Int) -> String,
                                 getItems: (Servant?) -> List<Collection<Item>>,
                                 getQP: (Servant?) -> List<Long>): List<ServantInfoLevelListEntity> {
        val qp = getQP(servant)
        val items = getItems(servant)
        return items.mapIndexed { idx, it -> it + Item("qp", qp[idx]) }
                .mapIndexed { idx, it ->
                    ServantInfoLevelListEntity(start = getStart.invoke(idx),
                            to = getTo.invoke(idx),
                            items = it.groupBy { it.descriptor?.type }.toList().sortedBy { it.first }
                                    .flatMap { it.second.sortedBy { ResourcesProvider.instance.itemRank[it.codename] } })
                }
    }

    companion object {
        fun newInstance(servantID: Int) = ServantInfoDialogFragment().apply {
            arguments = Bundle().apply {
                putInt(KEY_SERVANT_ID, servantID)
            }
        }

        private const val KEY_SERVANT_ID = "servant_id"
    }
}