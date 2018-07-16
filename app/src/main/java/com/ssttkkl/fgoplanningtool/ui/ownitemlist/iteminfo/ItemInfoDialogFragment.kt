package com.ssttkkl.fgoplanningtool.ui.ownitemlist.iteminfo

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.data.item.Item
import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider
import com.ssttkkl.fgoplanningtool.resources.servant.Servant
import com.ssttkkl.fgoplanningtool.ui.costitemlist.requirementlist.RequirementListEntity
import kotlinx.android.synthetic.main.fragment_iteminfo.*

class ItemInfoDialogFragment : DialogFragment() {
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

        viewPager.adapter = object : FragmentPagerAdapter(childFragmentManager) {
            override fun getCount(): Int = 2
            override fun getItem(position: Int): Fragment = RequirementListFragment()

            override fun getPageTitle(pos: Int): CharSequence = if (pos == 0)
                getString(R.string.ascension_iteminfo)
            else
                getString(R.string.skill_iteminfo)

            override fun instantiateItem(container: ViewGroup, pos: Int): Any {
                return (super.instantiateItem(container, pos) as RequirementListFragment).apply {
                    data = if (pos == 0)
                        generateRequirementEntities { it.ascensionItems }
                    else
                        generateRequirementEntities { it.skillItems }
                }
            }
        }
        tabLayout.setupWithViewPager(viewPager)
    }

    private fun generateRequirementEntities(getItems: (Servant) -> List<Collection<Item>>): List<RequirementListEntity> {
        val list = ArrayList<RequirementListEntity>()
        ResourcesProvider.instance.servants.values.sortedBy { it.id }.forEach { servant ->
            val requirement = getItems.invoke(servant).sumBy { cur ->
                cur.sumBy { item ->
                    if (item.codename == codename)
                        item.count
                    else
                        0
                }
            }
            if (requirement > 0)
                list.add(RequirementListEntity(name = servant.localizedName,
                        require = requirement,
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