package sontdhust.cointrack.helper

import org.json.JSONArray
import java.text.DecimalFormat
import java.text.NumberFormat

fun Double.toFormatString(pattern: String): String {
    val format = NumberFormat.getInstance() as DecimalFormat
    format.applyPattern(pattern)
    return format.format(this)
}

operator fun JSONArray.iterator(): Iterator<Any>
        = (0 until length()).asSequence().map { get(it) }.iterator()
