package org.mentionbattle.server

import io.javalin.Javalin
import io.javalin.embeddedserver.jetty.websocket.WsSession
import org.eclipse.jetty.websocket.api.Session
import org.mentionbattle.configuration.Configuration
import java.net.SocketException

class Server(configuration: Configuration) {
    private val configuration = configuration
    private var app: Javalin? = null
    private lateinit var snaClient: SnaClient
    private val sessions = mutableListOf<WsSession>()

    fun start() {
        while (true) {
            try  {
                snaClient = SnaClient(configuration, this::createApp, this::createBroadcast)
                snaClient.start()
            } catch(e : SocketException) {
                System.err.println("An error occurer in snaClient, reconnecting")
                System.err.println(e.message)
            }
            Thread.sleep(15000)
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
                                session.send(snaClient.initialMessage)
                                sessions.add(session)
                                println("smbd connected")
                            })
                            ws.onClose({ session, statusCode, reason ->
                                synchronized(session) {
                                    sessions.remove(session)
                                }
                            })
                        }
                    })
                    .start()
        }
    }

    private fun createBroadcast(msg: String) {
        synchronized(sessions) {
            sessions.stream().filter(Session::isOpen).forEach({ session ->
                try {
                    session.remote.sendString(msg)
                } catch (e: Exception) {
                    println(e)
                }
            })
        }
    }
}

