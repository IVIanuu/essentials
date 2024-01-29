/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.ContentProvider
import android.content.Intent
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Spread
import com.ivianuu.injekt.Tag
import kotlin.reflect.KClass

@Tag annotation class AndroidComponent {
  @Provide companion object {
    @Provide fun <@Spread T : @AndroidComponent S, S : Activity> activity(
      componentClass: KClass<T>,
      factory: (Intent?) -> T
    ): Pair<KClass<*>, (Intent?) -> Any> = componentClass to factory

    @Provide fun <@Spread T : @AndroidComponent S, S : android.app.Service> service(
      componentClass: KClass<T>,
      factory: (Intent?) -> T
    ): Pair<KClass<*>, (Intent?) -> Any> = componentClass to factory

    @Provide fun <@Spread T : @AndroidComponent S, S : BroadcastReceiver> receiver(
      componentClass: KClass<T>,
      factory: (Intent?) -> T
    ): Pair<KClass<*>, (Intent?) -> Any> = componentClass to factory

    @Provide fun <@Spread T : @AndroidComponent S, S : ContentProvider> provider(
      componentClass: KClass<T>,
      factory: (Intent?) -> T
    ): Pair<KClass<*>, (Intent?) -> Any> = componentClass to factory

    @Provide val defaultAndroidComponents get() = emptyList<Pair<KClass<*>, (Intent?) -> Any>>()
  }
}

@Provide @Service<AppScope> class AndroidComponentFactoryComponent(
  val factories: List<Pair<KClass<*>, (Intent?) -> Any>>
)
