package sontdhust.cointrack.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ListView
import sontdhust.cointrack.R
import sontdhust.cointrack.component.CoinsAdapter
import sontdhust.cointrack.helper.RetrieveDataTask
import java.util.*
import kotlin.concurrent.fixedRateTimer

class CoinsListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coins_list)
        createViews()
    }

    /*
     * Helpers
     */

    private fun createViews() {
        val coinsListView = findViewById(R.id.coins_list_view) as ListView
        val adapter = CoinsAdapter(this, ArrayList())
        coinsListView.adapter = adapter
        fixedRateTimer(period = 5 * 1000, action = {
            RetrieveDataTask(fun(result) {
                adapter.clear()
                adapter.addAll(result)
                adapter.notifyDataSetChanged()
            }).execute()
        })
    }
}
