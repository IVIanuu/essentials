package com.ivianuu.essentials.xposed

import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.Component
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage

private lateinit var appComponent: Component<XposedAppComponent>

abstract class EsXposedApp : IXposedHookLoadPackage {
  override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
    @Provide val context = XposedContextImpl(lpparam)
    @Provide val appComponent = buildAppComponent()
      .also { appComponent = it }
    appComponent.element<XposedHooksComponent>().hooks().forEach { it(context) }
  }

  protected abstract fun buildAppComponent(@Inject context: XposedContext): Component<XposedAppComponent>
}

inline fun createXposedAppComponent(
  @Inject context: XposedContext,
  x: (XposedContext) -> Component<XposedAppComponent>
): Component<XposedAppComponent> = x(context)
