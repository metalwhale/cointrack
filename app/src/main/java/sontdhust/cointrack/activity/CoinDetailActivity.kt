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
import sontdhust.cointrack.fragment.CoinDetailBooksFragment.Type
import sontdhust.cointrack.fragment.CoinDetailTradesFragment
import sontdhust.cointrack.helper.DataFetcher

class CoinDetailActivity : AppCompatActivity() {

    private var name: String = ""
    private var adapter: SectionsPagerAdapter? = null
    private var pager: ViewPager? = null
    private var socket: DataFetcher.Socket? = null
    private var tradesChannelId: Int? = null
    private var booksChannelId: Int? = null
    private var onTradesSnapshot: ((ArrayList<JSONArray>) -> Unit)? = null
    private var onBooksSnapshots = HashMap<String, (ArrayList<JSONArray>) -> Unit>()
    private var onTradesUpdate: ((JSONArray) -> Unit)? = null
    private var onBooksUpdates = HashMap<String, (JSONArray) -> Unit>()
    private var isResubscribingTrades = false
    private var isResubscribingBooks = false

    companion object {
        private val INTENT_NAME = "name"
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
        pager?.offscreenPageLimit = 1
        pager?.adapter = adapter
        createSocket()
    }

    override fun onDestroy() {
        socket?.close()
        super.onDestroy()
    }

    /*
     * Actions
     */

    fun subscribeTrades() {
        socket?.subscribeTrades(name)
    }

    fun subscribeBooks() {
        socket?.subscribeBooks(name)
    }

    fun unsubscribeTrades() {
        if (tradesChannelId != null) {
            socket?.unsubscribe(tradesChannelId as Int)
        }
    }

    fun unsubscribeBooks(tag: String) {
        onBooksSnapshots.remove(tag)
        onBooksUpdates.remove(tag)
        if (onBooksSnapshots.isEmpty() && onBooksUpdates.isEmpty()) {
            unsubscribeBooks()
        }
    }

    fun setOnTradesSnapshot(onTradesSnapshot: (ArrayList<JSONArray>) -> Unit) {
        this.onTradesSnapshot = onTradesSnapshot
    }

    fun setOnTradesUpdate(onTradesUpdate: (JSONArray) -> Unit) {
        this.onTradesUpdate = onTradesUpdate
    }

    fun addOnBooksSnapshot(tag: String, onBooksSnapshot: (ArrayList<JSONArray>) -> Unit) {
        onBooksSnapshots[tag] = onBooksSnapshot
    }

    fun addOnBooksUpdate(tag: String, onBooksUpdate: (JSONArray) -> Unit) {
        onBooksUpdates[tag] = onBooksUpdate
    }

    /*
     * Helpers
     */

    private inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> CoinDetailTradesFragment.newInstance()
                1 -> CoinDetailBooksFragment.newInstance(Type.BIDS)
                2 -> CoinDetailBooksFragment.newInstance(Type.ASKS)
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
        socket = DataFetcher.Socket(this)
        socket?.setOnSubscribedTrades {
            id, _ ->
            tradesChannelId = id
            isResubscribingTrades = false
        }
        socket?.setOnSubscribedBooks {
            id, _ ->
            booksChannelId = id
            isResubscribingBooks = false
        }
        socket?.setOnSubscriptionSnapshot {
            id, snapshot ->
            if (id == tradesChannelId) {
                onTradesSnapshot?.invoke(snapshot)
            } else if (id == booksChannelId) {
                for (onBooksSnapshot in onBooksSnapshots.values) {
                    onBooksSnapshot.invoke(snapshot)
                }
            }
        }
        socket?.setOnSubscriptionUpdate {
            id, update ->
            val isHeartBeat = update.getString(0) == "hb"
            if (isHeartBeat) {
                return@setOnSubscriptionUpdate
            }
            if (id == tradesChannelId) {
                onTradesUpdate?.invoke(update)
            } else if (id == booksChannelId) {
                for (onBooksUpdate in onBooksUpdates.values) {
                    onBooksUpdate.invoke(update)
                }
            }
        }
        socket?.setOnUnsubscribed {
            channelId ->
            when (channelId) {
                tradesChannelId -> tradesChannelId = null
                booksChannelId -> booksChannelId = null
            }
            if (isResubscribingTrades) {
                subscribeTrades()
            }
            if (isResubscribingBooks) {
                subscribeBooks()
            }
        }
        socket?.setOnErrorSubscribeTrades {
            _ ->
            isResubscribingTrades = true
            unsubscribeTrades()
        }
        socket?.setOnErrorSubscribeBooks {
            _ ->
            isResubscribingBooks = true
            unsubscribeBooks()
        }
    }

    private fun unsubscribeBooks() {
        if (booksChannelId != null) {
            socket?.unsubscribe(booksChannelId as Int)
        }
    }
}
