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
import android.widget.TextView
import org.json.JSONArray
import sontdhust.cointrack.R
import sontdhust.cointrack.fragment.CoinDetailTradesFragment
import sontdhust.cointrack.helper.DataFetcher
import java.net.URI

class CoinDetailActivity : AppCompatActivity() {

    private var name: String = ""
    private var adapter: SectionsPagerAdapter? = null
    private var pager: ViewPager? = null
    private lateinit var socket: DataFetcher.Socket
    private var tradesChannelId: Int? = null
    private var onTradesSnapshot: ((ArrayList<JSONArray>) -> Unit)? = null
    private var onTradesUpdate: ((JSONArray) -> Unit)? = null

    companion object {
        private val INTENT_NAME = "name"
        private val SOCKET_URI = "wss://api2.bitfinex.com:3000/ws"
        private val FRAGMENTS = arrayOf("Trades") // TODO: Use string resource

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

        val fragmentNameTextView = findViewById(R.id.fragment_name_text_view) as TextView
        fragmentNameTextView.text = FRAGMENTS[0]
        adapter = SectionsPagerAdapter(supportFragmentManager)
        pager = findViewById(R.id.view_pager) as ViewPager
        pager?.adapter = adapter
        pager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(p0: Int) {
            }

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
            }

            override fun onPageSelected(p0: Int) {
                fragmentNameTextView.text = FRAGMENTS[p0]
            }
        })
        createSocket()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (!socket.isOpen) {
            socket.connect()
            socket.subscribeTrades(name)
        }
    }

    override fun onDestroy() {
        socket.close()
        super.onDestroy()
    }

    /*
     * Actions
     */

    fun setOnTradesSnapshot(onTradesSnapshot: (ArrayList<JSONArray>) -> Unit) {
        this.onTradesSnapshot = onTradesSnapshot
    }

    fun setOnTradesUpdate(onTradesUpdate: (JSONArray) -> Unit) {
        this.onTradesUpdate = onTradesUpdate
    }

    /*
     * Helpers
     */

    private inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return CoinDetailTradesFragment.newInstance(name)
        }

        override fun getCount(): Int {
            return FRAGMENTS.size
        }
    }

    private fun createSocket() {
        socket = DataFetcher.Socket(this, URI(SOCKET_URI))
        socket.setOnSubscribedTrades {
            id, _ ->
            tradesChannelId = id
        }
        socket.setOnSubscriptionSnapshot {
            id, snapshot ->
            if (id == tradesChannelId && onTradesSnapshot != null) {
                onTradesSnapshot?.invoke(snapshot)
            }
        }
        socket.setOnSubscriptionUpdate {
            id, update ->
            if (id == tradesChannelId && onTradesUpdate != null) {
                onTradesUpdate?.invoke(update)
            }
        }
    }
}
