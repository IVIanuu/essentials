package com.ivianuu.essentials.xposed

import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.scope.AppScope
import com.ivianuu.injekt.scope.ScopeElement
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage

private lateinit var appScope: AppScope

abstract class EsXposedApp : IXposedHookLoadPackage {
  override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
    @Provide val context = XposedContextImpl(lpparam)
    appScope = buildAppScope()
    val component = appScope.element<XposedAppComponent>()
    component.hooks().forEach { it(context) }
  }

  protected abstract fun buildAppScope(@Inject context: XposedContext): AppScope
}

@Provide @ScopeElement<AppScope>
class XposedAppComponent(val hooks: () -> Set<Hooks>)

inline fun createXposedAppScope(
  @Inject context: XposedContext,
  @Inject scopeFactory: (@Provide XposedContext) -> AppScope
): AppScope = scopeFactory(context)
