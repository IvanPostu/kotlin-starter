package com.iv127.kotlin.starter

sealed class WebResponse {
    abstract val statusCode: Int
    abstract val headers: Map<String, List<String>>
    abstract fun copyResponse(
        statusCode: Int,
        headers: Map<String, List<String>>
    ): WebResponse

    fun header(headerName: String, headerValue: String) = header(headerName, listOf(headerValue))

    fun header(headerName: String, headerValues: List<String>) = copyResponse(
        statusCode,
        headers.plus(
            Pair(
                headerName,
                headers.getOrDefault(headerName, listOf())
                    .plus(headerValues)
            )
        )
    )
}
