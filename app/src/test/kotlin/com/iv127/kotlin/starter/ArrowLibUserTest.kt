package com.iv127.kotlin.starter

import com.iv127.kotlin.starter.app.signUpUser
import kotlinx.coroutines.runBlocking
import kotlin.test.Test

class ArrowLibUserTest {

    @Test
    fun testSignUp() {
        runBlocking {
            signUpUser("foo@bar.com", "test")
                .fold({ err -> println(err)}, { user -> println(user)})
// MyUser(email=foo@bar.com, password=test)
            signUpUser("foo", "test")
                .fold({ err -> println(err)}, { user -> println(user)})
// ValidationError(error=Invalid e-mail)
            signUpUser("foo@bar.com", "1234")
                .fold({ err -> println(err)}, { user -> println(user)})
// ValidationError(error=Insecure password)
        }

    }

}
