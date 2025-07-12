package com.iv127.kotlin.starter.app

import at.favre.lib.crypto.bcrypt.BCrypt
import kotliquery.Session
import kotliquery.queryOf

const val BCRYPT_DIFFICULTY_FACTOR = 10

val bcryptHasher = BCrypt.withDefaults()
val bcryptVerifier = BCrypt.verifyer()

fun authenticateUser(
    dbSession: Session,
    email: String,
    passwordText: String
): Long? {
    return dbSession.single(
        queryOf("SELECT * FROM user_t WHERE email = ?", email),
        ::mapFromRow
    )?.let {
        val pwHash = it["password_hash"] as ByteArray
        if (bcryptVerifier.verify(
                passwordText.toByteArray(Charsets.UTF_8),
                pwHash
            ).verified
        ) {
            return it["id"] as Long
        } else {
            return null
        }
    }
}

fun createUser(
    dbSession: Session,
    email: String,
    name: String,
    passwordText: String,
    tosAccepted: Boolean = false,
): Long {
    val hashedPassword: ByteArray =
        bcryptHasher.hash(
            BCRYPT_DIFFICULTY_FACTOR,
            passwordText.toByteArray(Charsets.UTF_8)
        )
    val userId =
        dbSession.updateAndReturnGeneratedKey(
            queryOf(
                """
                            INSERT INTO user_t
                            (email, name, tos_accepted, password_hash)
                            VALUES (:email, :name, :tosAccepted, :passwordHash)
                            """,
                mapOf(
                    "email" to email,
                    "name" to name,
                    "tosAccepted" to tosAccepted,
                    "passwordHash" to hashedPassword
//                        passwordText
//                            .toByteArray(Charsets.UTF_8),
                ),
            ),
        )
    return userId!!
}

fun listUsers(dbSession: Session) =
    dbSession
        .list(queryOf("SELECT * FROM user_t"), ::mapFromRow)
        .map(User.Companion::fromRow)

fun getUser(
    dbSession: Session,
    id: Long,
): User? {
    return dbSession
        .single(
            queryOf("SELECT * FROM user_t WHERE id = ?", id),
            ::mapFromRow,
        )
        ?.let(User.Companion::fromRow)
}
