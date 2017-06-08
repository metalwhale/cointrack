package sontdhust.cointrack.component

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import sontdhust.cointrack.R
import sontdhust.cointrack.helper.toFormatString
import sontdhust.cointrack.model.Coin
import sontdhust.cointrack.model.Coin.Field
import java.text.NumberFormat

class CoinsAdapter(context: Context, items: ArrayList<Coin>?) : ArrayAdapter<Coin>(context, 0, items) {

    /*
     * Methods: ArrayAdapter
     */

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val coin = getItem(position)
        val rowView: View
        val viewHolder: ViewHolder
        // Get view holder
        if (convertView == null) {
            rowView = LayoutInflater.from(context).inflate(R.layout.view_coin_row, parent, false)
            viewHolder = ViewHolder()
            viewHolder.name = rowView.findViewById(R.id.name_text_view) as TextView
            viewHolder.fields[Field.PRICE] = rowView.findViewById(R.id.price_text_view) as TextView
            viewHolder.fields[Field.CHANGE_ABS] = rowView.findViewById(R.id.change_abs_text_view) as TextView
            viewHolder.fields[Field.CHANGE_REL] = rowView.findViewById(R.id.change_rel_text_view) as TextView
            viewHolder.fields[Field.BID] = rowView.findViewById(R.id.bid_text_view) as TextView
            viewHolder.fields[Field.MAX] = rowView.findViewById(R.id.max_text_view) as TextView
            viewHolder.fields[Field.ASK] = rowView.findViewById(R.id.ask_text_view) as TextView
            viewHolder.fields[Field.MIN] = rowView.findViewById(R.id.min_text_view) as TextView
            viewHolder.fields[Field.VOLUME_DAY] = rowView.findViewById(R.id.volume_day_text_view) as TextView
            viewHolder.fields[Field.VOLUME_CHANGE] = rowView.findViewById(R.id.volume_change_text_view) as TextView
            viewHolder.fields[Field.VOLUME_WEEK] = rowView.findViewById(R.id.volume_week_text_view) as TextView
            viewHolder.fields[Field.VWAP] = rowView.findViewById(R.id.vwap_text_view) as TextView
            viewHolder.fields[Field.VOLUME_MONTH] = rowView.findViewById(R.id.volume_month_text_view) as TextView
            viewHolder.fields[Field.BUY] = rowView.findViewById(R.id.buy_text_view) as TextView
            rowView.tag = viewHolder
        } else {
            rowView = convertView
            viewHolder = convertView.tag as ViewHolder
        }
        // Run update animation
        if (viewHolder.name.text == coin.name) {
            val upFields = ArrayList<Field>()
            val downFields = ArrayList<Field>()
            enumValues<Field>().forEach {
                field ->
                val oldValue = NumberFormat.getInstance().parse(viewHolder.fields[field]!!.text.toString()).toDouble()
                val newValue = NumberFormat.getInstance().parse(formatField(coin, field)).toDouble()
                if (newValue > oldValue) {
                    upFields.add(field)
                } else if (newValue < oldValue) {
                    downFields.add(field)
                }
            }
            val upAnimator = ValueAnimator.ofObject(ArgbEvaluator(), ContextCompat.getColor(context, R.color.lightGreen), Color.TRANSPARENT)
            upAnimator.duration = 5000
            upAnimator.addUpdateListener {
                animator ->
                upFields.forEach {
                    field ->
                    viewHolder.fields[field]!!.setBackgroundColor(animator.animatedValue as Int)
                }
            }
            val downAnimator = ValueAnimator.ofObject(ArgbEvaluator(), ContextCompat.getColor(context, R.color.lightRed), Color.TRANSPARENT)
            downAnimator.duration = 5000
            downAnimator.addUpdateListener {
                animator ->
                downFields.forEach {
                    field ->
                    viewHolder.fields[field]!!.setBackgroundColor(animator.animatedValue as Int)
                }
            }
            upAnimator.start()
            downAnimator.start()
        }
        // Update new values
        viewHolder.name.text = coin.name
        enumValues<Field>().forEach {
            field ->
            viewHolder.fields[field]!!.text = formatField(coin, field)
        }
        viewHolder.fields[Field.CHANGE_ABS]!!.setTextColor(
                ContextCompat.getColor(context,
                        if (coin[Field.CHANGE_ABS] > 0) R.color.green
                        else if (coin[Field.CHANGE_ABS] < 0) R.color.red
                        else R.color.blue)
        )
        viewHolder.fields[Field.CHANGE_REL]!!.setTextColor(
                ContextCompat.getColor(context,
                        if (coin[Field.CHANGE_REL] > 0) R.color.green
                        else if (coin[Field.CHANGE_REL] < 0) R.color.red
                        else R.color.blue)
        )
        viewHolder.fields[Field.VOLUME_CHANGE]!!.setTextColor(
                ContextCompat.getColor(context,
                        if (coin[Field.VOLUME_CHANGE] > 0) R.color.green
                        else if (coin[Field.VOLUME_CHANGE] < 0) R.color.red
                        else R.color.blue)
        )
        return rowView
    }

    /*
     * Helpers
     */

    private class ViewHolder {
        lateinit var name: TextView
        val fields: HashMap<Coin.Field, TextView> = HashMap()
    }

    private fun formatField(coin: Coin, field: Field): String {
        return if (field == Field.VOLUME_DAY || field == Field.VOLUME_WEEK || field == Field.VOLUME_MONTH)
            coin[field].toFormatString("###,###")
        else
            coin[field].toFormatString("###,###.###")
    }
}
