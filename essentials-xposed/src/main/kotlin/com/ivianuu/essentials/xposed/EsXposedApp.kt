package com.ivianuu.essentials.xposed

import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.AppComponent
import com.ivianuu.injekt.common.EntryPoint
import com.ivianuu.injekt.common.entryPoint
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage

private lateinit var appComponent: AppComponent

abstract class EsXposedApp : IXposedHookLoadPackage {
  override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
    @Provide val context = XposedContextImpl(lpparam)
    @Provide val appComponent = buildAppComponent()
      .also { appComponent = it }
    val xposedComponent = entryPoint<XposedAppComponent>(appComponent)
    xposedComponent.hooks().forEach { it(context) }
  }

  protected abstract fun buildAppComponent(@Inject context: XposedContext): AppComponent
}

@EntryPoint<AppComponent> interface XposedAppComponent {
  val hooks: () -> List<Hooks>
}

inline fun createXposedAppComponent(
  @Inject context: XposedContext,
  @Inject scopeFactory: (XposedContext) -> AppComponent
): AppComponent = scopeFactory(context)
