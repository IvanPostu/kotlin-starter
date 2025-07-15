package com.iv127.kotlin.starter.app.spring

import com.iv127.kotlin.starter.app.Application
import com.iv127.kotlin.starter.app.Application.Companion.createKtorApplication
import com.iv127.kotlin.starter.app.EnvironmentType
import com.iv127.kotlin.starter.app.WebappConfig
import com.iv127.kotlin.starter.app.createAndMigrateDataSource
import com.iv127.kotlin.starter.app.createAppConfig
import io.ktor.server.application.ApplicationStarting
import io.ktor.server.engine.BaseApplicationResponse
import io.ktor.server.engine.applicationEngineEnvironment
import io.ktor.server.engine.defaultEnginePipeline
import io.ktor.server.engine.installDefaultTransformations
import io.ktor.server.servlet.ServletApplicationEngine
import org.eclipse.jetty.server.HttpConnectionFactory
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.ServerConnector
import org.eclipse.jetty.servlet.ListenerHolder
import org.eclipse.jetty.servlet.ServletContextHandler
import org.slf4j.LoggerFactory
import javax.servlet.ServletContextEvent
import javax.servlet.ServletContextListener
import javax.servlet.annotation.WebListener

private val LOG = LoggerFactory.getLogger(Application::class.java)


fun main() {
    val env = EnvironmentType.valueOf(System.getenv("APPLICATION_ENV") ?: EnvironmentType.LOCAL.name)
    val appConfig = createAppConfig(env)
    val server = Server()
    val connector = ServerConnector(
        server,
        HttpConnectionFactory()
    )
    connector.port = appConfig.httpPort
    server.addConnector(connector)
    server.handler = ServletContextHandler(
        // enable sessions support, required for Spring Security
        ServletContextHandler.SESSIONS
    ).apply {
        contextPath = "/"
        resourceBase = System.getProperty("java.io.tmpdir")
        servletContext.setAttribute("appConfig", appConfig)
        servletHandler.addListener(
            ListenerHolder(BootstrapWebApp::class.java)
        )
    }
    server.start()
    server.join()
}

// @WebListener is needed for servlet containers like tomcat
// which doesn't have any explicit main methods
// for jetty it is not required
@WebListener
class BootstrapWebApp : ServletContextListener {
    override fun contextInitialized(sce: ServletContextEvent) {
        val ctx = sce.servletContext
        LOG.debug("Extracting config")
        val appConfig = ctx.getAttribute(
            "appConfig"
        ) as WebappConfig
        LOG.debug("Setting up data source")
        val dataSource = createAndMigrateDataSource(appConfig)
        LOG.debug("Setting up Ktor servlet environment")
        val appEngineEnvironment = applicationEngineEnvironment {
            module {
                createKtorApplication(appConfig, dataSource)
            }
        }
        val appEnginePipeline = defaultEnginePipeline(
            appEngineEnvironment
        )
        BaseApplicationResponse.setupSendPipeline(
            appEnginePipeline.sendPipeline
        )
        appEngineEnvironment.monitor.subscribe(
            ApplicationStarting
        ) {
            it.receivePipeline.merge(appEnginePipeline.receivePipeline)
            it.sendPipeline.merge(appEnginePipeline.sendPipeline)
            it.receivePipeline.installDefaultTransformations()
            it.sendPipeline.installDefaultTransformations()
        }
        ctx.setAttribute(
            ServletApplicationEngine
                .ApplicationEngineEnvironmentAttributeKey,
            appEngineEnvironment
        )
        LOG.debug("Setting up Ktor servlet")
        ctx.addServlet(
            "ktorServlet",
            ServletApplicationEngine::class.java
        ).apply {
            addMapping("/")
        }
    }

    @Suppress("EmptyFunctionBlock")
    override fun contextDestroyed(sce: ServletContextEvent) {
    }
}
