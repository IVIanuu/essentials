package com.ivianuu.injekt

/**
 * Lambda which provides an instance of T.
 *
 * The [ComponentContext] of the current context is passed to the lambda which enables transitive dependency injection, e.g.
 * injection of dependencies within a module.
 */
typealias Provider<T> = (ComponentContext, Parameters) -> T

typealias Declarations = Map<Key, Declaration<*>>
