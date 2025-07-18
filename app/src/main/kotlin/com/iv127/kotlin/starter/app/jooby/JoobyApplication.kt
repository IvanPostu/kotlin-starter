package com.iv127.kotlin.starter.app.jooby

import com.google.gson.Gson
import com.iv127.kotlin.starter.app.EnvironmentType
import com.iv127.kotlin.starter.app.HtmlWebResponse
import com.iv127.kotlin.starter.app.JsonWebResponse
import com.iv127.kotlin.starter.app.TextWebResponse
import com.iv127.kotlin.starter.app.WebResponse
import com.iv127.kotlin.starter.app.createAndMigrateDataSource
import com.iv127.kotlin.starter.app.createAppConfig
import com.iv127.kotlin.starter.app.findUser
import io.jooby.HandlerContext
import io.jooby.MediaType
import io.jooby.internal.ClassPathAssetSource
import io.jooby.runApp
import kotlinx.coroutines.delay
import kotlinx.html.html
import kotlinx.html.stream.appendHTML
import kotliquery.Session
import kotliquery.sessionOf
import javax.sql.DataSource

private const val DELAY_MS: Long = 300L

fun main(args: Array<String>) {

    val env = EnvironmentType.valueOf(System.getenv("APPLICATION_ENV") ?: EnvironmentType.LOCAL.name)
    val config = createAppConfig(env)
    val dataSource = createAndMigrateDataSource(config)
    runApp(args) {
        serverOptions {
            port = config.httpPort
            server = "netty"
        }
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
