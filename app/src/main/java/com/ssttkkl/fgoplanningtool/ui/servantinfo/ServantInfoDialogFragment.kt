package com.ssttkkl.fgoplanningtool.ui.servantinfo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.widget.PopupMenu
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

        val link = servant?.wikiLinks?.entries?.firstOrNull()
        if (link != null) {
            gotoWiki_button.text = getString(R.string.gotoWiki_servantinfo, link.key)
            gotoWiki_button.setOnClickListener {
                startActivity(Intent().apply {
                    action = Intent.ACTION_VIEW
                    data = Uri.parse(link.value)
                })
            }

            more_button.setOnClickListener {
                PopupMenu(context!!, it).apply {
                    servant?.wikiLinks?.forEach { (title, _) ->
                        menu.add(title)
                    }
                    setOnMenuItemClickListener {
                        val link = servant?.wikiLinks?.get(it.title)
                        if (link != null) {
                            startActivity(Intent().apply {
                                action = Intent.ACTION_VIEW
                                data = Uri.parse(link)
                            })
                        }
                        true
                    }
                    show()
                }
            }
        } else {
            gotoWiki_button.visibility = View.GONE
            more_button.visibility = View.GONE
        }

        viewPager.adapter = object : FragmentPagerAdapter(childFragmentManager) {
            val ascensionItemEntities = generateEntities({ _, cur -> cur.toString() },
                    { _, cur -> (cur + 1).toString() },
                    { it.ascensionItems.plus(it.dress.map { it.items }) },
                    { it.ascensionQP.plus(it.dress.map { it.qp }) },
                    true)

            val skillItemEntities = generateEntities({ _, cur -> (cur + 1).toString() },
                    { _, cur -> (cur + 2).toString() },
                    { it.skillItems },
                    { it.skillQP },
                    true)

            val dressItemEntities = generateEntities({ servant, cur -> servant.dress[cur].localizedName },
                    { _, _ -> "" },
                    { it.dress.map { it.items } },
                    { it.dress.map { it.qp } },
                    false)

            val pairs = listOf(Pair(getString(R.string.ascension_iteminfo), ascensionItemEntities),
                    Pair(getString(R.string.skill_iteminfo), skillItemEntities),
                    Pair(getString(R.string.dress_iteminfo), dressItemEntities)).filter { it.second.isNotEmpty() }

            override fun getCount(): Int = pairs.size
            override fun getItem(pos: Int): Fragment = ServantInfoLevelListFragment()

            override fun getPageTitle(pos: Int): CharSequence = pairs[pos].first

            override fun instantiateItem(container: ViewGroup, pos: Int): Any {
                return (super.instantiateItem(container, pos) as ServantInfoLevelListFragment).apply {
                    data = pairs[pos].second
                }
            }
        }
        tabLayout.setupWithViewPager(viewPager)
    }

    private fun generateEntities(getStart: (Servant, Int) -> String,
                                 getTo: (Servant, Int) -> String,
                                 getItems: (Servant) -> List<Collection<Item>>,
                                 getQP: (Servant) -> List<Long>,
                                 isArrowVisible: Boolean): List<ServantInfoLevelListEntity> {
        servant?.also { servant ->
            val qp = getQP(servant)
            val items = getItems(servant)
            return items.mapIndexed { idx, it -> it + Item("qp", qp[idx]) }
                    .mapIndexed { idx, it ->
                        ServantInfoLevelListEntity(start = getStart.invoke(servant, idx),
                                to = getTo.invoke(servant, idx),
                                isHorizontalArrowVisible = isArrowVisible,
                                items = it.groupBy { it.descriptor?.type }.toList().sortedBy { it.first }
                                        .flatMap { it.second.sortedBy { ResourcesProvider.instance.itemRank[it.codename] } })
                    }
        }
        return listOf()
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