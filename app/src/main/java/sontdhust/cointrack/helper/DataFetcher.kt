package sontdhust.cointrack.helper

import android.app.Activity
import android.os.AsyncTask
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONObject
import sontdhust.cointrack.R
import sontdhust.cointrack.model.Coin
import sontdhust.cointrack.model.Coin.Field
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.Reader
import java.lang.Exception
import java.net.URI
import java.net.URL
import java.security.KeyStore
import java.util.regex.Pattern
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory

class DataFetcher {

    companion object {
        private val CURRENCY = "USD"
    }

    class Task(val postExecute: (ArrayList<Coin>?) -> Unit) : AsyncTask<Void, Void, ArrayList<Coin>>() {

        companion object {
            private val TODAY = "https://www.bfxdata.com/json/bfxdataToday.json"
            private val TODAY_MINUTE = "https://www.bfxdata.com/json/bfxdataToday1Minute.json"
            private val KEYS = hashMapOf(
                    Field.PRICE to "price",
                    Field.CHANGE_ABS to "change24abs",
                    Field.CHANGE_REL to "change24rel",
                    Field.BID to "bid",
                    Field.MAX to "max",
                    Field.ASK to "ask",
                    Field.MIN to "min",
                    Field.VOLUME_DAY to "todayVolume",
                    Field.VOLUME_CHANGE to "volumeChange",
                    Field.VOLUME_WEEK to "volumeWeek",
                    Field.VWAP to "vwap24",
                    Field.VOLUME_MONTH to "volumeMonth",
                    Field.BUY to "buyPercentage"
            )
        }

        /*
         * Methods: AsyncTask
         */

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
                    enumValues<Field>().forEach {
                        field ->
                        val key = KEYS[field] + coinName + CURRENCY
                        coin[field] = (
                                if (todayData.has(key))
                                    todayData.getJSONArray(key).getString(0).toDoubleOrNull()
                                else
                                    todayMinuteData.getJSONArray(key).getString(0).toDoubleOrNull()
                                ) ?: 0.0
                    }
                    coins.add(coin)
                }
            }
            return coins
        }

        override fun onPostExecute(result: ArrayList<Coin>?) {
            super.onPostExecute(result)
            postExecute(result)
        }

        /*
         * Helpers
         */

        private fun readUrl(url: String): JSONObject {
            val reader = BufferedReader(InputStreamReader(URL(url).openStream()) as Reader?)
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

    class Socket(val activity: Activity, val uri: URI, val subscription: String) {

        private var socketClient: WebSocketClient
        private var onMessage: ((String?) -> Unit)? = null

        companion object {
            private val SOCKET_STORE_PASS = "cointrack"
        }

        init {
            socketClient = object : WebSocketClient(uri) {

                override fun onOpen(serverHandshake: ServerHandshake) {
                    send(subscription)
                }

                override fun onMessage(message: String?) {
                    activity.runOnUiThread {
                        if (onMessage != null) {
                            onMessage?.invoke(message)
                        }
                    }
                }

                override fun onError(ex: Exception?) {
                    ex!!.printStackTrace()
                }

                override fun onClose(code: Int, reason: String?, remote: Boolean) {
                    System.out.println(reason)
                }
            }
            val keyStore = KeyStore.getInstance("BKS")
            val rawStream = activity.baseContext.resources.openRawResource(R.raw.keystore)
            keyStore.load(rawStream, SOCKET_STORE_PASS.toCharArray())
            val keyManagerFactory = KeyManagerFactory.getInstance("X509")
            keyManagerFactory.init(keyStore, "".toCharArray())
            val trustManagerFactory = TrustManagerFactory.getInstance("X509")
            trustManagerFactory.init(keyStore)
            val sslContext = SSLContext.getInstance("TLS")
            sslContext!!.init(keyManagerFactory.keyManagers, trustManagerFactory.trustManagers, null)
            socketClient.socket = sslContext.socketFactory.createSocket()
            socketClient.connect()
        }

        /*
         * Actions
         */

        fun setOnMessage(onMessage: (String?) -> Unit) {
            this.onMessage = onMessage
        }

        fun close() {
            socketClient.close()
        }
    }
}
