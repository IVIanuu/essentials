package com.ivianuu.essentials.xposed

import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage

private lateinit var appComponent: XposedAppComponent

abstract class EsXposedApp : IXposedHookLoadPackage {
  override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
    @Provide val context = XposedContextImpl(lpparam)
    @Provide val appComponent = buildAppComponent()
      .also { appComponent = it }
    val xposedComponent = appComponent
    xposedComponent.hooks().forEach { it(context) }
  }

  protected abstract fun buildAppComponent(@Inject context: XposedContext): XposedAppComponent
}

inline fun createXposedAppComponent(
  @Inject context: XposedContext,
  x: (XposedContext) -> XposedAppComponent
): XposedAppComponent = x(context)
