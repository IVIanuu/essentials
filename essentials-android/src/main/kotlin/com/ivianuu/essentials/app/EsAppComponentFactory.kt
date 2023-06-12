/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.app

import android.app.Activity
import android.app.AppComponentFactory
import android.app.Application
import android.app.Service
import android.content.BroadcastReceiver
import android.content.ContentProvider
import android.content.Intent
import com.ivianuu.essentials.AndroidComponentFactoryComponent
import com.ivianuu.essentials.cast
import kotlin.reflect.KClass

class EsAppComponentFactory : AppComponentFactory() {
  private lateinit var app: EsApp

  private val factories: Map<KClass<*>, Map<String, (Intent?) -> Any>> by lazy(LazyThreadSafetyMode.NONE) {
    app.appScope.service<AndroidComponentFactoryComponent>()
      .factories
      .groupBy { it.first }
      .mapValues { (_, factories) ->
        factories
          .map { it.second }
          .toMap()
          .mapKeys { it.key.java.name }
      }
  }

  override fun instantiateApplication(cl: ClassLoader, className: String): Application =
    super.instantiateApplication(cl, className).cast<EsApp>()
      .also { app = it }

  override fun instantiateActivity(cl: ClassLoader, className: String, intent: Intent?): Activity =
    factories[Activity::class]?.get(className)?.invoke(intent)?.cast()
      ?: super.instantiateActivity(cl, className, intent)

  override fun instantiateService(cl: ClassLoader, className: String, intent: Intent?): Service =
    factories[Service::class]?.get(className)?.invoke(intent)?.cast()
      ?: super.instantiateService(cl, className, intent)

  override fun instantiateReceiver(
    cl: ClassLoader,
    className: String,
    intent: Intent?
  ): BroadcastReceiver =
    factories[BroadcastReceiver::class]?.get(className)?.invoke(intent)?.cast()
      ?: super.instantiateReceiver(cl, className, intent)

  override fun instantiateProvider(cl: ClassLoader, className: String): ContentProvider =
    factories[ContentProvider::class]?.get(className)?.invoke(null)?.cast()
      ?: super.instantiateProvider(cl, className)
}
