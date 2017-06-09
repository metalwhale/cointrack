package sontdhust.cointrack.component

import android.content.Context
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import sontdhust.cointrack.R
import sontdhust.cointrack.helper.toFormatString
import sontdhust.cointrack.model.Trade
import java.lang.Math.abs

class TradesAdapter(context: Context, items: ArrayList<Trade>?) : ArrayAdapter<Trade>(context, 0, items) {

    /*
     * Methods: ArrayAdapter
     */

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val trade = getItem(position)
        val rowView: View
        val viewHolder: ViewHolder
        // Get view holder
        if (convertView == null) {
            rowView = LayoutInflater.from(context).inflate(R.layout.view_trade_row, parent, false)
            viewHolder = ViewHolder()
            viewHolder.price = rowView.findViewById(R.id.price_text_view) as TextView
            viewHolder.amount = rowView.findViewById(R.id.amount_text_view) as TextView
            viewHolder.type = rowView.findViewById(R.id.type_text_view) as TextView
            viewHolder.time = rowView.findViewById(R.id.time_text_view) as TextView
            rowView.tag = viewHolder
        } else {
            rowView = convertView
            viewHolder = convertView.tag as ViewHolder
        }
        // Update new values
        viewHolder.price.text = trade.price.toFormatString("###,###.0000")
        viewHolder.amount.text = abs(trade.amount).toFormatString("0.00")
        viewHolder.type.text = context.getString(if (trade.amount > 0) R.string.type_buy else R.string.type_sell)
        viewHolder.type.setTextColor(ContextCompat.getColor(context, if (trade.amount > 0) R.color.green else R.color.red))
        viewHolder.time.text = trade.time.toFormatString("HH:mm:ss")
        return rowView
    }

    /*
     * Helpers
     */

    private class ViewHolder {
        lateinit var price: TextView
        lateinit var amount: TextView
        lateinit var type: TextView
        lateinit var time: TextView
    }
}
