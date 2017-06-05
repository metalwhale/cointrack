package sontdhust.cointrack.helper

import java.text.NumberFormat

fun Double.toFormatString(): String {
    return NumberFormat.getInstance().format(this)
}

fun Float.toFormatString(): String {
    return NumberFormat.getInstance().format(this)
}

fun Int.toFormatString(): String {
    return NumberFormat.getInstance().format(this)
}
