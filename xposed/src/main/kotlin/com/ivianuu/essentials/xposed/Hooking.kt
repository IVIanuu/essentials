/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.xposed

import com.ivianuu.injekt.*
import de.robv.android.xposed.*
import java.lang.reflect.*
import kotlin.reflect.*

class MethodHookBuilder {
  var priority: Int = XC_MethodHook.PRIORITY_DEFAULT

  private var before: (MethodHookScope.() -> Unit)? = null
  private var after: (MethodHookScope.() -> Unit)? = null

  fun before(block: MethodHookScope.() -> Unit) {
    before = block
  }

  fun after(block: MethodHookScope.() -> Unit) {
    after = block
  }

  fun build(): XC_MethodHook = HookImpl(priority, before, after)

  private class HookImpl(
    priority: Int,
    private val before: (MethodHookScope.() -> Unit)? = null,
    private val after: (MethodHookScope.() -> Unit)? = null
  ) : XC_MethodHook(priority) {
    override fun beforeHookedMethod(param: MethodHookParam) {
      before?.invoke(MethodHookScope(param))
    }

    override fun afterHookedMethod(param: MethodHookParam) {
      after?.invoke(MethodHookScope(param))
    }
  }
}

inline fun methodHook(block: MethodHookBuilder.() -> Unit): XC_MethodHook =
  MethodHookBuilder().apply(block).build()

fun MethodHookBuilder.replace(block: MethodHookScope.() -> Any?) {
  before {
    try {
      result = block()
    } catch (e: Throwable) {
      throwable = e
    }
  }
}

fun MethodHookBuilder.skip() = replace { null }

class MethodHookScope(private val param: XC_MethodHook.MethodHookParam) {
  var result: Any?
    get() = param.result
    set(value) {
      param.result = value
    }

  var throwable: Throwable?
    get() = param.throwable
    set(value) {
      param.throwable = value
    }

  val args: Array<Any?>
    get() = param.args

  fun `this`(): Any = param.thisObject
}

inline fun <reified T> MethodHookScope.arg(index: Int): T = args[index] as T

inline fun <reified T> MethodHookScope.`this`(): T = `this`() as T

inline fun <reified T> MethodHookScope.result(): T = result as T

inline fun <reified T : Throwable> MethodHookScope.throwable(): T = throwable as T

inline fun hookMethod(
  method: Method,
  block: MethodHookBuilder.() -> Unit
): XC_MethodHook.Unhook = XposedBridge.hookMethod(method, methodHook(block))

inline fun <T : Any> hookAllMethods(
  methodName: String,
  hookClass: KClass<T> = inject,
  block: MethodHookBuilder.() -> Unit
): Set<XC_MethodHook.Unhook> = hookAllMethods(hookClass, methodName, block)

inline fun hookAllMethods(
  className: String,
  methodName: String,
  classLoader: ClassLoader = inject,
  block: MethodHookBuilder.() -> Unit
): Set<XC_MethodHook.Unhook> = hookAllMethods(classLoader.getClass(className), methodName, block)

inline fun hookAllMethods(
  hookClass: KClass<*>,
  methodName: String,
  block: MethodHookBuilder.() -> Unit
): Set<XC_MethodHook.Unhook> = XposedBridge.hookAllMethods(
  hookClass.java,
  methodName,
  methodHook(block)
)

inline fun hookAllConstructors(
  className: String,
  classLoader: ClassLoader = inject,
  block: MethodHookBuilder.() -> Unit
): Set<XC_MethodHook.Unhook> = hookAllConstructors(classLoader.getClass(className), block)

inline fun <T : Any> hookAllConstructors(
  hookClass: KClass<T> = inject,
  block: MethodHookBuilder.() -> Unit
): Set<XC_MethodHook.Unhook> = XposedBridge.hookAllConstructors(hookClass.java, methodHook(block))
