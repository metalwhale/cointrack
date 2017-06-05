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
import sontdhust.cointrack.model.Coin

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
        viewHolder.priceTextView.text = coin.price.toFormatString()
        viewHolder.changeAbsTextView.text = coin.changeAbs.toFormatString()
        viewHolder.changeAbsTextView.setTextColor(
                ContextCompat.getColor(context,
                        if (coin.changeAbs > 0) R.color.colorGreen
                        else if (coin.changeAbs < 0) R.color.colorRed
                        else R.color.colorBlue)
        )
        viewHolder.changeRelTextView.text = coin.changeRel.toFormatString()
        viewHolder.changeRelTextView.setTextColor(
                ContextCompat.getColor(context,
                        if (coin.changeRel > 0) R.color.colorGreen
                        else if (coin.changeRel < 0) R.color.colorRed
                        else R.color.colorBlue)
        )
        viewHolder.bidTextView.text = coin.bid.toFormatString()
        viewHolder.maxTextView.text = coin.max.toFormatString()
        viewHolder.askTextView.text = coin.ask.toFormatString()
        viewHolder.minTextView.text = coin.min.toFormatString()
        viewHolder.volumeDayTextView.text = coin.volumeDay.toInt().toFormatString()
        viewHolder.volumeChangeTextView.text = coin.volumeChange.toFormatString()
        viewHolder.volumeChangeTextView.setTextColor(
                ContextCompat.getColor(context,
                        if (coin.volumeChange > 0) R.color.colorGreen
                        else if (coin.volumeChange < 0) R.color.colorRed
                        else R.color.colorBlue)
        )
        viewHolder.volumeWeekTextView.text = coin.volumeWeek.toInt().toFormatString()
        viewHolder.vwapTextView.text = coin.vwap.toFormatString()
        viewHolder.volumeMonthTextView.text = coin.volumeMonth.toInt().toFormatString()
        viewHolder.buyTextView.text = coin.buy.toFormatString()
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
