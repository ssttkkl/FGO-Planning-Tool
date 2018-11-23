package com.ssttkkl.fgoplanningtool.ui.planlist.costitemlist

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.data.plan.Plan
import com.ssttkkl.fgoplanningtool.ui.utils.replaceFragment
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
        replaceFragment(R.id.frameLayout,
                CostItemListFragment.newInstanceWithPlans(intent.getParcelableArrayExtra(ARG_PLANS).mapNotNull { it as? Plan }),
                CostItemListFragment::class.qualifiedName)
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
