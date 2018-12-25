package com.ivianuu.injekt

/**
 * Parameters which will be used for assisted injection
 */
@Suppress("UNCHECKED_CAST")
class Parameters(vararg values: Any?) {

    val values: List<*> = values.toList()

    private fun <T> elementAt(i: Int): T =
        if (values.size > i) values[i] as T else throw IllegalArgumentException("Can't get parameter value #$i from $this")

    operator fun <T> component1(): T = elementAt(0)
    operator fun <T> component2(): T = elementAt(1)
    operator fun <T> component3(): T = elementAt(2)
    operator fun <T> component4(): T = elementAt(3)
    operator fun <T> component5(): T = elementAt(4)
    operator fun <T> component6(): T = elementAt(5)
    operator fun <T> component7(): T = elementAt(6)
    operator fun <T> component8(): T = elementAt(7)
    operator fun <T> component9(): T = elementAt(8)

    /**
     * get element at given index
     * return T
     */
    operator fun <T> get(i: Int) = values[i] as T

    /**
     * Get first element of given type T
     * return T
     */
    inline fun <reified T> get() = values.first { it is T }

}

/**
 * Returns new [Parameters] which contains all [values]
 */
fun parametersOf(vararg values: Any?) = Parameters(*values)

private val emptyParams by lazy { parametersOf() }

/**
 * Returns empty [Parameters]
 */
fun emptyParameters() = emptyParams