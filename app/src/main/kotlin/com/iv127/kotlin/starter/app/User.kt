package com.iv127.kotlin.starter.app

import java.time.OffsetDateTime

data class User(
    val id: Long,
    val createdAt: String,
    val updatedAt: String,
    val email: String,
    val tosAccepted: Boolean,
    val name: String?,
    val passwordHash: ByteArray,
) {
    companion object {
        fun fromRow(row: Map<String, Any?>) =
            User(
                id = row["id"] as Long,
                createdAt =
                    (row["created_at"] as OffsetDateTime)
                        .toString(),
                updatedAt =
                    (row["updated_at"] as OffsetDateTime)
                        .toString(),
                email = row["email"] as String,
                name = row["name"] as? String,
                tosAccepted = row["tos_accepted"] as Boolean,
                passwordHash = row["password_hash"] as ByteArray,
            )
    }
}
