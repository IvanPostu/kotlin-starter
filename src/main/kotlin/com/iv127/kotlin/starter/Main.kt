package com.iv127.kotlin.starter;

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            embeddedServer(Netty, port = 4207) {
                createKtorApplication()
            }.start(wait = true)
        }

        private fun Application.createKtorApplication() {
            routing {
                get("/") {
                    call.respondText(getClicheMessage())
                }
            }
        }


        private fun getClicheMessage(): String {
            return "Hello, World! Class=" + Main.javaClass.name
        }

    }
}