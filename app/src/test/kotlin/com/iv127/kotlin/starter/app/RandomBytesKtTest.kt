package com.iv127.kotlin.starter.app

import org.slf4j.LoggerFactory
import kotlin.test.Test

class RandomBytesKtTest {
    companion object {
        private val LOG = LoggerFactory.getLogger(RandomBytesKtTest::class.java)
    }

    @Test
    fun testRandomBytes() {
        val randomBytesAsHex = getRandomBytesHex(32)
        LOG.info(randomBytesAsHex)
        kotlin.test.assertNotNull(randomBytesAsHex)
    }

}
