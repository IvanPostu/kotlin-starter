package com.iv127.kotlin.starter.coroutine

import com.iv127.kotlin.starter.blackbox.HandleCoroutineTest
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.createCoroutineUnintercepted
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.resume
import kotlin.test.Test

class ContinuationTest {
    private val LOG = LoggerFactory.getLogger(HandleCoroutineTest::class.java)

    @Test
    fun testContinuation() {
        runBlocking {
            val cont = createContinuation()
                .createCoroutineUnintercepted(
                    Continuation(EmptyCoroutineContext, {})
                )
            cont.resume(Unit)
// a
            cont.resume(Unit)
// b
// c
            cont.resume(Unit)
// d
//            cont.resume(Unit)
// d
        }
    }

    suspend fun haltHere() =
        suspendCoroutineUninterceptedOrReturn<Unit> { cont ->
            COROUTINE_SUSPENDED
        }

    fun createContinuation(): suspend () -> Unit {
        return {
            println("a")
            haltHere()
            println("b")
            println("c")
            haltHere()
            println("d")
        }
    }
}
