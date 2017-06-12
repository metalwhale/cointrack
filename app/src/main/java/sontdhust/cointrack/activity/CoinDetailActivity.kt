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
import org.json.JSONArray
import sontdhust.cointrack.R
import sontdhust.cointrack.fragment.CoinDetailBooksFragment
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
    private var booksChannelId: Int? = null
    private var onBooksSnapshots = ArrayList<(ArrayList<JSONArray>) -> Unit>()
    private var onBooksUpdates = ArrayList<(JSONArray) -> Unit>()

    companion object {
        private val INTENT_NAME = "name"
        private val SOCKET_URI = "wss://api2.bitfinex.com:3000/ws"
        private val FRAGMENTS = arrayOf("Trades", "Bids", "Asks") // TODO: Use string resource

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
        pager?.offscreenPageLimit = 2
        pager?.adapter = adapter
        createSocket()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (!socket.isOpen) {
            socket.connect()
            socket.subscribeTrades(name)
            socket.subscribeBooks(name)
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

    fun addOnBooksSnapshot(onBooksSnapshot: (ArrayList<JSONArray>) -> Unit) {
        this.onBooksSnapshots.add(onBooksSnapshot)
    }

    fun addOnBooksUpdate(onBooksUpdate: (JSONArray) -> Unit) {
        this.onBooksUpdates.add(onBooksUpdate)
    }

    /*
     * Helpers
     */

    private inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> CoinDetailTradesFragment.newInstance()
                1 -> CoinDetailBooksFragment.newInstance(CoinDetailBooksFragment.Type.BIDS)
                2 -> CoinDetailBooksFragment.newInstance(CoinDetailBooksFragment.Type.ASKS)
                else -> {
                    Fragment()
                }
            }
        }

        override fun getCount(): Int {
            return FRAGMENTS.size
        }

        override fun getPageTitle(position: Int): CharSequence {
            return FRAGMENTS[position]
        }
    }

    private fun createSocket() {
        socket = DataFetcher.Socket(this, URI(SOCKET_URI))
        socket.setOnSubscribedTrades {
            id, _ ->
            tradesChannelId = id
        }
        socket.setOnSubscribedBooks {
            id, _ ->
            booksChannelId = id
        }
        socket.setOnSubscriptionSnapshot {
            id, snapshot ->
            if (id == tradesChannelId && onTradesSnapshot != null) {
                onTradesSnapshot?.invoke(snapshot)
            } else if (id == booksChannelId) {
                for (onBooksSnapshot in onBooksSnapshots) {
                    onBooksSnapshot.invoke(snapshot)
                }
            }
        }
        socket.setOnSubscriptionUpdate {
            id, update ->
            val isHeartBeat = update.getString(0) == "hb"
            if (isHeartBeat) {
                return@setOnSubscriptionUpdate
            }
            if (id == tradesChannelId && onTradesUpdate != null) {
                onTradesUpdate?.invoke(update)
            } else if (id == booksChannelId) {
                for (onBooksUpdate in onBooksUpdates) {
                    onBooksUpdate.invoke(update)
                }
            }
        }
    }
}
