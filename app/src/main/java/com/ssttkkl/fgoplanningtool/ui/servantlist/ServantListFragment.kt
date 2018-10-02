package com.ssttkkl.fgoplanningtool.ui.servantlist

import android.arch.lifecycle.LifecycleOwner
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.*
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.ssttkkl.fgoplanningtool.PreferenceKeys
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.resources.servant.Servant
import com.ssttkkl.fgoplanningtool.ui.MainActivity
import com.ssttkkl.fgoplanningtool.ui.servantfilter.ServantFilterFragment
import com.ssttkkl.fgoplanningtool.ui.utils.BackHandlerFragment
import com.ssttkkl.fgoplanningtool.ui.utils.CommonRecViewItemDecoration
import com.ssttkkl.fgoplanningtool.ui.utils.NoInterfaceImplException
import kotlinx.android.synthetic.main.fragment_servantlist.*

class ServantListFragment : BackHandlerFragment(), ServantFilterFragment.OnFilterListener, LifecycleOwner {
    interface OnServantSelectedListener {
        fun onServantSelected(servantId: Int)
    }

    private var listener: OnServantSelectedListener? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = when {
            parentFragment is OnServantSelectedListener -> parentFragment as OnServantSelectedListener
            activity is OnServantSelectedListener -> activity as OnServantSelectedListener
            else -> throw NoInterfaceImplException(OnServantSelectedListener::class)
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    private lateinit var hiddenServantIDs: Set<Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hiddenServantIDs = arguments?.getIntArray(KEY_HIDDEN)?.toHashSet() ?: setOf()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_servantlist, container, false)

    private lateinit var itemDecoration: CommonRecViewItemDecoration

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Setup the Toolbar
        (activity as? AppCompatActivity)?.apply {
            setHasOptionsMenu(true)
            setSupportActionBar(toolbar)
            supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(true)
                setDisplayShowTitleEnabled(false)
            }
        }

        (activity as? MainActivity)?.setupDrawerToggle(toolbar)

        // Setup the ServantFilterFragment
        if (childFragmentManager.findFragmentByTag(ServantFilterFragment::class.qualifiedName) == null) {
            childFragmentManager.beginTransaction()
                    .replace(R.id.frameLayout, ServantFilterFragment(), ServantFilterFragment::class.qualifiedName)
                    .commit()
        }

        // Setup the Servant RecView
        recView.apply {
            adapter = ServantListAdapter(context!!, hiddenServantIDs).apply {
                setOnItemClickListener { onServantSelected(it) }
                data = (childFragmentManager.findFragmentByTag(ServantFilterFragment::class.qualifiedName)
                        as? ServantFilterFragment)?.filtered ?: listOf()
            }
            setHasFixedSize(true)
        }
        itemDecoration = CommonRecViewItemDecoration(context!!)

        when (viewType) {
            ViewType.List -> onSwitchToListView()
            ViewType.Grid -> onSwitchToGridView()
        }

        // Setup the SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean = false
            override fun onQueryTextChange(newText: String): Boolean {
                (childFragmentManager.findFragmentByTag(ServantFilterFragment::class.qualifiedName)
                        as? ServantFilterFragment)?.nameFilter = newText
                return true
            }
        })
    }

    override fun onFilter(filtered: List<Servant>) {
        (recView.adapter as ServantListAdapter).data = filtered
    }

    private var viewType: ViewType
        get() = ViewType.valueOf(activity?.getPreferences(Context.MODE_PRIVATE)
                ?.getString(PreferenceKeys.KEY_SERVANT_LIST_VIEW_TYPE, ViewType.List.name)
                ?: ViewType.List.name)
        set(value) {
            when (value) {
                ViewType.List -> onSwitchToListView()
                ViewType.Grid -> onSwitchToGridView()
            }
            activity?.getPreferences(Context.MODE_PRIVATE)?.edit()
                    ?.putString(PreferenceKeys.KEY_SERVANT_LIST_VIEW_TYPE, value.name)
                    ?.apply()
            activity?.invalidateOptionsMenu()
        }

    private fun onSwitchToGridView() {
        recView?.apply {
            layoutManager = FlexboxLayoutManager(activity).apply {
                flexWrap = FlexWrap.WRAP
                justifyContent = JustifyContent.SPACE_AROUND
                removeItemDecoration(itemDecoration)
            }
            (adapter as? ServantListAdapter)?.viewType = ViewType.Grid
        }
    }

    private fun onSwitchToListView() {
        recView?.apply {
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            (adapter as? ServantListAdapter)?.viewType = ViewType.List
            addItemDecoration(itemDecoration)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.servantlist, menu)
        when (viewType) {
            ViewType.List -> menu?.findItem(R.id.switchToListView_action)?.isVisible = false
            ViewType.Grid -> menu?.findItem(R.id.switchToGridView_action)?.isVisible = false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> activity?.finish()
            R.id.sortAndFilter_action -> drawerlayout.openDrawer(Gravity.END)
            R.id.switchToGridView_action -> viewType = ViewType.Grid
            R.id.switchToListView_action -> viewType = ViewType.List
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onBackPressed(): Boolean {
        return if (drawerlayout.isDrawerOpen(Gravity.END)) {
            drawerlayout.closeDrawer(Gravity.END)
            true
        } else super.onBackPressed()
    }

    private fun onServantSelected(servantId: Int) {
        listener?.onServantSelected(servantId)
    }

    companion object {
        fun newInstance(hiddenServantIDs: Set<Int>) = ServantListFragment().apply {
            arguments = Bundle().apply {
                putIntArray(KEY_HIDDEN, hiddenServantIDs.toIntArray())
            }
        }

        private const val KEY_HIDDEN = "hidden"
    }
}