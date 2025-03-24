/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.app

import android.app.*
import android.content.*
import essentials.*
import essentials.Service
import injekt.*
import kotlin.reflect.*

@Tag annotation class AndroidComponent {
  @Provide companion object {
    @Provide fun <@AddOn T : @AndroidComponent S, S : Any> binding(
      componentClass: KClass<T>,
      factory: (Intent?) -> T
    ): Pair<KClass<*>, (Intent?) -> Any> = componentClass to factory

    @Provide val defaultAndroidComponents get() = emptyList<Pair<KClass<*>, (Intent?) -> Any>>()
  }
}

class EsAppComponentFactory : AppComponentFactory() {
  private val factories: Map<String, (Intent?) -> Any> by lazy(LazyThreadSafetyMode.NONE) {
    app.appScope.service<Component>()
      .factories
      .toMap()
      .mapKeys { it.key.java.name }
  }

  override fun instantiateApplication(cl: ClassLoader, className: String): Application =
    super.instantiateApplication(cl, className).cast<EsApp>().also { app = it }

  override fun instantiateActivity(cl: ClassLoader, className: String, intent: Intent?): Activity =
    factories[className]?.invoke(intent)?.cast() ?: super.instantiateActivity(cl, className, intent)

  override fun instantiateService(cl: ClassLoader, className: String, intent: Intent?): android.app.Service =
    factories[className]?.invoke(intent)?.cast() ?: super.instantiateService(cl, className, intent)

  override fun instantiateReceiver(
    cl: ClassLoader,
    className: String,
    intent: Intent?
  ): BroadcastReceiver = factories[className]?.invoke(intent)?.cast()
    ?: super.instantiateReceiver(cl, className, intent)

  override fun instantiateProvider(cl: ClassLoader, className: String): ContentProvider =
    factories[className]?.invoke(null)?.cast() ?: super.instantiateProvider(cl, className)

  @Provide @Service<AppScope> class Component(
    val factories: List<Pair<KClass<*>, (Intent?) -> Any>>
  )

  companion object {
    private lateinit var app: EsApp
  }
}
