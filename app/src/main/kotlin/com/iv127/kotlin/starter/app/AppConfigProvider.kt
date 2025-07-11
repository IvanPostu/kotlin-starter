package com.iv127.kotlin.starter.app

import com.typesafe.config.ConfigFactory

fun createAppConfig(env: EnvironmentType): WebappConfig =
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
                useFileSystemAssets = it.getBoolean("useFileSystemAssets"),
                cookieEncryptionKey = it.getString("cookieEncryptionKey"),
                cookieSigningKey = it.getString("cookieSigningKey"),
                useSecureCookie = it.getBoolean("useSecureCookie"),
            )
        }
