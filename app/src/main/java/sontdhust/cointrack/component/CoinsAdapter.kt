package sontdhust.cointrack.component

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import sontdhust.cointrack.R
import sontdhust.cointrack.model.Coin
import java.text.NumberFormat

class CoinsAdapter(context: Context, items: ArrayList<Coin>?) : ArrayAdapter<Coin>(context, 0, items) {

    /*
     * Methods: ArrayAdapter
     */

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val coin = getItem(position)
        val rowView: View
        val viewHolder: ViewHolder
        if (convertView == null) {
            rowView = LayoutInflater.from(context).inflate(R.layout.view_coin_row, parent, false)
            viewHolder = ViewHolder()
            viewHolder.nameTextView = rowView.findViewById(R.id.name_text_view) as TextView
            viewHolder.priceTextView = rowView.findViewById(R.id.price_text_view) as TextView
            viewHolder.changeAbsTextView = rowView.findViewById(R.id.change_abs_text_view) as TextView
            viewHolder.changeRelTextView = rowView.findViewById(R.id.change_rel_text_view) as TextView
            viewHolder.bidTextView = rowView.findViewById(R.id.bid_text_view) as TextView
            viewHolder.maxTextView = rowView.findViewById(R.id.max_text_view) as TextView
            viewHolder.askTextView = rowView.findViewById(R.id.ask_text_view) as TextView
            viewHolder.minTextView = rowView.findViewById(R.id.min_text_view) as TextView
            viewHolder.volumeDayTextView = rowView.findViewById(R.id.volume_day_text_view) as TextView
            viewHolder.volumeChangeTextView = rowView.findViewById(R.id.volume_change_text_view) as TextView
            viewHolder.volumeWeekTextView = rowView.findViewById(R.id.volume_week_text_view) as TextView
            viewHolder.vwapTextView = rowView.findViewById(R.id.vwap_text_view) as TextView
            viewHolder.volumeMonthTextView = rowView.findViewById(R.id.volume_month_text_view) as TextView
            viewHolder.buyTextView = rowView.findViewById(R.id.buy_text_view) as TextView
            rowView.tag = viewHolder
        } else {
            rowView = convertView
            viewHolder = convertView.tag as ViewHolder
        }
        viewHolder.nameTextView.text = coin.name
        viewHolder.priceTextView.text = coin.price.toString()
        viewHolder.changeAbsTextView.text = coin.changeAbs.toString()
        viewHolder.changeRelTextView.text = coin.changeRel.toString()
        viewHolder.bidTextView.text = coin.bid.toString()
        viewHolder.maxTextView.text = coin.max.toString()
        viewHolder.askTextView.text = coin.ask.toString()
        viewHolder.minTextView.text = coin.min.toString()
        viewHolder.volumeDayTextView.text = NumberFormat.getInstance().format(coin.volumeDay.toInt())
        viewHolder.volumeChangeTextView.text = coin.volumeChange.toString()
        viewHolder.volumeWeekTextView.text = NumberFormat.getInstance().format(coin.volumeWeek.toInt())
        viewHolder.vwapTextView.text = coin.vwap.toString()
        viewHolder.volumeMonthTextView.text = NumberFormat.getInstance().format(coin.volumeMonth.toInt())
        viewHolder.buyTextView.text = coin.buy.toString()
        return rowView
    }

    private class ViewHolder {
        lateinit var nameTextView: TextView
        lateinit var priceTextView: TextView
        lateinit var changeAbsTextView: TextView
        lateinit var changeRelTextView: TextView
        lateinit var bidTextView: TextView
        lateinit var maxTextView: TextView
        lateinit var askTextView: TextView
        lateinit var minTextView: TextView
        lateinit var volumeDayTextView: TextView
        lateinit var volumeChangeTextView: TextView
        lateinit var volumeWeekTextView: TextView
        lateinit var vwapTextView: TextView
        lateinit var volumeMonthTextView: TextView
        lateinit var buyTextView: TextView
    }
}
