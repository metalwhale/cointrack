package sontdhust.cointrack.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import org.json.JSONArray
import sontdhust.cointrack.R
import sontdhust.cointrack.component.TradesAdapter
import java.util.*

class CoinDetailTradesFragment : Fragment() {

    private var name: String? = null

    companion object {
        private val ARG_NAME = ""

        fun newInstance(name: String): CoinDetailTradesFragment {
            val fragment = CoinDetailTradesFragment()
            val args = Bundle()
            args.putString(ARG_NAME, name)
            fragment.arguments = args
            return fragment
        }
    }

    /*
     * Methods: Fragment
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            name = arguments.getString(ARG_NAME)
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_coin_detail_trades, container, false)
        val tradesListView = view?.findViewById(R.id.trades_list_view) as ListView
        val adapter = TradesAdapter(activity, ArrayList())
        tradesListView.adapter = adapter
        return view
    }

    /*
     * Actions
     */

    fun snapshot(trades: ArrayList<JSONArray>) {
        System.out.println("Subscription Snapshot: ${trades[0]}")
    }

    fun update(trade: JSONArray) {
        System.out.println("Subscription Update: $trade")
    }
}
