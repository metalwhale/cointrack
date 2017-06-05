package sontdhust.cointrack.helper

import android.os.AsyncTask
import org.json.JSONObject
import sontdhust.cointrack.model.Coin
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.util.regex.Pattern

class RetrieveDataTask(val postExecute: (ArrayList<Coin>?) -> Unit) : AsyncTask<Void, Void, ArrayList<Coin>>() {

    companion object {
        val TODAY: String = "https://www.bfxdata.com/json/bfxdataToday.json"
        val TODAY_MINUTE: String = "https://www.bfxdata.com/json/bfxdataToday1Minute.json"
        val CURRENCY = "USD"
    }

    override fun doInBackground(vararg p0: Void?): ArrayList<Coin>? {
        val todayData = readUrl(TODAY)
        val todayMinuteData = readUrl(TODAY_MINUTE)
        val coinNames = todayData.keys()
        val coins = ArrayList<Coin>()
        while (coinNames.hasNext()) {
            val matcher = Pattern.compile("price([A-Z]{3})" + CURRENCY).matcher(coinNames.next())
            if (matcher.find()) {
                val coinName = matcher.group(1)
                val coin = Coin(coinName)
                coin.price = todayData.getJSONArray("price" + coinName + CURRENCY).getString(0).toDoubleOrNull() ?: 0.0
                coin.changeAbs = todayMinuteData.getJSONArray("change24abs" + coinName + CURRENCY).getString(0).toDoubleOrNull() ?: 0.0
                coin.changeRel = todayMinuteData.getJSONArray("change24rel" + coinName + CURRENCY).getString(0).toFloatOrNull() ?: 0.0F
                coin.bid = todayData.getJSONArray("bid" + coinName + CURRENCY).getString(0).toDoubleOrNull() ?: 0.0
                coin.max = todayData.getJSONArray("max" + coinName + CURRENCY).getString(0).toDoubleOrNull() ?: 0.0
                coin.ask = todayData.getJSONArray("ask" + coinName + CURRENCY).getString(0).toDoubleOrNull() ?: 0.0
                coin.min = todayData.getJSONArray("min" + coinName + CURRENCY).getString(0).toDoubleOrNull() ?: 0.0
                coin.volumeDay = todayMinuteData.getJSONArray("todayVolume" + coinName + CURRENCY).getString(0).toDoubleOrNull() ?: 0.0
                coin.volumeChange = todayMinuteData.getJSONArray("volumeChange" + coinName + CURRENCY).getString(0).toFloatOrNull() ?: 0.0F
                coin.volumeWeek = todayMinuteData.getJSONArray("volumeWeek" + coinName + CURRENCY).getString(0).toDoubleOrNull() ?: 0.0
                coin.vwap = todayMinuteData.getJSONArray("vwap24" + coinName + CURRENCY).getString(0).toDoubleOrNull() ?: 0.0
                coin.volumeMonth = todayMinuteData.getJSONArray("volumeMonth" + coinName + CURRENCY).getString(0).toDoubleOrNull() ?: 0.0
                coin.buy = todayMinuteData.getJSONArray("buyPercentage" + coinName + CURRENCY).getString(0).toFloatOrNull() ?: 0.0F
                coins.add(coin)
            }
        }
        return coins
    }

    override fun onPostExecute(result: ArrayList<Coin>?) {
        super.onPostExecute(result)
        postExecute(result)
    }

    private fun readUrl(url: String): JSONObject {
        val reader = BufferedReader(InputStreamReader(URL(url).openStream()))
        val buffer = StringBuffer()
        var read: Int
        val chars = CharArray(1024)
        do {
            read = reader.read(chars)
            if (read == -1) {
                break
            } else {
                buffer.append(chars, 0, read)
            }
        } while (true)
        return JSONObject(buffer.toString())
    }
}
