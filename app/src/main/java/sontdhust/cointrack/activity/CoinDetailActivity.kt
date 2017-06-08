package sontdhust.cointrack.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import sontdhust.cointrack.R
import sontdhust.cointrack.fragment.CoinDetailTradesFragment
import sontdhust.cointrack.helper.DataFetcher
import java.net.URI


class CoinDetailActivity : AppCompatActivity() {

    private var name: String = ""
    private var adapter: SectionsPagerAdapter? = null
    private var pager: ViewPager? = null
    private lateinit var socket: DataFetcher.Socket

    companion object {
        private val INTENT_NAME = "name"
        private val SOCKET_URI = "wss://api2.bitfinex.com:3000/ws"

        fun intent(context: Context, name: String): Intent {
            val intent = Intent(context, CoinDetailActivity::class.java)
            intent.putExtra(INTENT_NAME, name)
            return intent
        }
    }

    /*
     * Methods: AppCompatActivity
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coin_detail)
        name = intent.getStringExtra(INTENT_NAME)

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.title = name
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this, CoinsListActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

        adapter = SectionsPagerAdapter(supportFragmentManager)
        pager = findViewById(R.id.view_pager) as ViewPager
        pager?.adapter = adapter
        connectWebSocket()
    }

    override fun onDestroy() {
        socket.close()
        super.onDestroy()
    }

    /*
     * Helpers
     */

    private inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return CoinDetailTradesFragment.newInstance(name)
        }

        override fun getCount(): Int {
            return 1
        }
    }

    private fun connectWebSocket() {
        socket = DataFetcher.Socket(this, URI(SOCKET_URI), "{ \"event\": \"subscribe\", \"channel\": \"trades\", \"pair\": \"$name\" }")
        socket.setOnMessage {
            message ->
            System.out.println(message)
        }
    }
}
