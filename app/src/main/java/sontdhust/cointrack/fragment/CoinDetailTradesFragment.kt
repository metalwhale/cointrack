package sontdhust.cointrack.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import sontdhust.cointrack.R

class CoinDetailTradesFragment : Fragment() {

    private var name: String? = null

    companion object {
        private val ARG_NAME = ""

        fun newInstance(param1: String): CoinDetailTradesFragment {
            val fragment = CoinDetailTradesFragment()
            val args = Bundle()
            args.putString(ARG_NAME, param1)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            name = arguments.getString(ARG_NAME)
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_coin_detail_trades, container, false)
    }
}
