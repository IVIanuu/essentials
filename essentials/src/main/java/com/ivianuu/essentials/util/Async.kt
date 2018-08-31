package com.ivianuu.essentials.util

import java.util.*

/**
 * The T generic is unused for some classes but since it is sealed and useful for Success and Fail,
 * it should be on all of them.
 *
 * Complete: Success, Fail
 * ShouldLoad: Uninitialized, Fail
 */
sealed class Async<out T>(val complete: Boolean, val shouldLoad: Boolean) {
    open operator fun invoke(): T? = null
}

object Uninitialized : Async<Nothing>(complete = false, shouldLoad = true), Incomplete

class Loading<out T> : Async<T>(complete = false, shouldLoad = false), Incomplete {
    override fun equals(other: Any?) = other is Loading<*>
    override fun hashCode() = "Loading".hashCode()
}

data class Success<out T>(private val value: T) : Async<T>(complete = true, shouldLoad = false) {
    override operator fun invoke(): T = value
}

data class Fail<out T>(val error: Throwable) : Async<T>(complete = true, shouldLoad = true) {
    override fun equals(other: Any?): Boolean {
        if (other !is Fail<*>) return false

        val otherError = other.error
        return error::class == otherError::class &&
                error.message == otherError.message &&
                error.stackTrace[0] == otherError.stackTrace[0]
    }

    override fun hashCode(): Int =
        Arrays.hashCode(arrayOf(error::class, error.message, error.stackTrace[0]))
}

interface Incomplete