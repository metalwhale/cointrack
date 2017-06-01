package sontdhust.cointrack.component

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import sontdhust.cointrack.R
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
            rowView.tag = viewHolder
        } else {
            rowView = convertView
            viewHolder = convertView.tag as ViewHolder
        }
        viewHolder.nameTextView.text = coin.name
        return rowView
    }

    private class ViewHolder {
        lateinit var nameTextView: TextView
    }
}
