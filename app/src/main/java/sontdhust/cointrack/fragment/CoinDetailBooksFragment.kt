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
import java.lang.Math.abs
import java.util.*
import kotlin.concurrent.fixedRateTimer

open class CoinDetailBooksFragment : Fragment() {

    enum class Type {
        BIDS, ASKS
    }

    private var timer: Timer? = null

    companion object {

        private val ARG_TYPE = "type"

        fun newInstance(type: Type): CoinDetailBooksFragment {
            val fragment = CoinDetailBooksFragment()
            val args = Bundle()
            args.putString(ARG_TYPE, type.name)
            fragment.arguments = args
            return fragment
        }
    }

    /*
     * Methods: Fragment
     */

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val type = Type.valueOf(arguments.getString(ARG_TYPE))
        val view = inflater?.inflate(R.layout.fragment_coin_detail_books, container, false)
        val booksListView = view?.findViewById(R.id.books_list_view) as ListView
        val list = ArrayList<Book>()
        var booksList = ArrayList<Book>()
        val adapter = BooksAdapter(activity, list)
        booksListView.adapter = adapter
        val coinDetailActivity = activity as CoinDetailActivity
        fun updateList() {
            booksList.sortBy { (if (type == Type.BIDS) -1 else 1) * it.price }
            var sum = 0.0
            list.clear()
            list.addAll(booksList.map {
                book ->
                sum += book.amount
                val newBook = book.copy()
                newBook.sum = sum
                newBook
            })
            adapter.amount = booksList.maxBy { abs(it.amount) }?.amount ?: 0.0
        }
        coinDetailActivity.addOnBooksSnapshot {
            books ->
            booksList.clear()
            books.mapTo(booksList) { Book(it.getDouble(0), it.getDouble(2), it.getInt(1), 0.0) }
            booksList = ArrayList(booksList.filter { if (type == Type.BIDS) it.amount > 0.0 else it.amount < 0.0 })
            updateList()
            adapter.notifyDataSetChanged()
        }
        coinDetailActivity.addOnBooksUpdate {
            book ->
            val price = book.getDouble(0)
            val amount = book.getDouble(2)
            val count = book.getInt(1)
            booksList.removeAll { it.price == price }
            if (count != 0 && (type == Type.BIDS && amount > 0.0 || type == Type.ASKS && amount < 0.0)) {
                booksList.add(Book(price, amount, count, 0.0))
            }
        }
        coinDetailActivity.subscribeBooks()
        timer = fixedRateTimer(period = 5000, action = {
            activity?.runOnUiThread {
                updateList()
                adapter.notifyDataSetChanged()
            }
        })
        return view
    }

    override fun onDestroyView() {
        (activity as CoinDetailActivity).unsubscribeBooks()
        // FIXME: Correctly stop timer
        timer?.cancel()
        timer?.purge()
        super.onDestroyView()
    }
}
