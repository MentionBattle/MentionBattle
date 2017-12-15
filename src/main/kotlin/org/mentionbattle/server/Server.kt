package org.mentionbattle.server

import io.javalin.Javalin
import io.javalin.embeddedserver.jetty.websocket.WsSession
import org.mentionbattle.configuration.Configuration
import java.net.SocketException

class Server(configuration: Configuration) {
    private val configuration = configuration
    private var app: Javalin? = null
    private lateinit var snaClient: SnaClient
    private val sessions = mutableListOf<WsSession>()
    private val toRemove = mutableListOf<WsSession>()

    fun start() {
        while (true) {
            try  {
                snaClient = SnaClient(configuration, this::createApp, this::createBroadcast)
                snaClient.start()
            } catch(e : SocketException) {
                System.err.println("An error occurer in snaClient, reconnecting")
                System.err.println(e.message)
            }
        }
    }

    private fun createApp(snaClient : SnaClient) {
        if (app != null) {
            createBroadcast(snaClient.initialMessage)
        } else {
            app = Javalin.create().
                    enableStaticFiles("/public")
                    .port(configuration.port).ws("/mentionbattle",
                    { ws ->
                        run {
                            ws.onConnect({ session ->
                                sessions.add(session)
                                session.send(snaClient.initialMessage)
                                println("smbd connected")
                            })
                            ws.onClose({ session, statusCode, reason -> toRemove.add(session) })
                        }
                    })
                    .start()
        }
    }

    private fun createBroadcast(msg: String) {
        toRemove.forEach { t -> sessions.remove(t) }
        toRemove.clear()
        sessions.forEach { s -> s.send(msg) }
    }
}

