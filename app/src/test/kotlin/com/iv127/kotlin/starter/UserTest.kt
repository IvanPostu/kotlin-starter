package com.iv127.kotlin.starter

import com.iv127.kotlin.starter.app.EnvironmentType
import com.iv127.kotlin.starter.app.createAndMigrateDataSource
import com.iv127.kotlin.starter.app.createAppConfig
import com.iv127.kotlin.starter.app.createUser
import com.iv127.kotlin.starter.app.getUser
import com.iv127.kotlin.starter.app.listUsers
import kotliquery.TransactionalSession
import kotliquery.sessionOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class UserTest {
    companion object {
        private val testAppConfig = createAppConfig(EnvironmentType.TEST)
        private val testDataSource = createAndMigrateDataSource(testAppConfig)
    }

    @Test
    fun testCreateUser() {
        testTx { dbSess ->
            val userAId =
                createUser(
                    dbSess,
                    email = "test1@me.com",
                    name = "First Last",
                    passwordText = "1234",
                )
            val userBId =
                createUser(
                    dbSess,
                    email = "test2@me.com",
                    name = "First Last",
                    passwordText = "1234",
                )
            assertNotEquals(userAId, userBId)
        }
    }

    @Test
    fun testCreateAnotherUser() {
        testTx { dbSess ->
            val userId =
                createUser(
                    dbSess,
                    email = "test1@me.com",
                    name = "First Last",
                    passwordText = "1234",
                )
            assertNotNull(userId)
        }
    }

    @Test
    fun testListUsers() {
        testTx { dbSess ->
            val userAId =
                createUser(
                    dbSess,
                    email = "test1@me.com",
                    name = "Example",
                    passwordText = "1234",
                )
            val userBId =
                createUser(
                    dbSess,
                    email = "test2@me.com",
                    name = "Example",
                    passwordText = "1234",
                )
            val users = listUsers(dbSess)
            assertNotNull(users.find { it.id == userAId })
            assertNotNull(users.find { it.id == userBId })
        }
    }

    @Test
    fun testGetUser() {
        testTx { dbSess ->
            val userId =
                createUser(
                    dbSess,
                    email = "test1@me.com",
                    name = "Example",
                    passwordText = "1234",
                )
            assertNull(getUser(dbSess, -9000))
            val user = getUser(dbSess, userId)
            assertNotNull(user)
            assertEquals(user.email, "test1@me.com")
        }
    }

    private fun testTx(handler: (dbSess: TransactionalSession) -> Unit) {
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
