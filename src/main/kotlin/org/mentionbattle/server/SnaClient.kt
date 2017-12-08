package org.mentionbattle.server

import org.mentionbattle.configuration.Configuration
import org.mentionbattle.utils.readString
import org.mentionbattle.utils.sendString
import java.net.Socket
import java.util.concurrent.TimeUnit

class SnaClient(config: Configuration, onFirstMessageReceived: (String) -> Unit,
                onDataReceived: (String) -> Unit) {
    private val config = config
    private lateinit var client: Socket
    private val onFirstMessageReceived = onFirstMessageReceived
    private val onDataReceived = onDataReceived

    fun start() {
        client = Socket(config.snaUrl, config.snaPort)
        client.sendString(config.snaKey)
        val result = client.readString()
        onFirstMessageReceived(result)
        while (true) {
            onDataReceived(client.readString())
        }
    }
}