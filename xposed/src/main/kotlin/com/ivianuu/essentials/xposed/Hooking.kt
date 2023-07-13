/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.xposed

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import java.lang.reflect.Method
import kotlin.reflect.KClass

interface MethodHookBuilder {
  var priority: Int

  fun before(block: MethodHookScope.() -> Unit)

  fun after(block: MethodHookScope.() -> Unit)
}

class MethodHookBuilderImpl : MethodHookBuilder {
  override var priority: Int = XC_MethodHook.PRIORITY_DEFAULT

  private var before: (MethodHookScope.() -> Unit)? = null
  private var after: (MethodHookScope.() -> Unit)? = null

  override fun before(block: MethodHookScope.() -> Unit) {
    before = block
  }

  override fun after(block: MethodHookScope.() -> Unit) {
    after = block
  }

  fun build(): XC_MethodHook = HookImpl(priority, before, after)

  private class HookImpl(
    priority: Int,
    private val before: (MethodHookScope.() -> Unit)? = null,
    private val after: (MethodHookScope.() -> Unit)? = null
  ) : XC_MethodHook(priority) {
    override fun beforeHookedMethod(param: MethodHookParam) {
      before?.invoke(MethodHookScopeImpl(param))
    }

    override fun afterHookedMethod(param: MethodHookParam) {
      after?.invoke(MethodHookScopeImpl(param))
    }
  }
}

inline fun methodHook(block: MethodHookBuilder.() -> Unit): XC_MethodHook =
  MethodHookBuilderImpl().apply(block).build()

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

interface MethodHookScope {
  var result: Any?

  var throwable: Throwable?

  val args: Array<Any?>

  fun `this`(): Any
}

class MethodHookScopeImpl(private val param: XC_MethodHook.MethodHookParam) : MethodHookScope {
  override var result: Any?
    get() = param.result
    set(value) {
      param.result = value
    }

  override var throwable: Throwable?
    get() = param.throwable
    set(value) {
      param.throwable = value
    }

  override val args: Array<Any?>
    get() = param.args

  override fun `this`(): Any = param.thisObject
}

inline fun <reified T> MethodHookScope.arg(index: Int): T = args[index] as T

inline fun <reified T> MethodHookScope.`this`(): T = `this`() as T

inline fun <reified T> MethodHookScope.result(): T = result as T

inline fun <reified T : Throwable> MethodHookScope.throwable(): T = throwable as T

inline fun hookMethod(
  method: Method,
  block: MethodHookBuilder.() -> Unit
): XC_MethodHook.Unhook = XposedBridge.hookMethod(method, methodHook(block))

context(KClass<T>) inline fun <T : Any> hookAllMethods(
  methodName: String,
  block: MethodHookBuilder.() -> Unit
): Set<XC_MethodHook.Unhook> = hookAllMethods(this@KClass, methodName, block)

context(ClassLoader) inline fun hookAllMethods(
  className: String,
  methodName: String,
  block: MethodHookBuilder.() -> Unit
): Set<XC_MethodHook.Unhook> = hookAllMethods(getClass(className), methodName, block)

@JvmName("hookAllMethodsImpl") inline fun hookAllMethods(
  hookClass: KClass<*>,
  methodName: String,
  block: MethodHookBuilder.() -> Unit
): Set<XC_MethodHook.Unhook> = XposedBridge.hookAllMethods(
  hookClass.java,
  methodName,
  methodHook(block)
)

context(ClassLoader) inline fun hookAllConstructors(
  className: String,
  block: MethodHookBuilder.() -> Unit
): Set<XC_MethodHook.Unhook> = hookAllConstructors(getClass(className), block)

context(KClass<T>) inline fun <T : Any> hookAllConstructors(
  block: MethodHookBuilder.() -> Unit
): Set<XC_MethodHook.Unhook> = hookAllConstructors(this@KClass, block)

@JvmName("hookAllConstructorsImpl") inline fun hookAllConstructors(
  hookClass: KClass<*>,
  block: MethodHookBuilder.() -> Unit
): Set<XC_MethodHook.Unhook> = XposedBridge.hookAllConstructors(hookClass.java, methodHook(block))
