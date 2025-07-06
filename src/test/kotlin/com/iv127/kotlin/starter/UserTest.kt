package com.iv127.kotlin.starter

import com.iv127.kotlin.starter.UserDao.Companion.createUser
import kotliquery.TransactionalSession
import kotliquery.sessionOf
import kotlin.test.Test
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

class UserTest {

    companion object {
        val testAppConfig = AppConfigProvider.createAppConfig(EnvironmentType.TEST)
        val testDataSource = createAndMigrateDataSource(testAppConfig)
    }

    @Test
    fun testCreateUser() {
        testTx { dbSess ->
            val userAId = createUser(
                dbSess,
                email = "test1@me.com",
                name = "First Last",
                passwordText = "1234"
            )
            val userBId = createUser(
                dbSess,
                email = "test2@me.com",
                name = "First Last",
                passwordText = "1234"
            )
            assertNotEquals(userAId, userBId)
        }
    }

    @Test
    fun testCreateAnotherUser() {
        testTx { dbSess ->
            val userId = createUser(
                dbSess,
                email = "test1@me.com",
                name = "First Last",
                passwordText = "1234"
            )
            assertNotNull(userId)
        }
    }

    fun testTx(handler: (dbSess: TransactionalSession) -> Unit) {
        sessionOf(
            testDataSource,
            returnGeneratedKey = true
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