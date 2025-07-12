package com.iv127.kotlin.starter.app

import io.ktor.util.hex
import java.security.SecureRandom

fun getRandomBytesHex(length: Int) =
    ByteArray(length)
        .also { SecureRandom().nextBytes(it) }
        .let(::hex)
