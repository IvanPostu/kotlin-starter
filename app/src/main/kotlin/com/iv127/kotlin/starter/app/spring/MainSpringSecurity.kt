package com.iv127.kotlin.starter.app.spring

import com.iv127.kotlin.starter.app.Application
import com.iv127.kotlin.starter.app.Application.Companion.createKtorApplication
import com.iv127.kotlin.starter.app.EnvironmentType
import com.iv127.kotlin.starter.app.WebappConfig
import com.iv127.kotlin.starter.app.createAndMigrateDataSource
import com.iv127.kotlin.starter.app.createAppConfig
import com.iv127.kotlin.starter.app.spring.security.WebappSecurityConfig
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
import org.springframework.beans.factory.support.DefaultListableBeanFactory
import org.springframework.context.annotation.AnnotatedBeanDefinitionReader
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl
import org.springframework.web.context.support.AbstractRefreshableWebApplicationContext
import org.springframework.web.filter.DelegatingFilterProxy
import javax.servlet.ServletContextEvent
import javax.servlet.ServletContextListener
import javax.servlet.annotation.WebListener

private val LOG = LoggerFactory.getLogger(Application::class.java)


fun main() {
    val env = EnvironmentType.valueOf(System.getenv("APPLICATION_ENV") ?: EnvironmentType.LOCAL.name)
    val appConfig = createAppConfig(env)
    val server = Server()
    val connector =
        ServerConnector(
            server,
            HttpConnectionFactory()
        )
    connector.port = appConfig.httpPort
    server.addConnector(connector)
    server.handler =
        ServletContextHandler(
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
        val appConfig =
            ctx.getAttribute(
                "appConfig"
            ) as WebappConfig
        LOG.debug("Setting up data source")
        val dataSource = createAndMigrateDataSource(appConfig)
        LOG.debug("Setting up Ktor servlet environment")
        val appEngineEnvironment =
            applicationEngineEnvironment {
                module {
                    createKtorApplication(appConfig, dataSource)
                }
            }
        val appEnginePipeline =
            defaultEnginePipeline(
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

        LOG.debug("Setting up Spring Security")
        val roleHierarchy = """
ROLE_ADMIN > ROLE_USER
"""
        val wac =
            object : AbstractRefreshableWebApplicationContext() {
                override fun loadBeanDefinitions(
                    beanFactory: DefaultListableBeanFactory
                ) {
                    beanFactory.registerSingleton(
                        "dataSource",
                        dataSource
                    )
                    beanFactory.registerSingleton(
                        "rememberMeKey",
                        "asdf"
                    )
                    beanFactory.registerSingleton(
                        "roleHierarchy",
                        RoleHierarchyImpl().apply {
                            setHierarchy(roleHierarchy)
                        }
                    )
                    AnnotatedBeanDefinitionReader(beanFactory)
                        .register(WebappSecurityConfig::class.java)
                }
            }

        wac.servletContext = ctx
        ctx.addFilter(
            "springSecurityFilterChain",
            DelegatingFilterProxy("springSecurityFilterChain", wac)
        ).apply {
            addMappingForServletNames(null, false, "ktorServlet")
        }
    }

    @Suppress("EmptyFunctionBlock")
    override fun contextDestroyed(sce: ServletContextEvent) {
    }
}
