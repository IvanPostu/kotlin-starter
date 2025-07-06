package com.iv127.kotlin.starter

import kotliquery.Session
import kotliquery.queryOf

class UserDao {
    companion object {
        fun createUser(
            dbSession: Session,
            email: String,
            name: String,
            passwordText: String,
            tosAccepted: Boolean = false
        ): Long {
            val userId = dbSession.updateAndReturnGeneratedKey(
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
                        "passwordHash" to passwordText
                            .toByteArray(Charsets.UTF_8)
                    )
                )
            )
            return userId!!
        }
    }
}