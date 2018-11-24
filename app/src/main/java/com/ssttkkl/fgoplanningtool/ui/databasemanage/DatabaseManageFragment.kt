package com.ssttkkl.fgoplanningtool.ui.databasemanage

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.databinding.FragmentDatabasemanageBinding
import com.ssttkkl.fgoplanningtool.ui.MainActivity
import com.ssttkkl.fgoplanningtool.ui.utils.CommonRecViewItemDecoration

class DatabaseManageFragment : Fragment() {
    private lateinit var binding: FragmentDatabasemanageBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentDatabasemanageBinding.inflate(inflater, container, false)
        binding.setLifecycleOwner(this)
        binding.viewModel = ViewModelProviders.of(this)[DatabaseManageFragmentViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as? MainActivity)?.apply {
            setSupportActionBar(binding.toolbar)
            setupDrawerToggle(binding.toolbar)
        }
        setHasOptionsMenu(true)

        binding.viewModel?.apply {
            gotoCreateJsonUIEvent.observe(this@DatabaseManageFragment, Observer {
                if (it != null)
                    gotoCreateJsonUi(it.first, it.second)
            })
            gotoOpenJsonUIEvent.observe(this@DatabaseManageFragment, Observer {
                if (it != null)
                    gotoOpenJsonUi(it.first, it.second)
            })
            showMessageEvent.observe(this@DatabaseManageFragment, Observer {
                showMessage(it ?: "")
            })
        }

        binding.recView.apply {
            adapter = DatabaseManageRecViewAdapter(context!!, this@DatabaseManageFragment, binding.viewModel!!)
            layoutManager = LinearLayoutManager(context!!, RecyclerView.VERTICAL, false)
            addItemDecoration(CommonRecViewItemDecoration(context!!))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.databasemanage, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_action -> binding.viewModel?.onClickAdd()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun gotoCreateJsonUi(filename: String, requestCode: Int) {
        startActivityForResult(Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            type = DatabaseManageFragmentViewModel.TYPE_JSON
            addCategory(Intent.CATEGORY_OPENABLE)
            putExtra(Intent.EXTRA_TITLE, filename)
        }, requestCode)
    }

    private fun gotoOpenJsonUi(filename: String, requestCode: Int) {
        startActivityForResult(Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = DatabaseManageFragmentViewModel.TYPE_JSON
            addCategory(Intent.CATEGORY_OPENABLE)
            putExtra(Intent.EXTRA_TITLE, filename)
        }, requestCode)
    }

    private fun showMessage(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (binding.viewModel?.onActivityResult(requestCode, resultCode, data) != true)
            super.onActivityResult(requestCode, resultCode, data)
    }
}