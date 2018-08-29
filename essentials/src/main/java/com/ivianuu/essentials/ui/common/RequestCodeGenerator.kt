package com.ivianuu.essentials.ui.common

import java.util.concurrent.atomic.AtomicInteger

/**
 * Request code generator
 */
object RequestCodeGenerator {

    private val codes = AtomicInteger(0)

    fun generate() = codes.incrementAndGet()

}