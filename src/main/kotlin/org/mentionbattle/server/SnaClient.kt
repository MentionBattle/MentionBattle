package org.mentionbattle.server

import org.json.JSONArray
import org.json.JSONObject
import org.mentionbattle.configuration.Configuration
import org.mentionbattle.utils.readString
import org.mentionbattle.utils.sendString
import java.net.Socket
import java.util.concurrent.TimeUnit

class SnaClient(config: Configuration, onFirstMessageReceived: (SnaClient) -> Unit,
                onDataReceived: (String) -> Unit) {
    private val config = config
    private lateinit var client: Socket
    private val onFirstMessageReceived = onFirstMessageReceived
    private val onDataReceived = onDataReceived
    private lateinit var initialJSON : JSONObject

    fun start() {
        client = Socket(config.snaUrl, config.snaPort)
        client.sendString(config.snaKey)
        val result = client.readString()
        initialJSON = JSONObject(result)
        onFirstMessageReceived(this)
        while (true) {
            val json = JSONObject(client.readString())
            val rateA = json["rateA"] as Int
            val rateB = json["rateB"] as Int

            ((initialJSON["contenders"] as JSONArray)[0] as JSONObject).put("votes", rateA)
            ((initialJSON["contenders"] as JSONArray)[1] as JSONObject).put("votes", rateB)

            onDataReceived("mention|" + json["event"])
        }
    }

    val initialMessage: String
        get() {
            return "init|" + initialJSON
        }
}