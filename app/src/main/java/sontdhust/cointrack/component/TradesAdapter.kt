package sontdhust.cointrack.component

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import sontdhust.cointrack.R
import sontdhust.cointrack.model.Trade

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
        viewHolder.price.text = trade.price.toString()
        viewHolder.amount.text = trade.amount.toString()
        viewHolder.time.text = trade.time.toString()
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
