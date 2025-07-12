package com.iv127.kotlin.starter

import com.iv127.kotlin.starter.app.EnvironmentType
import com.iv127.kotlin.starter.app.authenticateUser
import com.iv127.kotlin.starter.app.createAndMigrateDataSource
import com.iv127.kotlin.starter.app.createAppConfig
import com.iv127.kotlin.starter.app.createUser
import com.iv127.kotlin.starter.app.getUser
import com.iv127.kotlin.starter.app.listUsers
import com.iv127.kotlin.starter.app.mapFromRow
import kotliquery.TransactionalSession
import kotliquery.queryOf
import kotliquery.sessionOf
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class UserTest {
    companion object {
        private val testAppConfig = createAppConfig(EnvironmentType.TEST)
        private val testDataSource = createAndMigrateDataSource(testAppConfig)
    }

    @Test
    fun testVerifyUserPassword() = testTx { dbSess ->
        val userId = createUser(
            dbSess,
            email = "abc@b.com",
            name = "Test User",
            passwordText = "1234",
            tosAccepted = true
        )
        assertEquals(
            userId,
            authenticateUser(dbSess, "abc@b.com", "1234")
        )
        assertEquals(
            null,
            authenticateUser(dbSess, "abc@b.com", "incorrect")
        )
        assertEquals(
            null,
            authenticateUser(dbSess, "does@not.exist", "1234")
        )
    }

    @Test
    fun testUserPasswordSalting() = testTx { dbSess ->
        val userAId = createUser(
            dbSess,
            email = "a@b.com",
            name = "A",
            passwordText = "1234",
            tosAccepted = true
        )
        val userBId = createUser(
            dbSess,
            email = "x@b.com",
            name = "X",
            passwordText = "1234",
            tosAccepted = true
        )
        val userAHash = dbSess.single(
            queryOf("SELECT * FROM user_t WHERE id = ?", userAId),
            ::mapFromRow
        )!!["password_hash"] as ByteArray
        val userBHash = dbSess.single(
            queryOf("SELECT * FROM user_t WHERE id = ?", userBId),
            ::mapFromRow
        )!!["password_hash"] as ByteArray
        assertFalse(Arrays.equals(userAHash, userBHash))
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
