package com.ivianuu.injekt

/**
 * Provides instances of [T]
 */
typealias Provider<T> = (ComponentContext, Parameters) -> T