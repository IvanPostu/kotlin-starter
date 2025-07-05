package com.iv127.kotlin.starter;

import com.typesafe.config.ConfigFactory
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.LoggerFactory

class Main {
    companion object {
        private val LOG = LoggerFactory.getLogger(Main.javaClass)

        @JvmStatic
        fun main(args: Array<String>) {
            val webappConfig = createAppConfig()

            embeddedServer(factory = Netty, port = webappConfig.httpPort) {
                createKtorApplication()
            }.start(wait = true)
        }

        private fun Application.createKtorApplication() {
            install(StatusPages) {
                exception<Throwable> { call, cause ->
                    LOG.error("An unknown error occurred", cause)
                    call.respondText(
                        text = "500: $cause",
                        status = HttpStatusCode.InternalServerError
                    )
                }
            }

            routing {
                get("/") {
                    LOG.debug("request received")
                    call.respondText(getClicheMessage())
                }
                get("/err") {
                    throw IllegalStateException("test exception")
                    call.respondText(getClicheMessage())
                }
            }
        }


        private fun getClicheMessage(): String {
            return "Hello, World! Class=" + Main.javaClass.name
        }

        private fun createAppConfig() =
            ConfigFactory
                .parseResources("app.conf")
                .resolve()
                .let {
                    WebappConfig(
                        httpPort = it.getInt("httpPort"),
                        test1 = null
                    )
                }

    }
}