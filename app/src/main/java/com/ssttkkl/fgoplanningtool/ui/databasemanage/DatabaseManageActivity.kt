package com.ssttkkl.fgoplanningtool.ui.databasemanage

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.ui.utils.setStatusBarColor
import kotlinx.android.synthetic.main.activity_databasemanage.*

class DatabaseManageActivity : AppCompatActivity() {
    private lateinit var presenter: DatabaseManageActivityPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarColor()
        setContentView(R.layout.activity_databasemanage)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recView.apply {
            adapter = DatabaseManageRecViewAdapter(this@DatabaseManageActivity)
            layoutManager = LinearLayoutManager(this@DatabaseManageActivity, LinearLayoutManager.VERTICAL, false)
        }

        presenter = DatabaseManageActivityPresenter(this).also {
            (recView.adapter as DatabaseManageRecViewAdapter).setCallback(it)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
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
}
