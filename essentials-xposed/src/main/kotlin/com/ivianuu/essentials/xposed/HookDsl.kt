/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.xposed

import com.ivianuu.injekt.Inject
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import java.lang.reflect.Method
import kotlin.reflect.KClass

interface MethodHookBuilder {
  var priority: Int

  fun before(block: context(MethodHookScope) () -> Unit)

  fun after(block: context(MethodHookScope) () -> Unit)

  fun build(): XC_MethodHook
}

class MethodHookBuilderImpl : MethodHookBuilder {
  override var priority: Int = XC_MethodHook.PRIORITY_DEFAULT

  private var before: (context(MethodHookScope) () -> Unit)? = null
  private var after: (context(MethodHookScope) () -> Unit)? = null

  override fun before(block: context(MethodHookScope) () -> Unit) {
    before = block
  }

  override fun after(block: context(MethodHookScope) () -> Unit) {
    after = block
  }

  override fun build(): XC_MethodHook = HookImpl(priority, before, after)

  private class HookImpl(
    priority: Int,
    private val before: (context(MethodHookScope) () -> Unit)? = null,
    private val after: (context(MethodHookScope) () -> Unit)? = null
  ) : XC_MethodHook(priority) {
    override fun beforeHookedMethod(param: MethodHookParam) {
      before?.invoke(MethodHookScopeImpl(param))
    }

    override fun afterHookedMethod(param: MethodHookParam) {
      after?.invoke(MethodHookScopeImpl(param))
    }
  }
}

inline fun methodHook(block: context(MethodHookBuilder) () -> Unit): XC_MethodHook =
  MethodHookBuilderImpl().apply(block).build()

context(MethodHookBuilder) fun replace(block: context(MethodHookScope) () -> Any?) {
  before {
    try {
      result = block()
    } catch (e: Throwable) {
      throwable = e
    }
  }
}

context(MethodHookBuilder) fun skip() {
  replace { null }
}

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

context(MethodHookScope) inline fun <reified T> arg(index: Int): T = args[index] as T

context(MethodHookScope) inline fun <reified T> `this`(): T = `this`() as T

context(MethodHookScope) inline fun <reified T> result(): T = result as T

context(MethodHookScope) inline fun <reified T : Throwable> throwable(): T = throwable as T

inline fun hookMethod(
  method: Method,
  block: context(MethodHookBuilder) () -> Unit
): XC_MethodHook.Unhook = XposedBridge.hookMethod(method, methodHook(block))

inline fun <T : Any> hookAllMethods(
  methodName: String,
  @Inject hookClass: KClass<T>,
  block: context(MethodHookBuilder) () -> Unit
): Set<XC_MethodHook.Unhook> = hookAllMethods(hookClass, methodName, block)

inline fun hookAllMethods(
  className: String,
  methodName: String,
  @Inject classLoader: ClassLoader,
  block: context(MethodHookBuilder) () -> Unit
): Set<XC_MethodHook.Unhook> = hookAllMethods(classLoader.getClass(className), methodName, block)

inline fun hookAllMethods(
  hookClass: KClass<*>,
  methodName: String,
  block: context(MethodHookBuilder) () -> Unit
): Set<XC_MethodHook.Unhook> = XposedBridge.hookAllMethods(
  hookClass.java,
  methodName,
  methodHook(block)
)

inline fun hookAllConstructors(
  className: String,
  @Inject classLoader: ClassLoader,
  block: context(MethodHookBuilder) () -> Unit
): Set<XC_MethodHook.Unhook> = hookAllConstructors(classLoader.getClass(className), block)

inline fun <T : Any> hookAllConstructors(
  @Inject hookClass: KClass<T>,
  block: context(MethodHookBuilder) () -> Unit
): Set<XC_MethodHook.Unhook> = XposedBridge.hookAllConstructors(hookClass.java, methodHook(block))
