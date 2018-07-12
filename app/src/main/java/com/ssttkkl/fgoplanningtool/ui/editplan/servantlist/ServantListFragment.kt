package com.ssttkkl.fgoplanningtool.ui.editplan.servantlist

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.*
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.ui.editplan.servantlist.filterpresenter.ClassFilterPresenter
import com.ssttkkl.fgoplanningtool.ui.editplan.servantlist.filterpresenter.OrderFilterPresenter
import com.ssttkkl.fgoplanningtool.ui.editplan.servantlist.filterpresenter.StarFilterPresenter
import com.ssttkkl.fgoplanningtool.ui.editplan.servantlist.filterpresenter.itemfilter.ItemFilterPresenter
import com.ssttkkl.fgoplanningtool.ui.editplan.servantlist.filterpresenter.itemfilter.additem.AddItemDialogFragment
import com.ssttkkl.fgoplanningtool.ui.utils.BackHandlerFragment
import com.ssttkkl.fgoplanningtool.ui.utils.CommonRecViewItemDecoration
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
            else -> throw Exception("Either parent fragment or activity must impl OnServantSelectedListener interface.")
        }
        viewModel = ViewModelProviders.of(this).get(ServantListViewModel::class.java)
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    private lateinit var orderFilterPresenter: OrderFilterPresenter
    private lateinit var starFilterPresenter: StarFilterPresenter
    private lateinit var classFilterPresenter: ClassFilterPresenter
    private lateinit var itemFilterPresenter: ItemFilterPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_servantlist, container, false)

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

        // Setup the Servant RecView
        recView.apply {
            adapter = ServantListAdapter(context!!).apply {
                setOnItemClickListener { onServantSelected(it) }
            }
            layoutManager = LinearLayoutManager(activity!!, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(CommonRecViewItemDecoration(context!!))
            setHasFixedSize(true)
        }
        viewModel.data.observe(this, Observer {
            (recView.adapter as ServantListAdapter).data = it ?: listOf()
        })

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
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.servantlist, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> activity?.finish()
            R.id.sortAndFilter_action -> drawerlayout.openDrawer(Gravity.END)
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

    companion object {
        val tag
            get() = ServantListFragment::class.qualifiedName
    }
}