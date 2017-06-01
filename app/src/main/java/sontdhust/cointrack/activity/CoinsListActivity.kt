package sontdhust.cointrack.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ListView
import sontdhust.cointrack.R
import sontdhust.cointrack.component.CoinsAdapter
import sontdhust.cointrack.helper.RetrieveDataTask

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
        RetrieveDataTask(fun (result) {
            val adapter = CoinsAdapter(this, result)
            coinsListView.adapter = adapter
        }).execute("https://www.bfxdata.com/json/bfxdataToday.json")
    }
}
