package sontdhust.cointrack.model

class Coin(val name: String) {

    enum class Field {
        PRICE,
        CHANGE_ABS, CHANGE_REL /* % */,
        BID, MAX,
        ASK, MIN,
        VOLUME_DAY, VOLUME_CHANGE /* % */,
        VOLUME_WEEK, VWAP,
        VOLUME_MONTH, BUY /* % */
    }

    private val fields: HashMap<Field, Double?> = HashMap()

    operator fun get(field: Field): Double {
        return fields[field] ?: 0.0
    }

    operator fun set(field: Field, value: Double?) {
        fields[field] = value
    }
}
