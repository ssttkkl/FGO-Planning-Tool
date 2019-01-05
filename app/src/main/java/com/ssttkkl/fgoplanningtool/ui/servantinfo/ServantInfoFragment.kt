package com.ssttkkl.fgoplanningtool.ui.servantinfo

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
import com.ssttkkl.fgoplanningtool.databinding.FragmentServantinfoBinding
import com.ssttkkl.fgoplanningtool.resources.servant.Servant
import com.ssttkkl.fgoplanningtool.ui.MainActivity

class ServantInfoFragment : Fragment(), ServantInfoLevelListFragment.OnClickItemListener {
    private lateinit var binding: FragmentServantinfoBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentServantinfoBinding.inflate(inflater, container, false)
        binding.viewModel = ViewModelProviders.of(this)[ServantInfoFragmentViewModel::class.java].apply {
            servantID.value = arguments!!.getInt(KEY_SERVANT_ID)
        }
        binding.setLifecycleOwner(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as? MainActivity)?.apply {
            drawerState = false
        }
        setHasOptionsMenu(true)

        binding.viewPager.adapter = ServantInfoPagerAdapter(childFragmentManager, this, binding.viewModel!!)
        binding.tabLayout.setupWithViewPager(binding.viewPager)

        binding.viewModel?.apply {
            showWikiMenuEvent.observe(this@ServantInfoFragment, Observer {
                showWikiMenu(it ?: return@Observer)
            })
            gotoWikiEvent.observe(this@ServantInfoFragment, Observer {
                gotoWiki(it ?: return@Observer)
            })
            showItemInfoEvent.observe(this@ServantInfoFragment, Observer {
                showItemInfo(it ?: return@Observer)
            })
            servant.observe(this@ServantInfoFragment, Observer {
                onServantChanged(it ?: return@Observer)
            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        if (::binding.isInitialized && binding.viewModel?.servant?.value?.wikiLinks != null)
            inflater.inflate(R.menu.iteminfo, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.gotoWiki -> binding.viewModel?.onClickGotoWiki()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onClickItem(codename: String) {
        binding.viewModel?.onClickItem(codename)
    }

    private fun showWikiMenu(wikiTitles: Collection<String>) {
        PopupMenu(context!!, activity!!.findViewById(R.id.toolbar), GravityCompat.END.or(Gravity.TOP)).apply {
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

    private fun showItemInfo(codename: String) {
        findNavController().navigate(ServantInfoFragmentDirections.actionServantInfoFragmentToItemInfoFragment(codename))
    }

    private fun onServantChanged(servant: Servant) {
        (activity as? MainActivity)?.apply {
            title = servant.localizedName
            invalidateOptionsMenu()
        }
    }

    companion object {
        const val KEY_SERVANT_ID = "servantID"
    }
}