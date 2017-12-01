package org.mentionbattle.server

import io.javalin.Javalin
import io.javalin.embeddedserver.jetty.websocket.WsSession
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import org.eclipse.jetty.websocket.api.Session
import java.util.*
import java.util.concurrent.TimeUnit

val initialMessage = "initialHERE"
val mentionsMessages = listOf("a", "b", "c")
val sessions = mutableListOf<WsSession>()
val toRemove = mutableListOf<WsSession>()

fun main(args: Array<String>) {
    val ws = Javalin.create().
            enableStaticFiles("/public")
            .port(80).ws("/mentionbattle",
            { ws ->
                run {
                    ws.onConnect({ session ->
                        sessions.add(session)
                        session.send(initialMessage)
                        println("smbd connected")
                    })
                    ws.onClose({ session, statusCode, reason -> toRemove.add(session)})
                    ws.onMessage({ session, msg -> })
                }
            })
            .start()

    launch {
        val rnd = Random()
        while (true) {
            val sendNext = rnd.nextInt(mentionsMessages.count())
            delay(3, TimeUnit.SECONDS)
            toRemove.forEach({t -> sessions.remove(t)})
            toRemove.clear()
            sessions.forEach({s -> s.send(mentionsMessages[sendNext])})
            println("send new message:")
            println(mentionsMessages[sendNext])
        }
    }

}

