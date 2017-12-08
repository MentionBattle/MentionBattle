package org.mentionbattle.server

import io.javalin.Javalin
import io.javalin.embeddedserver.jetty.websocket.WsSession
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import org.eclipse.jetty.websocket.api.Session
import org.mentionbattle.configuration.CreateConfiguration
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.TimeUnit


fun main(args: Array<String>) {
    Server(CreateConfiguration(Paths.get("app.config"))).start()
}

