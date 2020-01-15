package com.ivianuu.essentials.composehelpers

import androidx.compose.composer

inline fun <T> exec(block: () -> T): T = block()

inline fun <T> expr(key: Any, block: () -> T): T {
    return with(composer) {
        startExpr(key)
        val result = block()
        endExpr()
        result
    }
}
