package com.iv127.kotlin.starter.app.ktor

import com.iv127.kotlin.starter.app.JsonWebResponse
import com.iv127.kotlin.starter.app.KtorJsonWebResponse
import com.iv127.kotlin.starter.app.TextWebResponse
import com.iv127.kotlin.starter.app.WebResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.response.header
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.util.pipeline.PipelineContext
import io.ktor.util.pipeline.PipelineInterceptor

fun webResponse(handler: suspend PipelineContext<Unit, ApplicationCall>.() -> WebResponse): //
    PipelineInterceptor<Unit, ApplicationCall> {
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
