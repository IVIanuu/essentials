package com.ivianuu.essentials.util

import java.util.concurrent.atomic.AtomicInteger

/**
 * Request code generator
 */
object RequestCodeGenerator {

    private val codes = AtomicInteger(0)

    fun generate() = codes.incrementAndGet()

}