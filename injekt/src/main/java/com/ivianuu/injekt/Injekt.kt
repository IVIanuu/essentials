package com.ivianuu.injekt

/**
 * Global configurations
 */
object Injekt {

    interface Logger {
        fun debug(msg: String)

        fun info(msg: String)

        fun warn(msg: String)

        fun error(msg: String, throwable: Throwable? = null)
    }

    /**
     * The logger to use
     */
    var logger: Logger? = null

}
