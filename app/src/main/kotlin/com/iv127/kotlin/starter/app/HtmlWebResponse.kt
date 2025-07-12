package com.iv127.kotlin.starter.app

import io.ktor.server.html.Template
import kotlinx.html.HTML

data class HtmlWebResponse(
    val body: Template<HTML>,
    override val statusCode: Int = 200,
    override val headers: Map<String, List<String>> = mapOf(),
) : WebResponse() {
    override fun copyResponse(
        statusCode: Int,
        headers: Map<String, List<String>>
    ) =
        copy(body, statusCode, headers)
}
