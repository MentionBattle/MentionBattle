package org.mentionbattle.server

import io.javalin.Javalin

fun main(args : Array<String>) {
    val app = Javalin.create()
            .port(7777)
            .enableStaticFiles("/public")
            .start()
}

