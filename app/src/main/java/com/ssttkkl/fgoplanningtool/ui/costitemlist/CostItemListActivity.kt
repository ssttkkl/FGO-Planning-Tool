package com.ssttkkl.fgoplanningtool.ui.costitemlist

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.data.item.costItems
import com.ssttkkl.fgoplanningtool.data.plan.Plan
import com.ssttkkl.fgoplanningtool.ui.utils.setStatusBarColor
import kotlinx.android.synthetic.main.activity_costitemlist.*

class CostItemListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarColor()
        setContentView(R.layout.activity_costitemlist)

        // setup the Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // set the DataSet to CostItemListFragment
        (supportFragmentManager.findFragmentById(R.id.itemListFragment) as CostItemListFragment).data =
                intent.getParcelableArrayExtra(ARG_PLANS).map { it as Plan }.costItems
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> finish()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    companion object {
        const val ARG_PLANS = "plans"
    }
}
