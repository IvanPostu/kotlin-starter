package com.iv127.kotlin.starter.app.jooby

import com.google.gson.Gson
import com.iv127.kotlin.starter.app.AppLayout
import com.iv127.kotlin.starter.app.EnvironmentType
import com.iv127.kotlin.starter.app.HtmlWebResponse
import com.iv127.kotlin.starter.app.JsonWebResponse
import com.iv127.kotlin.starter.app.TextWebResponse
import com.iv127.kotlin.starter.app.WebResponse
import com.iv127.kotlin.starter.app.authenticateUser
import com.iv127.kotlin.starter.app.createAndMigrateDataSource
import com.iv127.kotlin.starter.app.createAppConfig
import com.iv127.kotlin.starter.app.findUser
import com.iv127.kotlin.starter.app.getUser
import io.jooby.Cookie
import io.jooby.HandlerContext
import io.jooby.MediaType
import io.jooby.SameSite
import io.jooby.SessionStore
import io.jooby.internal.ClassPathAssetSource
import io.jooby.runApp
import kotlinx.coroutines.delay
import kotlinx.html.ButtonType
import kotlinx.html.FormMethod
import kotlinx.html.InputType
import kotlinx.html.button
import kotlinx.html.form
import kotlinx.html.html
import kotlinx.html.input
import kotlinx.html.label
import kotlinx.html.p
import kotlinx.html.stream.appendHTML
import kotliquery.Session
import kotliquery.sessionOf
import javax.sql.DataSource
import kotlin.time.Duration

private const val DELAY_MS: Long = 300L

@Suppress("LongMethod")
fun main(args: Array<String>) {

    val env = EnvironmentType.valueOf(System.getenv("APPLICATION_ENV") ?: EnvironmentType.LOCAL.name)
    val config = createAppConfig(env)
    val dataSource = createAndMigrateDataSource(config)
    runApp(args) {
        serverOptions {
            port = config.httpPort
            server = "netty"
        }
        sessionStore =
            SessionStore.signed(
                config.cookieSigningKey,
                Cookie("joobyCookie")
                    .setMaxAge(Duration.parse("30d").inWholeSeconds)
                    .setHttpOnly(true)
                    .setPath("/")
                    .setSecure(config.useSecureCookie)
                    .setSameSite(SameSite.LAX)
            )
        coroutine {
            get("/") {
                "Hello, World!"
            }
            get("/", joobyWebResponse {
                delay(DELAY_MS)
                TextWebResponse("Hello, World!")
            })
            post("/db_test", joobyWebResponseDb(dataSource) { dbSess ->
                val input = Gson().fromJson(
                    ctx.body(String::class.java), Map::class.java
                )
                val email = input["email"]?.toString().orEmpty()
                val response = findUser(dbSess, email)?.let { user ->
                    JsonWebResponse(user)
                } ?: JsonWebResponse(mapOf("message" to "user not found"))
                response
            })
            get("/login", joobyWebResponse {
                HtmlWebResponse(AppLayout("Log in").apply {
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
                    val formData = ctx.form()
                    val userId = authenticateUser(
                        dbSess,
                        formData["username"].value(),
                        formData["password"].value()
                    )
                    if (userId == null) {
                        ctx.sendRedirect("/login")
                    } else {
                        ctx.session().put("userId", userId)
                        ctx.sendRedirect("/secret")
                    }
                }
            }
            path("") {
                decorator {
                    val userId = ctx.session().get("userId").valueOrNull()
                    if (userId == null) {
                        ctx.sendRedirect("/login")
                    } else {
                        ctx.attribute("userId", userId.toLong())
                        next.apply(ctx)
                    }
                }
                get("/secret", joobyWebResponseDb(dataSource) { dbSess ->
                    val user = getUser(
                        dbSess,
                        ctx.attribute<Long>("userId")!!
                    )!!
                    TextWebResponse("Hello, ${user.email}!")
                })
                get("/logout") {
                    ctx.session().destroy()
                    ctx.sendRedirect("/")
                }
            }
        }

        if (config.useFileSystemAssets) {
            assets("/*", "app/src/main/resources/public")
        } else {
            assets("/*", ClassPathAssetSource(this.classLoader, "public"))
        }
    }
}

fun joobyWebResponse(
    handler: suspend HandlerContext.() -> WebResponse
): suspend HandlerContext.() -> Any {
    return {
        val resp = this.handler()
        ctx.setResponseCode(resp.statusCode)
        for ((name, values) in resp.headers())
            for (value in values)
                ctx.setResponseHeader(name, value)
        when (resp) {
            is TextWebResponse -> {
                ctx.responseType = MediaType.text
                resp.body
            }

            is JsonWebResponse -> {
                ctx.responseType = MediaType.json
                Gson().toJson(resp.body)
            }

            is HtmlWebResponse -> {
                ctx.responseType = MediaType.html
                buildString {
                    appendHTML().html {
                        with(resp.body) { apply() }
                    }
                }
            }
        }
    }
}

fun joobyWebResponseDb(
    dataSource: DataSource,
    handler: suspend HandlerContext.(
        dbSess: Session
    ) -> WebResponse
) = joobyWebResponse {
    sessionOf(
        dataSource,
        returnGeneratedKey = true
    ).use { dbSess ->
        handler(dbSess)
    }
}
