package com.iv127.kotlin.starter

import com.typesafe.config.ConfigFactory
import com.zaxxer.hikari.HikariDataSource
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*
import org.slf4j.LoggerFactory

class Main {
    companion object {
        private val LOG = LoggerFactory.getLogger(Main::class.java)

        @JvmStatic
        fun main(args: Array<String>) {
            val env = EnvironmentType.valueOf(System.getenv("APPLICATION_ENV") ?: EnvironmentType.LOCAL.name)
            val webappConfig = createAppConfig(env)
            LOG.info("Configuration loaded successfully: {}{}", System.lineSeparator(), webappConfig)
            embeddedServer(factory = Netty, port = webappConfig.httpPort) {
                createKtorApplication(webappConfig)
            }.start(wait = true)
        }

        private fun Application.createKtorApplication(webappConfig: WebappConfig) {
            val dataSource = createDataSource(webappConfig)

            dataSource.getConnection().use { conn ->
                conn.createStatement().use { stmt ->
                    stmt.executeQuery("SELECT 1")
                }
            }

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
                get("/", webResponse {
                    LOG.debug("request received")
                    TextWebResponse(getClicheMessage())
                })
                get("/param_test", webResponse {
                    TextWebResponse(
                        "The param is: ${call.request.queryParameters["foo"]}"
                    )
                })
                get("/json_test", webResponse {
                    JsonWebResponse(mapOf("foo" to "bar"))
                })
                get("/json_test_with_header", webResponse {
                    JsonWebResponse(mapOf("foo" to "bar"))
                        .header("X-Test-Header", "Just a test!")
                })
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
                        env = env,
                        secretExample = "qwerty",
                        dbUrl = it.getString("dbUrl"),
                        dbUser = it.getString("dbUser"),
                        dbPassword = it.getString("dbPassword"),
                    )
                }

        private fun webResponse(
            handler: suspend PipelineContext<Unit, ApplicationCall>.(
            ) -> WebResponse
        ): PipelineInterceptor<Unit, ApplicationCall> {
            return {
                val resp: WebResponse = this.handler()
                for ((name, values) in resp.headers())
                    for (value in values)
                        call.response.header(name, value)
                val statusCode = HttpStatusCode.fromValue(
                    resp.statusCode
                )
                when (resp) {
                    is TextWebResponse -> {
                        call.respondText(
                            text = resp.body,
                            status = statusCode
                        )
                    }

                    is JsonWebResponse -> {
                        call.respond(
                            KtorJsonWebResponse(
                                body = resp.body,
                                status = statusCode
                            )
                        )
                    }
                }
            }
        }

        private fun createDataSource(config: WebappConfig) =
            HikariDataSource().apply {
                jdbcUrl = config.dbUrl
                username = config.dbUser
                password = config.dbPassword
            }
    }
}