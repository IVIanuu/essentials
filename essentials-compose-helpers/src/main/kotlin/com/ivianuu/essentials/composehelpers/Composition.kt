package com.ivianuu.essentials.composehelpers

import androidx.compose.Composable
import androidx.compose.Composer
import androidx.compose.ComposerAccessor
import androidx.compose.ScopeUpdateScope
import androidx.compose.ViewValidator
import androidx.compose.cache

val composition: Composition get() = Composition(ComposerAccessor.getCurrentComposer())

inline class Composition(val composer: Composer<*>) {

    @Suppress("NOTHING_TO_INLINE")
    inline fun joinKey(left: Any, right: Any?): Any = composer.joinKey(left, right)

    @Suppress("PLUGIN_WARNING")
    inline fun call(
        key: Any,
        /*crossinline*/
        invalid: ViewValidator.() -> Boolean,
        block: @Composable() () -> Unit
    ) = with(composer) {
        startGroup(key)
        if (ViewValidator(composer).invalid() || inserting) {
            startGroup(ComposerAccessor.getInvocation())
            block()
            endGroup()
        } else {
            skipCurrentGroup()
        }
        endGroup()
    }

    inline fun <T> expr(key: Any, block: () -> T): T = with(composer) {
        startGroup(key)
        val result = block()
        endGroup()
        result
    }

    inline fun <T> call(
        key: Any,
        /*crossinline*/
        ctor: () -> T,
        /*crossinline*/
        invalid: ViewValidator.(f: T) -> Boolean,
        block: (f: T) -> Unit
    ) = with(composer) {
        startGroup(key)
        val f = cache(true, ctor)
        if (ViewValidator(this).invalid(f) || inserting) {
            startGroup(ComposerAccessor.getInvocation())
            block(f)
            endGroup()
        } else {
            skipCurrentGroup()
        }
        endGroup()
    }

    fun startRestartGroup(key: Any) = composer.startRestartGroup(key)
    fun endRestartGroup(): ScopeUpdateScope? = composer.endRestartGroup()
}