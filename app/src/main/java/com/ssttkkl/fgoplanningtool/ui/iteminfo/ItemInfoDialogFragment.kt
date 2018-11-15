package com.ssttkkl.fgoplanningtool.ui.iteminfo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.appcompat.widget.PopupMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.data.item.Item
import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider
import com.ssttkkl.fgoplanningtool.resources.servant.Servant
import com.ssttkkl.fgoplanningtool.ui.requirementlist.RequirementListEntity
import kotlinx.android.synthetic.main.fragment_iteminfo.*

class ItemInfoDialogFragment : androidx.fragment.app.DialogFragment() {
    private lateinit var codename: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        codename = arguments!![KEY_CODENAME] as String
        setStyle(STYLE_NO_TITLE, R.style.AppTheme_Dialog_NoTitle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_iteminfo, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val descriptor = ResourcesProvider.instance.itemDescriptors[codename]
        name_textView.text = descriptor?.localizedName
        Glide.with(this).load(descriptor?.imgFile).error(R.drawable.item_placeholder).into(imageView)

        val link = descriptor?.wikiLinks?.entries?.firstOrNull()
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
                    descriptor.wikiLinks.forEach { (title, _) ->
                        menu.add(title)
                    }
                    setOnMenuItemClickListener {
                        val link = descriptor.wikiLinks[it.title]
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

        viewPager.adapter = object : androidx.fragment.app.FragmentPagerAdapter(childFragmentManager) {
            val ascensionItemEntities = generateEntities { it.ascensionItems }
            val skillItemEntities = generateEntities { it.skillItems }
            val dressItemEntities = generateEntities { it.dress.map { it.items } }

            val pairs = listOf(Pair(getString(R.string.ascension_iteminfo), ascensionItemEntities),
                    Pair(getString(R.string.skill_iteminfo), skillItemEntities),
                    Pair(getString(R.string.dress_iteminfo), dressItemEntities)).filter { it.second.isNotEmpty() }

            override fun getCount(): Int = pairs.size
            override fun getItem(pos: Int): androidx.fragment.app.Fragment = RequirementListFragment()

            override fun getPageTitle(pos: Int): CharSequence = pairs[pos].first

            override fun instantiateItem(container: ViewGroup, pos: Int): Any {
                return (super.instantiateItem(container, pos) as RequirementListFragment).apply {
                    data = pairs[pos].second
                }
            }
        }
        tabLayout.setupWithViewPager(viewPager)
    }

    private fun generateEntities(getItems: (Servant) -> List<Collection<Item>>): List<RequirementListEntity> {
        val list = ArrayList<RequirementListEntity>()
        ResourcesProvider.instance.servants.values.sortedBy { it.id }.forEach { servant ->
            var requirement = 0L
            getItems.invoke(servant).forEach { cur ->
                cur.forEach { item ->
                    if (item.codename == codename)
                        requirement += item.count
                }
            }
            if (requirement > 0)
                list.add(RequirementListEntity(servantID = servant.id,
                        name = servant.localizedName,
                        requirement = requirement,
                        avatarFile = servant.avatarFile))
        }
        return list
    }

    companion object {
        fun newInstance(codename: String) = ItemInfoDialogFragment().apply {
            arguments = Bundle().apply {
                putString(KEY_CODENAME, codename)
            }
        }

        private const val KEY_CODENAME = "codename"
    }
}