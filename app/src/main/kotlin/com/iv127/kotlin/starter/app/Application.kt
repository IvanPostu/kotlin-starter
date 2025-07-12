package com.iv127.kotlin.starter.app

import arrow.core.continuations.either
import com.google.gson.Gson
import com.iv127.kotlin.starter.app.ktor.webResponse
import com.iv127.kotlin.starter.core.ExampleAbc
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.auth.session
import io.ktor.server.engine.embeddedServer
import io.ktor.server.html.Template
import io.ktor.server.html.respondHtml
import io.ktor.server.html.respondHtmlTemplate
import io.ktor.server.http.content.files
import io.ktor.server.http.content.resources
import io.ktor.server.http.content.static
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.receiveParameters
import io.ktor.server.request.receiveText
import io.ktor.server.response.respondRedirect
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.server.sessions.SessionTransportTransformerEncrypt
import io.ktor.server.sessions.Sessions
import io.ktor.server.sessions.clear
import io.ktor.server.sessions.cookie
import io.ktor.server.sessions.maxAge
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set
import io.ktor.util.hex
import io.ktor.util.pipeline.PipelineContext
import kotlinx.html.ButtonType
import kotlinx.html.FormMethod
import kotlinx.html.HTML
import kotlinx.html.InputType
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.button
import kotlinx.html.form
import kotlinx.html.h1
import kotlinx.html.head
import kotlinx.html.input
import kotlinx.html.label
import kotlinx.html.p
import kotlinx.html.styleLink
import kotlinx.html.title
import kotliquery.Session
import kotliquery.queryOf
import kotliquery.sessionOf
import org.slf4j.LoggerFactory
import javax.sql.DataSource
import kotlin.time.Duration

class Application {
    companion object {
        private val LOG = LoggerFactory.getLogger(Application::class.java)

        @JvmStatic
        fun main(args: Array<String>) {
            val env = EnvironmentType.valueOf(System.getenv("APPLICATION_ENV") ?: EnvironmentType.LOCAL.name)
            val webappConfig = createAppConfig(env)
            LOG.info("Configuration loaded successfully: {}{}", System.lineSeparator(), webappConfig)
            embeddedServer(factory = Netty, port = webappConfig.httpPort) {
                val dataSource = createAndMigrateDataSource(webappConfig)
                setUpKtorCookieSecurity(webappConfig)
                createKtorApplication(webappConfig, dataSource)
            }.start(wait = true)
        }

        fun io.ktor.server.application.Application.setUpKtorCookieSecurity(
            appConfig: WebappConfig
        ) {
            install(Sessions) {
                cookie<UserSession>("user-session") {
                    transform(
                        SessionTransportTransformerEncrypt(
                            hex(appConfig.cookieEncryptionKey),
                            hex(appConfig.cookieSigningKey)
                        )
                    )
                    cookie.maxAge = Duration.parse("30d")
                    cookie.httpOnly = true
                    cookie.path = "/"
                    cookie.secure = appConfig.useSecureCookie

                    // protects the cookie from cross-origin attacks, e.g. form from another site
                    cookie.extensions["SameSite"] = "lax"
                }
            }
            install(Authentication) {
                session<UserSession>("auth-session") {
                    validate { session ->
                        session
                    }
                    challenge {
                        call.respondRedirect("/login")
                    }
                }
            }
        }

        @Suppress("detekt.LongMethod")
        fun io.ktor.server.application.Application.createKtorApplication(
            webappConfig: WebappConfig,
            dataSource: DataSource
        ) {
            dataSource.getConnection().use { conn ->
                conn.createStatement().use { stmt ->
                    stmt.executeQuery("SELECT 1")
                }
            }

            LOG.info("Application runs in the environment ${webappConfig.env}")
            this.install(StatusPages) {
                exception<Throwable> { call, cause ->
                    LOG.error("An unknown error occurred", cause)
                    call.respondText(
                        text = "500: $cause",
                        status = HttpStatusCode.InternalServerError,
                    )
                }
            }

            routing {
                static("/") {
                    if (webappConfig.useFileSystemAssets) {
                        files("app/src/main/resources/public")
                    } else {
                        resources("public")
                    }
                }
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
                            dbSess.single(queryOf("SELECT 1"), { mapFromRow(it) }),
                        )
                    },
                )
                get(
                    "/db_test2",
                    webResponseDb(dataSource) { dbSess ->
                        JsonWebResponse(
                            dbSess.single(queryOf("SELECT 1 AS example"), { mapFromRow(it) }),
                        )
                    },
                )
                get(
                    "/single_user",
                    webResponseDb(dataSource) { dbSess ->
                        JsonWebResponse(
                            dbSess.single(
                                queryOf("SELECT * FROM user_t"),
                                { mapFromRow(it) },
                            )?.let(User.Companion::fromRow),
                        )
                    },
                )
                get("/html_test") {
                    call.respondHtml {
                        head {
                            title("Hello, World!")
                            styleLink("/app.css")
                        }
                        body {
                            h1 { +"Hello, World!" }
                        }
                    }
                }
                get("/html_template_test") {
                    call.respondHtmlTemplate(AppLayout("Hello, world!")) {
                        pageBody {
                            h1 {
                                +"Hello, World!"
                            }
                        }
                    }
                }
                get(
                    "/html_webresponse_test1",
                    webResponse {
                        HtmlWebResponse(
                            AppLayout("Hello, world!").apply {
                                pageBody {
                                    h1 {
                                        +"Hello, readers!"
                                    }
                                }
                            })
                    })
                get(
                    "/html_webresponse_test2",
                    webResponse {
                        HtmlWebResponse(
                            object : Template<HTML> {
                                override fun HTML.apply() {
                                    head {
                                        title { +"Plain HTML here! " }
                                    }
                                    body {
                                        h1 { +"Very plan header" }
                                    }
                                }
                            })
                    })
                get(
                    "/login",
                    webResponse {
                        HtmlWebResponse(
                            AppLayout("Log in").apply {
                                pageBody {
                                    form(method = FormMethod.post, action = "/login") {
                                        p {
                                            label { +"E-mail" }
                                            input(type = InputType.text, name = "username")
                                        }
                                        p {
                                            label { +"Password" }
                                            input(type = InputType.password, name = "password")
                                        }
                                        button(type = ButtonType.submit) { +"Log in" }
                                    }
                                }
                            })
                    })
                post("/login") {
                    sessionOf(dataSource).use { dbSess ->
                        val params = call.receiveParameters()
                        val userId =
                            authenticateUser(
                                dbSess,
                                params["username"]!!,
                                params["password"]!!
                            )
                        if (userId == null) {
                            call.respondRedirect("/login")
                        } else {
                            call.sessions.set(UserSession(userId = userId))
                            call.respondRedirect("/secret")
                        }
                    }
                }
                authenticate("auth-session") {
                    get(
                        "/secret",
                        webResponseDb(dataSource) { dbSess ->
                            val userSession = call.principal<UserSession>()!!
                            val user = getUser(dbSess, userSession.userId)!!
                            HtmlWebResponse(
                                AppLayout("Welcome, ${user.email}").apply {
                                    pageBody {
                                        h1 {
                                            +"Hello there, ${user.email}"
                                        }
                                        p { +"You're logged in." }
                                        p {
                                            a(href = "/logout") { +"Log out" }
                                        }
                                    }
                                }
                            )
                        })
                    get("/logout") {
                        call.sessions.clear<UserSession>()
                        call.respondRedirect("/login")
                    }
                }
                post(
                    "/test_json",
                    webResponse {
                        either<ValidationError, MyUser> {
                            val input =
                                Gson().fromJson(
                                    call.receiveText(), Map::class.java
                                )
                            MyUser(
                                email = validateEmail(input["email"]).bind(),
                                password = validatePassword(input["password"]).bind()
                            )
                        }.fold(
                            { err ->
                                JsonWebResponse(
                                    mapOf("error" to err.error),
                                    statusCode = 422
                                )
                            },
                            { user ->
                                JsonWebResponse(mapOf("success" to true))
                            }
                        )
                    })
            }
        }

        private fun getClicheMessage(): String {
            return "Hello, World! Class=" +
                Application::class.java + " message=" +
                ExampleAbc.greet("test")
        }

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
    }

    @Suppress("detekt.UnusedPrivateMember", "detekt.FunctionOnlyReturningConstant")
    private fun noOp(): String {
        return ""
    }
}
