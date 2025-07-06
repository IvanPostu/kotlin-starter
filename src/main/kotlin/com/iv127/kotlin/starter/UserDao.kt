package com.iv127.kotlin.starter

import kotliquery.Row
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

        fun listUsers(dbSession: Session) =
            dbSession
                .list(queryOf("SELECT * FROM user_t"), ::mapFromRow)
                .map(User::fromRow)

        fun getUser(dbSession: Session, id: Long): User? {
            return dbSession
                .single(
                    queryOf("SELECT * FROM user_t WHERE id = ?", id),
                    ::mapFromRow
                )
                ?.let(User::fromRow)
        }

        private fun mapFromRow(row: Row): Map<String, Any?> {
            return row.underlying.metaData
                .let { (1..it.columnCount).map(it::getColumnName) }
                .map { it to row.anyOrNull(it) }
                .toMap()
        }
    }
}