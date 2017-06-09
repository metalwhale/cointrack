package sontdhust.cointrack.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import sontdhust.cointrack.R
import sontdhust.cointrack.activity.CoinDetailActivity
import sontdhust.cointrack.component.TradesAdapter
import sontdhust.cointrack.model.Trade

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
        val list = ArrayList<Trade>()
        val adapter = TradesAdapter(activity, list)
        tradesListView.adapter = adapter
        val coinDetailActivity = activity as CoinDetailActivity
        coinDetailActivity.setOnTradesSnapshot {
            trades ->
            adapter.clear()
            val tradesArray = ArrayList<Trade>()
            trades.mapTo(tradesArray) { Trade(it.getDouble(2), it.getDouble(3), it.getInt(1)) }
            adapter.addAll(tradesArray)
            adapter.notifyDataSetChanged()
        }
        coinDetailActivity.setOnTradesUpdate {
            trade ->
            if (trade.getString(0) == "te") {
                list.removeAt(list.size - 1)
                list.add(0, Trade(trade.getDouble(3), trade.getDouble(4), trade.getInt(2)))
                adapter.notifyDataSetChanged()
            }
        }
        return view
    }
}
