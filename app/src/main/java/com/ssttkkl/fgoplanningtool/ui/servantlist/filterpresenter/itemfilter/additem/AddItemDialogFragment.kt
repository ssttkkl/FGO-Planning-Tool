package com.ssttkkl.fgoplanningtool.ui.servantlist.filterpresenter.itemfilter.additem

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentPagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.resources.itemdescriptor.ItemType
import com.ssttkkl.fgoplanningtool.ui.utils.NoInterfaceImplException
import kotlinx.android.synthetic.main.fragment_servantlist_additem.*

class AddItemDialogFragment : DialogFragment(), ItemListFragment.OnItemClickListener {
    interface OnAddItemActionListener {
        fun onAddItemAction(codename: String)
    }

    private var listener: OnAddItemActionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.AppTheme_Dialog_NoTitle)
        listener = when {
            parentFragment is OnAddItemActionListener -> parentFragment as OnAddItemActionListener
            activity is OnAddItemActionListener -> activity as OnAddItemActionListener
            else -> throw NoInterfaceImplException(OnAddItemActionListener::class)
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_servantlist_additem, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val types = ItemType.values().filter { it != ItemType.General }
        viewPager.apply {
            adapter = object : FragmentPagerAdapter(childFragmentManager) {
                override fun getItem(pos: Int) = ItemListFragment.newInstance(types[pos])
                override fun getCount() = types.size
                override fun getPageTitle(pos: Int) = types[pos].localizedName
            }
        }
        tabLayout.setupWithViewPager(viewPager)
    }

    override fun onItemClick(codename: String) {
        listener?.onAddItemAction(codename)
        dismiss()
    }

    companion object {
        val tag
            get() = AddItemDialogFragment::class.qualifiedName
    }
}