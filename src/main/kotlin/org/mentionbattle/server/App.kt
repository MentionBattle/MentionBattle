package org.mentionbattle.server

import io.javalin.Javalin
import io.javalin.embeddedserver.jetty.websocket.WsSession
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import org.eclipse.jetty.websocket.api.Session
import java.util.*
import java.util.concurrent.TimeUnit

val initialMessage = "init|{\"contender1\":{\"votes\": 1000, \"rate\": 900, \"last\": [" +
        "{\"from\": \"twitter\", \"name\": \"asdzxc\", \"text\": \"asdzxc\", \"timestamp\": \"2017-12-02T02:51:15.952Z\"}," +
        "{\"from\": \"vk\", \"name\": \"123456\", \"text\": \"123456\", \"timestamp\": \"2017-12-02T02:52:15.952Z\"}" +
        "]}," +
        "\"contender2\": {" +
        "\"votes\": 800, \"rate\": 200, \"last\": [" +
        "{\"from\": \"twitter\", \"name\": \"asdzxc\", \"text\": \"asdzxc\", \"timestamp\": \"2017-12-02T02:51:15.952Z\"}," +
        "{\"from\": \"vk\", \"name\": \"123456\", \"text\": \"123456\", \"timestamp\": \"2017-12-02T02:52:15.952Z\"}" +
        "]}" +
        "}"
val mentionsMessages = listOf("mention|{\"contender\": 1, \"msg\": {\"from\": \"twitter\", \"name\": \"asdzxc\", \"text\": \"asdzxc\", \"timestamp\": \"2017-12-02T02:51:15.952Z\"}}",
        "mention|{\"contender\": 2, \"msg\": {\"from\": \"vk\", \"name\": \"123\", \"text\": \"zxc\", \"timestamp\": \"2017-12-02T02:51:15.952Z\"}}",
        "mention|{\"contender\": 1, \"msg\": {\"from\": \"facebook\", \"name\": \"aaa\", \"text\": \"1111\", \"timestamp\": \"2017-12-02T02:51:15.952Z\"}}")
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
                    ws.onClose({ session, statusCode, reason -> toRemove.add(session) })
                    ws.onMessage({ session, msg -> })
                }
            })
            .start()

    launch {
        val rnd = Random()
        while (true) {
            val sendNext = rnd.nextInt(mentionsMessages.count())
            delay(15, TimeUnit.SECONDS)
            toRemove.forEach({ t -> sessions.remove(t) })
            toRemove.clear()
            sessions.forEach({ s -> s.send(mentionsMessages[sendNext]) })
            println("send new message:")
            println(mentionsMessages[sendNext])
        }
    }

}

