package com.iv127.kotlin.starter.app

import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway
import javax.sql.DataSource

fun createAndMigrateDataSource(config: WebappConfig) = createDataSource(config).also(::migrateDataSource)

fun migrateDataSource(dataSource: DataSource) {
    Flyway.configure()
        .dataSource(dataSource)
        .locations("db/migration")
        .table("flyway_schema_history")
        .load()
        .migrate()
}

private fun createDataSource(config: WebappConfig) =
    HikariDataSource().apply {
        jdbcUrl = config.dbUrl
        username = config.dbUser
        password = config.dbPassword
    }

