package sontdhust.cointrack.helper

import android.app.Activity
import android.os.AsyncTask
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
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

    class Socket(val activity: Activity, val uri: URI) {

        private var socketClient: WebSocketClient
        private var onSubscribedTrades: ((Int, String) -> Unit)? = null
        private var onSubscriptionSnapshot: ((Int, ArrayList<JSONArray>) -> Unit)? = null
        private var onSubscriptionUpdate: ((Int, JSONArray) -> Unit)? = null

        companion object {
            private val SOCKET_STORE_PASS = "cointrack"
        }

        init {
            socketClient = object : WebSocketClient(uri) {

                override fun onOpen(serverHandshake: ServerHandshake) {
                }

                override fun onMessage(message: String?) {
                    activity.runOnUiThread {
                        val data = JSONTokener(message).nextValue()
                        if (data is JSONObject) {
                            if (data.has("event") && data.getString("event") == "subscribed"
                                    && data.has("channel") && data.getString("channel") == "trades") {
                                if (onSubscribedTrades != null) {
                                    onSubscribedTrades?.invoke(data.getInt("chanId"), data.getString("pair"))
                                }
                            }
                        } else if (data is JSONArray) {
                            val channelId = data.getInt(0)
                            val value = data.get(1)
                            if (value is JSONArray) {
                                val snapshot = ArrayList<JSONArray>()
                                for (update in value) {
                                    snapshot.add(update as JSONArray)
                                }
                                if (onSubscriptionSnapshot != null) {
                                    onSubscriptionSnapshot?.invoke(channelId, snapshot)
                                }
                            } else {
                                val update = JSONArray()
                                for (i in 1..(data.length() - 1)) {
                                    update.put(data.get(i))
                                }
                                if (onSubscriptionUpdate != null) {
                                    onSubscriptionUpdate?.invoke(channelId, update)
                                }
                            }
                        }
                    }
                }

                override fun onError(ex: Exception?) {
                    ex?.printStackTrace()
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
            sslContext?.init(keyManagerFactory.keyManagers, trustManagerFactory.trustManagers, null)
            socketClient.socket = sslContext.socketFactory.createSocket()
        }

        /*
         * Actions
         */

        val isOpen: Boolean get() = socketClient.isOpen

        fun connect() {
            socketClient.connectBlocking()
        }

        fun subscribeTrades(pair: String) {
            socketClient.send("{ \"event\": \"subscribe\", \"channel\": \"trades\", \"pair\": \"$pair$CURRENCY\" }")
        }

        fun setOnSubscribedTrades(onSubscribedTrades: (Int, String) -> Unit) {
            this.onSubscribedTrades = onSubscribedTrades
        }

        fun setOnSubscriptionSnapshot(onSubscriptionSnapshot: (Int, ArrayList<JSONArray>) -> Unit) {
            this.onSubscriptionSnapshot = onSubscriptionSnapshot
        }

        fun setOnSubscriptionUpdate(onSubscriptionUpdate: (Int, JSONArray) -> Unit) {
            this.onSubscriptionUpdate = onSubscriptionUpdate
        }

        fun close() {
            socketClient.close()
        }
    }
}
