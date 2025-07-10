package com.iv127.kotlin.starter.blackbox

import com.iv127.kotlin.starter.app.EnvironmentType
import com.iv127.kotlin.starter.app.createAndMigrateDataSource
import com.iv127.kotlin.starter.app.createAppConfig
import com.iv127.kotlin.starter.app.mapFromRow
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotliquery.Session
import kotliquery.TransactionalSession
import kotliquery.queryOf
import kotliquery.sessionOf
import org.slf4j.LoggerFactory
import kotlin.test.Test

class HandleCoroutineTest {
    companion object {
        private val LOG = LoggerFactory.getLogger(HandleCoroutineTest::class.java)
        private val testAppConfig = createAppConfig(EnvironmentType.TEST)
        private val testDataSource = createAndMigrateDataSource(testAppConfig)
    }

    @Test
    fun testCoroutineHandle() {
        runBlocking {
            testTx { dbSess ->
                handleCoroutine(dbSess)
            }
        }
    }

    suspend fun handleCoroutine(
        dbSess: Session
    ) = coroutineScope {
        val client = HttpClient(CIO)
        val randomNumberRequest = async {
            client.get("http://localhost:9876/random_number")
                .bodyAsText()
        }
        val reverseRequest = async {
            client.post("http://localhost:9876/reverse") {
                setBody(randomNumberRequest.await())
            }.bodyAsText()
        }
        val queryOperation = async {
            val pingPong = client.get("http://localhost:9876/ping")
                .bodyAsText()

            withContext(Dispatchers.IO) {
                dbSess.single(
                    queryOf(
                        "SELECT count(*) c from user_t WHERE email != ?",
                        pingPong
                    ),
                    { mapFromRow(it) })
            }
        }
        LOG.info(
            """
Random number: ${randomNumberRequest.await()}
Reversed: ${reverseRequest.await()}
Query: ${queryOperation.await()}
"""
        )
    }

    private suspend fun testTx(handler: suspend (dbSess: TransactionalSession) -> Unit) = coroutineScope {
        sessionOf(
            testDataSource,
            returnGeneratedKey = true,
        ).use { dbSess ->
            dbSess.transaction { dbSessTx ->
                try {
                    handler(dbSessTx)
                } finally {
                    dbSessTx.connection.rollback()
                }
            }
        }
    }
}
