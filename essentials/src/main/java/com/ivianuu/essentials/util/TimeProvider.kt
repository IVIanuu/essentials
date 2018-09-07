package com.ivianuu.essentials.util

import javax.inject.Inject

/**
 * Provides the current time
 */
class TimeProvider @Inject constructor() {
    val currentTimeMillis get() = System.currentTimeMillis()
}