package com.iv127.kotlin.starter.app.spring

import com.iv127.kotlin.starter.app.Application
import com.iv127.kotlin.starter.app.Application.Companion.createKtorApplication
import com.iv127.kotlin.starter.app.EnvironmentType
import com.iv127.kotlin.starter.app.WebappConfig
import com.iv127.kotlin.starter.app.createAppConfigUsingTypesafe
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.config.BeanDefinitionCustomizer
import org.springframework.beans.factory.config.RuntimeBeanReference
import org.springframework.context.support.StaticApplicationContext
import javax.sql.DataSource

private val LOG = LoggerFactory.getLogger(Application::class.java)

fun main() {
    LOG.debug("Starting application...")
    val env = EnvironmentType.valueOf(System.getenv("APPLICATION_ENV") ?: EnvironmentType.LOCAL.name)
    LOG.debug("Application runs in the environment {}", env)
    val config = createAppConfigUsingTypesafe(env)
    LOG.debug("Creating app context")
    val ctx = createApplicationContext(config)
    LOG.debug("Getting data source")
    val dataSource =
        ctx.getBean("dataSource", DataSource::class.java)
    embeddedServer(Netty, port = config.httpPort) {
        createKtorApplication(config, dataSource)
    }.start(wait = true)
}

private fun createApplicationContext(appConfig: WebappConfig) =
    StaticApplicationContext().apply {
        beanFactory.registerSingleton("appConfig", appConfig)
        registerBean(
            "unmigratedDataSource",
            HikariDataSource::class.java,
            BeanDefinitionCustomizer { bd ->
                bd.propertyValues.apply {
                    add("jdbcUrl", appConfig.dbUrl)
                    add("username", appConfig.dbUser)
                    add("password", appConfig.dbPassword)
                }
            }
        )
        registerBean(
            "dataSource",
            MigratedDataSourceFactoryBean::class.java,
            BeanDefinitionCustomizer { bd ->
                bd.propertyValues.apply {
                    add(
                        "unmigratedDataSource",
                        RuntimeBeanReference("unmigratedDataSource")
                    )
                }
            }
        )
        refresh()
        registerShutdownHook()
    }
