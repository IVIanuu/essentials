package com.ivianuu.essentials.android.text

import com.ivianuu.essentials.util.unsafeLazy
import kotlin.reflect.KProperty

/**
 * Namespace for app strings
 *
 * Example usage:
 *
 * ´´´
 * val Strings.AppName by lazyString { "Hello World App" }
 * val Strings.Greeting by lazyResolvableString { (userName: String) ->
 *     "Hello $userName"
 * }
 * ´´´
 *
 */
object Strings {
    object Essentials
}

fun lazyString(init: () -> String) = unsafeLazy(init)

fun lazyResolvableString(block: (Args) -> String) =
    unsafeLazy { ResolveableString(block) }

fun ResolveableString(block: (Args) -> String): ResolvableString {
    return object : ResolvableString {
        override fun invoke(args: Args): String = block(args)
    }
}

interface ResolvableString {
    operator fun invoke(args: Args): String
    operator fun invoke(vararg args: Any?): String = invoke(
        argsOf(
            *args
        )
    )
    operator fun getValue(thisRef: Any?, property: KProperty<*>): String = invoke()
}

class Args(private val values: Array<out Any?>) {

    val size: Int get() = values.size

    operator fun <T> get(index: Int): T = values[index] as T

    fun <T> getOrNull(index: Int): T? = values.getOrNull(index) as? T

    operator fun <T> component1(): T = get(0)
    operator fun <T> component2(): T = get(1)
    operator fun <T> component3(): T = get(2)
    operator fun <T> component4(): T = get(3)
    operator fun <T> component5(): T = get(4)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Args) return false

        if (!values.contentEquals(other.values)) return false

        return true
    }

    override fun hashCode(): Int = values.contentHashCode()

    override fun toString(): String = values.contentToString()
}

fun argsOf(): Args =
    emptyArgs()

fun argsOf(vararg values: Any?): Args =
    Args(values)

fun argsOf(values: List<Any?>): Args =
    Args(values.toTypedArray())

fun emptyArgs(): Args =
    Args(emptyArray())
