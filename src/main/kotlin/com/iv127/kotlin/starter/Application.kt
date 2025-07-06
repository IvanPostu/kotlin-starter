package com.iv127.kotlin.starter

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.header
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.util.pipeline.PipelineContext
import io.ktor.util.pipeline.PipelineInterceptor
import kotliquery.Row
import kotliquery.Session
import kotliquery.queryOf
import kotliquery.sessionOf
import org.slf4j.LoggerFactory
import javax.sql.DataSource

class Application {
    companion object {
        private val LOG = LoggerFactory.getLogger(Application::class.java)

        @JvmStatic
        fun main(args: Array<String>) {
            val env = EnvironmentType.valueOf(System.getenv("APPLICATION_ENV") ?: EnvironmentType.LOCAL.name)
            val webappConfig = createAppConfig(env)
            LOG.info("Configuration loaded successfully: {}{}", System.lineSeparator(), webappConfig)
            embeddedServer(factory = Netty, port = webappConfig.httpPort) {
                createKtorApplication(webappConfig)
            }.start(wait = true)
        }

        @Suppress("detekt.LongMethod")
        private fun io.ktor.server.application.Application.createKtorApplication(webappConfig: WebappConfig) {
            val dataSource = createAndMigrateDataSource(webappConfig)

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
                        status = HttpStatusCode.InternalServerError,
                    )
                }
            }

            routing {
                get(
                    "/",
                    webResponse {
                        LOG.debug("request received")
                        TextWebResponse(getClicheMessage())
                    },
                )
                get(
                    "/param_test",
                    webResponse {
                        TextWebResponse(
                            "The param is: ${call.request.queryParameters["foo"]}",
                        )
                    },
                )
                get(
                    "/json_test",
                    webResponse {
                        JsonWebResponse(mapOf("foo" to "bar"))
                    },
                )
                get(
                    "/json_test_with_header",
                    webResponse {
                        JsonWebResponse(mapOf("foo" to "bar"))
                            .header("X-Test-Header", "Just a test!")
                    },
                )
                get("/err") {
                    throw IllegalArgumentException("test exception")
                    call.respondText(getClicheMessage())
                }
                get(
                    "/db_test1",
                    webResponseDb(dataSource) { dbSess ->
                        JsonWebResponse(
                            dbSess.single(queryOf("SELECT 1"), ::mapFromRow),
                        )
                    },
                )
                get(
                    "/db_test2",
                    webResponseDb(dataSource) { dbSess ->
                        JsonWebResponse(
                            dbSess.single(queryOf("SELECT 1 AS example"), ::mapFromRow),
                        )
                    },
                )
                get(
                    "/single_user",
                    webResponseDb(dataSource) { dbSess ->
                        JsonWebResponse(
                            dbSess.single(
                                queryOf("SELECT * FROM user_t"),
                                ::mapFromRow,
                            )?.let(User::fromRow),
                        )
                    },
                )
            }
        }

        private fun getClicheMessage(): String {
            return "Hello, World! Class=" + Application::class.java
        }

        private fun webResponse(
            handler: suspend PipelineContext<Unit, ApplicationCall>.() -> WebResponse,
        ): PipelineInterceptor<Unit, ApplicationCall> {
            return {
                val resp: WebResponse = this.handler()
                for ((name, values) in resp.headers())
                    for (value in values)
                        call.response.header(name, value)
                val statusCode =
                    HttpStatusCode.fromValue(
                        resp.statusCode,
                    )
                when (resp) {
                    is TextWebResponse -> {
                        call.respondText(
                            text = resp.body,
                            status = statusCode,
                        )
                    }

                    is JsonWebResponse -> {
                        call.respond(
                            KtorJsonWebResponse(
                                body = resp.body,
                                status = statusCode,
                            ),
                        )
                    }
                }
            }
        }

//        private fun webResponseTx(
//            dataSource: DataSource,
//            handler: suspend PipelineContext<Unit, ApplicationCall>.(
//                dbSess: TransactionalSession
//            ) -> WebResponse
//        ) = webResponseDb(dataSource) { dbSess ->
//            dbSess.transaction { txSess ->
//                handler(txSess)
//            }
//        }

        private fun webResponseDb(
            dataSource: DataSource,
            handler: suspend PipelineContext<Unit, ApplicationCall>.(
                dbSess: Session,
            ) -> WebResponse,
        ) = webResponse {
            sessionOf(
                dataSource,
                returnGeneratedKey = true,
            ).use { dbSess ->
                handler(dbSess)
            }
        }

        private fun mapFromRow(row: Row): Map<String, Any?> {
            return row.underlying.metaData
                .let { (1..it.columnCount).map(it::getColumnName) }
                .map { it to row.anyOrNull(it) }
                .toMap()
        }
    }

    @Suppress("detekt.UnusedPrivateMember", "detekt.FunctionOnlyReturningConstant")
    private fun noOp(): String {
        return ""
    }
}
