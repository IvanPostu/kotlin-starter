package com.iv127.kotlin.starter.app

import kotlin.reflect.full.declaredMemberProperties

data class WebappConfig(
    val env: EnvironmentType,
    val httpPort: Int,
    val secretExample: String?,
    val dbUser: String,
    val dbPassword: String,
    val dbUrl: String,
    val useFileSystemAssets: Boolean,
    val cookieEncryptionKey: String,
    val cookieSigningKey: String,
    val useSecureCookie: Boolean,
) {
    override fun toString(): String {
        val secretsRegex =
            "password|secret|key"
                .toRegex(RegexOption.IGNORE_CASE)
        return WebappConfig::class.declaredMemberProperties
            .sortedBy { it.name }
            .map {
                val config = this
                if (secretsRegex.containsMatchIn(it.name)) {
                    "${it.name}=${it.get(config).toString().take(2)}*****"
                } else {
                    "${it.name}=${it.get(config)}"
                }
            }
            .joinToString(separator = "\n")
    }
}
