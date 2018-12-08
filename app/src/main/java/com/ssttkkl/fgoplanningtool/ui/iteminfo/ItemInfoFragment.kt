package com.ssttkkl.fgoplanningtool.ui.iteminfo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.PopupMenu
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.databinding.FragmentIteminfoBinding
import com.ssttkkl.fgoplanningtool.resources.itemdescriptor.ItemDescriptor
import com.ssttkkl.fgoplanningtool.ui.MainActivity
import com.ssttkkl.fgoplanningtool.ui.requirementlist.RequirementListEntity
import com.ssttkkl.fgoplanningtool.ui.requirementlist.RequirementListFragment

class ItemInfoFragment : Fragment(), RequirementListFragment.OnClickItemListener {
    private lateinit var binding: FragmentIteminfoBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentIteminfoBinding.inflate(inflater, container, false)
        binding.viewModel = ViewModelProviders.of(this)[ItemInfoFragmentViewModel::class.java].apply {
            codename.value = arguments!!.getString(KEY_CODENAME)
        }
        binding.setLifecycleOwner(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as? MainActivity)?.apply {
            drawerState = false
        }
        setHasOptionsMenu(true)

        binding.viewPager.adapter = ItemInfoPagerAdapter(childFragmentManager, this, binding.viewModel!!)
        binding.tabLayout.setupWithViewPager(binding.viewPager)

        binding.viewModel?.apply {
            showWikiMenuEvent.observe(this@ItemInfoFragment, Observer {
                showWikiMenu(it ?: return@Observer)
            })
            gotoWikiEvent.observe(this@ItemInfoFragment, Observer {
                gotoWiki(it ?: return@Observer)
            })
            showServantInfoEvent.observe(this@ItemInfoFragment, Observer {
                showServantInfo(it ?: return@Observer)
            })
            itemDescriptor.observe(this@ItemInfoFragment, Observer {
                onItemDescriptorChanged(it ?: return@Observer)
            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        if (::binding.isInitialized && binding.viewModel?.itemDescriptor?.value?.wikiLinks != null)
            inflater.inflate(R.menu.iteminfo, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.gotoWiki -> binding.viewModel?.onClickGotoWiki()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onClickItem(entity: RequirementListEntity) {
        binding.viewModel?.onClickItem(entity)
    }

    private fun showWikiMenu(wikiTitles: Collection<String>) {
        PopupMenu(context!!, binding.root, GravityCompat.END.or(Gravity.TOP)).apply {
            wikiTitles.forEach {
                menu.add(it)
            }
            setOnMenuItemClickListener {
                binding.viewModel?.onClickWikiMenuItem(it.title.toString()) == true
            }
            show()
        }
    }

    private fun gotoWiki(link: String) {
        startActivity(Intent().apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse(link)
        })
    }

    private fun showServantInfo(servantID: Int) {
        findNavController().navigate(ItemInfoFragmentDirections.actionItemInfoFragmentToServantInfoFragment(servantID))
    }

    private fun onItemDescriptorChanged(itemDescriptor: ItemDescriptor) {
        (activity as? MainActivity)?.apply {
            title = itemDescriptor.localizedName
            invalidateOptionsMenu()
        }
    }

    companion object {
        private const val KEY_CODENAME = "codename"
    }
}