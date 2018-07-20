package com.ssttkkl.fgoplanningtool.ui.servantlist

import android.app.Activity
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.*
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.ssttkkl.fgoplanningtool.PreferenceKeys
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.ui.MainActivity
import com.ssttkkl.fgoplanningtool.ui.servantlist.filterpresenter.ClassFilterPresenter
import com.ssttkkl.fgoplanningtool.ui.servantlist.filterpresenter.OrderFilterPresenter
import com.ssttkkl.fgoplanningtool.ui.servantlist.filterpresenter.StarFilterPresenter
import com.ssttkkl.fgoplanningtool.ui.servantlist.filterpresenter.WayToGetFilterPresenter
import com.ssttkkl.fgoplanningtool.ui.servantlist.filterpresenter.itemfilter.ItemFilterPresenter
import com.ssttkkl.fgoplanningtool.ui.servantlist.filterpresenter.itemfilter.additem.AddItemDialogFragment
import com.ssttkkl.fgoplanningtool.ui.utils.BackHandlerFragment
import com.ssttkkl.fgoplanningtool.ui.utils.CommonRecViewItemDecoration
import com.ssttkkl.fgoplanningtool.ui.utils.NoInterfaceImplException
import kotlinx.android.synthetic.main.fragment_servantlist.*

class ServantListFragment : BackHandlerFragment(),
        LifecycleOwner,
        AddItemDialogFragment.OnAddItemActionListener {
    interface OnServantSelectedListener {
        fun onServantSelected(servantId: Int)
    }

    private var listener: OnServantSelectedListener? = null

    private lateinit var viewModel: ServantListViewModel

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = when {
            parentFragment is OnServantSelectedListener -> parentFragment as OnServantSelectedListener
            activity is OnServantSelectedListener -> activity as OnServantSelectedListener
            else -> throw NoInterfaceImplException(OnServantSelectedListener::class)
        }
        viewModel = ViewModelProviders.of(this).get(ServantListViewModel::class.java)
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

    private lateinit var orderFilterPresenter: OrderFilterPresenter
    private lateinit var starFilterPresenter: StarFilterPresenter
    private lateinit var classFilterPresenter: ClassFilterPresenter
    private lateinit var wayToGetFilterPresenter: WayToGetFilterPresenter
    private lateinit var itemFilterPresenter: ItemFilterPresenter

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

        // Setup the Servant RecView
        recView.apply {
            adapter = ServantListAdapter(context!!, hiddenServantIDs).apply {
                setOnItemClickListener { onServantSelected(it) }
            }
            setHasFixedSize(true)
        }
        itemDecoration = CommonRecViewItemDecoration(context!!)
        viewModel.data.observe(this, Observer {
            (recView.adapter as ServantListAdapter).data = it ?: listOf()
        })

        viewModel.viewType = ViewType.valueOf(PreferenceManager.getDefaultSharedPreferences(context)
                ?.getString(PreferenceKeys.KEY_SERVANT_LIST_VIEW_TYPE, ViewType.List.name)
                ?: ViewType.List.name)

        if (viewModel.viewType == ViewType.List)
            onSwitchToListView()
        else
            onSwitchToGridView()

        // Setup the SearchView
        searchView.apply {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean = false
                override fun onQueryTextChange(newText: String): Boolean {
                    viewModel.searchText = query
                    return true
                }
            })
            setQuery(viewModel.searchText, true)
        }

        orderFilterPresenter = OrderFilterPresenter(this)
        starFilterPresenter = StarFilterPresenter(this)
        classFilterPresenter = ClassFilterPresenter(this)
        itemFilterPresenter = ItemFilterPresenter(this)
        wayToGetFilterPresenter = WayToGetFilterPresenter(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.servantlist, menu)
        if (viewModel.viewType == ViewType.List)
            menu?.findItem(R.id.switchToListView_action)?.isVisible = false
        else
            menu?.findItem(R.id.switchToGridView_action)?.isVisible = false
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> activity?.finish()
            R.id.sortAndFilter_action -> drawerlayout.openDrawer(Gravity.END)
            R.id.switchToGridView_action -> onSwitchToGridView()
            R.id.switchToListView_action -> onSwitchToListView()
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

    override fun onAddItemAction(codename: String) {
        itemFilterPresenter.onAddItemAction(codename)
    }

    private fun onServantSelected(servantId: Int) {
        listener?.onServantSelected(servantId)
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
        viewModel.viewType = ViewType.Grid
        activity?.invalidateOptionsMenu()

        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(PreferenceKeys.KEY_SERVANT_LIST_VIEW_TYPE, ViewType.Grid.name)
                .apply()
    }

    private fun onSwitchToListView() {
        recView?.apply {
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            (adapter as? ServantListAdapter)?.viewType = ViewType.List
            addItemDecoration(itemDecoration)
        }
        viewModel.viewType = ViewType.List
        activity?.invalidateOptionsMenu()

        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(PreferenceKeys.KEY_SERVANT_LIST_VIEW_TYPE, ViewType.List.name)
                .apply()
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