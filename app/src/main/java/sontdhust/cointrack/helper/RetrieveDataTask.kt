package sontdhust.cointrack.helper

import android.os.AsyncTask
import org.json.JSONObject
import sontdhust.cointrack.model.Coin
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.util.regex.Pattern

class RetrieveDataTask(val postExecute: (ArrayList<Coin>?) -> Unit) : AsyncTask<String, Void, ArrayList<Coin>>() {

    override fun doInBackground(vararg urls: String?): ArrayList<Coin> {
        val url = URL(urls[0])
        val reader = BufferedReader(InputStreamReader(url.openStream()))
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
        val coinNames = JSONObject(buffer.toString()).keys()
        val coins = ArrayList<Coin>()
        while (coinNames.hasNext()) {
            val matcher = Pattern.compile("price([A-Z]{3})USD").matcher(coinNames.next())
            if (matcher.find()) {
                val coinName = matcher.group(1)
                coins.add(Coin(coinName))
            }
        }
        return coins
    }

    override fun onPostExecute(result: ArrayList<Coin>?) {
        super.onPostExecute(result)
        postExecute(result)
    }
}
