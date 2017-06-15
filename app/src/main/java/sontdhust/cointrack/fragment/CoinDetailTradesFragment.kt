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

    companion object {

        fun newInstance(): CoinDetailTradesFragment {
            val fragment = CoinDetailTradesFragment()
            return fragment
        }
    }

    /*
     * Methods: Fragment
     */

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
            trades.mapTo(list) { Trade(it.getDouble(2), it.getDouble(3), it.getInt(1)) }
            adapter.notifyDataSetChanged()
        }
        coinDetailActivity.setOnTradesUpdate {
            trade ->
            if (trade.getString(0) == "te") {
                if (list.size > 0) {
                    list.removeAt(list.size - 1)
                }
                list.add(0, Trade(trade.getDouble(3), trade.getDouble(4), trade.getInt(2)))
                adapter.notifyDataSetChanged()
            }
        }
        coinDetailActivity.subscribeTrades()
        return view
    }

    override fun onDestroyView() {
        (activity as CoinDetailActivity).unsubscribeTrades()
        super.onDestroyView()
    }
}
