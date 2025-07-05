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
        private val LOG = LoggerFactory.getLogger(Main::class.java)

        @JvmStatic
        fun main(args: Array<String>) {
            val env = EnvironmentType.valueOf(System.getenv("APPLICATION_ENV") ?: EnvironmentType.PRODUCTION.name)
            val webappConfig = createAppConfig(env)
            embeddedServer(factory = Netty, port = webappConfig.httpPort) {
                createKtorApplication(webappConfig)
            }.start(wait = true)
        }

        private fun Application.createKtorApplication(webappConfig: WebappConfig) {
            LOG.info("Application runs in the environment ${webappConfig.env}")
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
            return "Hello, World! Class=" + Main::class.java
        }

        private fun createAppConfig(env: EnvironmentType): WebappConfig =
            ConfigFactory
                .parseResources("app-${env.shortName}.conf")
                .withFallback(ConfigFactory.parseResources("app.conf"))
                .resolve()
                .let {
                    WebappConfig(
                        httpPort = it.getInt("httpPort"),
                        env = env
                    )
                }

    }
}