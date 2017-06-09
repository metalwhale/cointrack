package sontdhust.cointrack.helper

import org.json.JSONArray
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

fun Double.toFormatString(pattern: String): String {
    val format = NumberFormat.getInstance() as DecimalFormat
    format.applyPattern(pattern)
    return format.format(this)
}

fun Int.toFormatString(pattern: String): String {
    val date = Date(toLong() * 1000)
    val format = SimpleDateFormat(pattern, Locale.getDefault())
    format.timeZone = TimeZone.getDefault()
    return format.format(date)
}

operator fun JSONArray.iterator(): Iterator<Any>
        = (0 until length()).asSequence().map { get(it) }.iterator()
