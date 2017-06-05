package sontdhust.cointrack.model

class Coin(val name: String) {
    var price: Double = 0.0
    var changeAbs: Double = 0.0
    var changeRel: Float = 0.0F
    var bid: Double = 0.0
    var max: Double = 0.0
    var ask: Double = 0.0
    var min: Double = 0.0
    var volumeDay: Double = 0.0
    var volumeChange: Float = 0.0F
    var volumeWeek: Double = 0.0
    var vwap: Double = 0.0
    var volumeMonth: Double = 0.0
    var buy: Float = 0.0F
}
