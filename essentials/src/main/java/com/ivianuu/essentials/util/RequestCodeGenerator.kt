package com.ivianuu.essentials.util

import java.util.concurrent.atomic.AtomicInteger

/**
 * Request code generator
 */
object RequestCodeGenerator {

    private val codes = AtomicInteger(5000)

    fun generate() = codes.incrementAndGet()

}