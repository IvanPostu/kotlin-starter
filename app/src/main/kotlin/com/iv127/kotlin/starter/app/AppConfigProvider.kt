package com.iv127.kotlin.starter.app

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addResourceSource
import com.sksamuel.hoplite.preprocessor.EnvOrSystemPropertyPreprocessor
import com.typesafe.config.ConfigFactory

fun createAppConfigUsingTypesafe(env: EnvironmentType): WebappConfig =
    ConfigFactory
        .parseResources("app-${env.shortName}.conf")
        .withFallback(ConfigFactory.parseResources("app.conf"))
        .resolve()
        .let {
            WebappConfig(
                httpPort = it.getInt("httpPort"),
                dbUrl = it.getString("dbUrl"),
                dbUser = it.getString("dbUser"),
                dbPassword = it.getString("dbPassword"),
                useFileSystemAssets = it.getBoolean("useFileSystemAssets"),
                cookieEncryptionKey = it.getString("cookieEncryptionKey"),
                cookieSigningKey = it.getString("cookieSigningKey"),
                useSecureCookie = it.getBoolean("useSecureCookie"),
            )
        }

fun createAppConfigUsingHoplite(env: EnvironmentType): WebappConfig =
    ConfigLoaderBuilder.default()
        .addResourceSource("/app-${env.shortName}.conf")
        .addResourceSource("/app.conf")
        .addPreprocessor(EnvOrSystemPropertyPreprocessor)
        .build()
        .loadConfigOrThrow<WebappConfig>()
