package com.ssttkkl.fgoplanningtool.ui.servantfilter.additem

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentPagerAdapter
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.databinding.FragmentServantfilterAdditemBinding
import com.ssttkkl.fgoplanningtool.resources.itemdescriptor.ItemType
import com.ssttkkl.fgoplanningtool.ui.utils.NoInterfaceImplException

class AddItemDialogFragment : DialogFragment(), ItemListFragment.OnItemClickListener {
    interface OnAddItemListener {
        fun onAddItem(codename: String)
    }

    private var listener: OnAddItemListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.AppTheme_Dialog_NoTitle)
        listener = when {
            parentFragment is OnAddItemListener -> parentFragment as OnAddItemListener
            activity is OnAddItemListener -> activity as OnAddItemListener
            else -> throw NoInterfaceImplException(OnAddItemListener::class)
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    private lateinit var binding: FragmentServantfilterAdditemBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentServantfilterAdditemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val types = ItemType.values().filter { it != ItemType.General }
        binding.viewPager.adapter = object : FragmentPagerAdapter(childFragmentManager) {
            override fun getItem(pos: Int) = ItemListFragment.newInstance(types[pos])
            override fun getCount() = types.size
            override fun getPageTitle(pos: Int) = types[pos].localizedName
        }
        binding.tabLayout.setupWithViewPager(binding.viewPager)
    }

    override fun onItemClick(codename: String) {
        listener?.onAddItem(codename)
        dismiss()
    }
}