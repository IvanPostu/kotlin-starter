package com.iv127.kotlin.starter.app.fake.server

import com.iv127.kotlin.starter.app.TextWebResponse
import com.iv127.kotlin.starter.app.ktor.webResponse
import io.ktor.server.application.call
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.request.receiveText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.coroutines.delay

const val MIN_RANDOM_VALUE = 200L
const val MAX_RANDOM_VALUE = 2000L

fun main() {
    embeddedServer(Netty, port = 9876) {
        routing {
            get(
                "/random_number",
                webResponse {
                    val num = (MIN_RANDOM_VALUE..MAX_RANDOM_VALUE).random()
                    delay(num)
                    TextWebResponse(num.toString())
                },
            )
            get(
                "/ping",
                webResponse {
                    TextWebResponse("pong")
                },
            )
            post(
                "/reverse",
                webResponse {
                    TextWebResponse(call.receiveText().reversed())
                },
            )
        }
    }.start(wait = false)
}
