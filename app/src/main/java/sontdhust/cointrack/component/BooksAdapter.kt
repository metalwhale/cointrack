package sontdhust.cointrack.component

import android.content.Context
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import sontdhust.cointrack.R
import sontdhust.cointrack.helper.toFormatString
import sontdhust.cointrack.model.Book
import java.lang.Math.abs

class BooksAdapter(context: Context, items: ArrayList<Book>?) : ArrayAdapter<Book>(context, 0, items) {

    var amount = 0.0

    /*
     * Methods: ArrayAdapter
     */

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val book = getItem(position)
        val rowView: View
        val viewHolder: ViewHolder
        // Get view holder
        if (convertView == null) {
            rowView = LayoutInflater.from(context).inflate(R.layout.view_book_row, parent, false)
            viewHolder = ViewHolder()
            viewHolder.price = rowView.findViewById(R.id.price_text_view) as TextView
            viewHolder.amount = rowView.findViewById(R.id.amount_text_view) as TextView
            viewHolder.sum = rowView.findViewById(R.id.sum_text_view) as TextView
            viewHolder.amountBar = rowView.findViewById(R.id.amount_bar_view) as View
            rowView.tag = viewHolder
        } else {
            rowView = convertView
            viewHolder = convertView.tag as ViewHolder
        }
        // Update new values
        viewHolder.price.text = book.price.toFormatString("###,##0.0000")
        viewHolder.amount.text = abs(book.amount).toFormatString("###,##0.00")
        viewHolder.sum.text = abs(book.sum).toFormatString("###,##0.00")
        viewHolder.amountBar.setBackgroundColor(ContextCompat.getColor(context, if (book.amount > 0) R.color.lightGreen else R.color.lightRed))
        (viewHolder.amountBar.layoutParams as LinearLayout.LayoutParams).weight = abs(book.amount / amount).toFloat()
        return rowView
    }

    /*
     * Helpers
     */

    private class ViewHolder {
        lateinit var price: TextView
        lateinit var amount: TextView
        lateinit var sum: TextView
        lateinit var amountBar: View
    }
}
