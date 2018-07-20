package com.ssttkkl.fgoplanningtool.ui.ownitemlist

import android.arch.lifecycle.LifecycleOwner
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.data.item.Item
import com.ssttkkl.fgoplanningtool.resources.itemdescriptor.ItemType
import com.ssttkkl.fgoplanningtool.ui.MainActivity
import com.ssttkkl.fgoplanningtool.ui.ownitemlist.edititem.EditItemDialogFragment
import com.ssttkkl.fgoplanningtool.ui.ownitemlist.itemlist.ItemListFragment
import com.ssttkkl.fgoplanningtool.ui.ownitemlist.itemlist.ItemListRecViewAdapter
import kotlinx.android.synthetic.main.fragment_ownitemlist.*

class OwnItemListFragment : Fragment(), LifecycleOwner
        , ItemListRecViewAdapter.Callback
        , EditItemDialogFragment.OnChangeItemActionListener {
    private lateinit var presenter: OwnItemListFragmentPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_ownitemlist, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // setup toolbar
        (activity as? AppCompatActivity)?.setSupportActionBar(toolbar)

        // setup drawer toggle
        (activity as? MainActivity)?.setupDrawerToggle(toolbar)

        viewPager.adapter = object : FragmentPagerAdapter(childFragmentManager) {
            override fun getPageTitle(pos: Int) = ItemType.values()[pos].localizedName
            override fun getItem(pos: Int) = ItemListFragment.getInstance(ItemType.values()[pos])
            override fun getCount() = ItemType.values().size
        }
        tabLayout.setupWithViewPager(viewPager)

        presenter = OwnItemListFragmentPresenter(this)
    }

    override fun onItemEditAction(codename: String) {
        presenter.onItemEditAction(codename)
    }

    override fun onItemInfoAction(codename: String) {
        presenter.onItemInfoAction(codename)
    }

    override fun onChangeItemAction(changedItem: Item) {
        presenter.updateItem(changedItem)
    }

    companion object {
        val tag
            get() = OwnItemListFragment::class.qualifiedName
    }
}