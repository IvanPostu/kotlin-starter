package com.iv127.kotlin.starter.app

import io.ktor.server.auth.Principal

data class UserSession(val userId: Long) : Principal
