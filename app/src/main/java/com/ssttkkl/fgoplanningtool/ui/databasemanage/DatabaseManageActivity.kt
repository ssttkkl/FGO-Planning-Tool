package com.ssttkkl.fgoplanningtool.ui.databasemanage

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.data.databasedescriptor.DatabaseDescriptor
import com.ssttkkl.fgoplanningtool.ui.utils.CommonRecViewItemDecoration
import com.ssttkkl.fgoplanningtool.ui.utils.setStatusBarColor
import kotlinx.android.synthetic.main.activity_databasemanage.*

class DatabaseManageActivity : AppCompatActivity() {
    var data: List<DatabaseDescriptor>
        get() = (recView?.adapter as? DatabaseManageRecViewAdapter)?.data ?: listOf()
        set(value) {
            (recView?.adapter as? DatabaseManageRecViewAdapter)?.setNewData(value)
            recView?.invalidateItemDecorations()
        }

    private lateinit var presenter: DatabaseManageActivityPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarColor()
        setContentView(R.layout.activity_databasemanage)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recView.apply {
            adapter = DatabaseManageRecViewAdapter(this@DatabaseManageActivity).apply {
                // restore editing states
                if (savedInstanceState != null) {
                    if (savedInstanceState.containsKey(KEY_DATA))
                        setNewData(savedInstanceState.getParcelableArray(KEY_DATA).map { it as DatabaseDescriptor })

                    if (savedInstanceState.containsKey(KEY_EDITING_POSITIONS) && savedInstanceState.containsKey(KEY_EDITING_CONTENTS)) {
                        val editingPositions = savedInstanceState.getIntArray(KEY_EDITING_POSITIONS)
                        val editingContents = savedInstanceState.getStringArray(KEY_EDITING_CONTENTS)
                        if (editingPositions.size == editingContents.size) {
                            editingPositions.indices.forEach { idx ->
                                setPositionInEditMode(editingPositions[idx], editingContents[idx])
                            }
                        }
                    }
                }
                layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this@DatabaseManageActivity, androidx.recyclerview.widget.LinearLayoutManager.VERTICAL, false)
                addItemDecoration(CommonRecViewItemDecoration(context))
            }
        }

        presenter = DatabaseManageActivityPresenter(this).also {
            (recView.adapter as DatabaseManageRecViewAdapter).setCallback(it)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putParcelableArray(KEY_DATA, data.toTypedArray())
        (recView?.adapter as? DatabaseManageRecViewAdapter)?.apply {
            val inEditModePositions = inEditModePositions.filter { it.value }.keys
            val pairs = inEditModePositions.map { Pair(it, editedNames[it] ?: data[it].name) }
            if (outState != null && pairs.isNotEmpty()) {
                outState.putIntArray(KEY_EDITING_POSITIONS, pairs.map { it.first }.toIntArray())
                outState.putStringArray(KEY_EDITING_CONTENTS, pairs.map { it.second }.toTypedArray())
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.databasemanage, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> finish()
            R.id.add_action -> presenter.onAddAction()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK)
            presenter.onActivityResultOK(requestCode, data)
    }

    fun showMessage(message: String) {
        Snackbar.make(root, message, Snackbar.LENGTH_SHORT).show()
    }

    companion object {
        private const val KEY_DATA = "data"
        private const val KEY_EDITING_POSITIONS = "editing_positions"
        private const val KEY_EDITING_CONTENTS = "editing_contents"
    }
}
