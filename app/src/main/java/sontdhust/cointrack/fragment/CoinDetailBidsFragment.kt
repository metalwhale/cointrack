package sontdhust.cointrack.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import sontdhust.cointrack.R
import sontdhust.cointrack.activity.CoinDetailActivity
import sontdhust.cointrack.component.BooksAdapter
import sontdhust.cointrack.model.Book
import java.util.*
import kotlin.concurrent.fixedRateTimer

class CoinDetailBidsFragment : Fragment() {

    private lateinit var timer: Timer

    /*
     * Methods: Fragment
     */

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_coin_detail_bids, container, false)
        val bidsListView = view?.findViewById(R.id.bids_list_view) as ListView
        val list = ArrayList<Book>()
        var bidsList = ArrayList<Book>()
        val adapter = BooksAdapter(activity, list)
        bidsListView.adapter = adapter
        val coinDetailActivity = activity as CoinDetailActivity
        fun updateList() {
            bidsList.sortBy { -it.price }
            var sum = 0.0
            list.clear()
            list.addAll(bidsList.map {
                book ->
                sum += book.amount
                val newBook = book.copy()
                newBook.sum = sum
                newBook
            })
            adapter.amount = bidsList.maxBy { it.amount }?.amount ?: 0.0
        }
        coinDetailActivity.setOnBooksSnapshot {
            books ->
            books.mapTo(bidsList) { Book(it.getDouble(0), it.getDouble(2), it.getInt(1), 0.0) }
            bidsList = ArrayList(bidsList.filter { it.amount > 0.0 })
            updateList()
            adapter.notifyDataSetChanged()
        }
        coinDetailActivity.setOnBooksUpdate {
            book ->
            val price = book.getDouble(0)
            val amount = book.getDouble(2)
            val count = book.getInt(1)
            bidsList.removeAll { it.price == price }
            if (count != 0 && amount > 0.0) {
                bidsList.add(Book(price, amount, count, 0.0))
            }
        }
        timer = fixedRateTimer(period = 5000, action = {
            activity.runOnUiThread {
                updateList()
                adapter.notifyDataSetChanged()
            }
        })
        return view
    }

    override fun onDetach() {
        timer.cancel()
        timer.purge()
        super.onDetach()
    }
}
