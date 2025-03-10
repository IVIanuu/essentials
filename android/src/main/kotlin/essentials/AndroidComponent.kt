/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials

import android.app.*
import android.content.*
import injekt.*
import kotlin.reflect.*

@Tag @Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS, AnnotationTarget.CONSTRUCTOR)
annotation class AndroidComponent {
  @Provide companion object {
    @Provide fun <@AddOn T : @AndroidComponent S, S : Activity> activity(
      componentClass: KClass<T>,
      factory: (Intent?) -> T
    ): Pair<KClass<*>, (Intent?) -> Any> = componentClass to factory

    @Provide fun <@AddOn T : @AndroidComponent S, S : android.app.Service> service(
      componentClass: KClass<T>,
      factory: (Intent?) -> T
    ): Pair<KClass<*>, (Intent?) -> Any> = componentClass to factory

    @Provide fun <@AddOn T : @AndroidComponent S, S : BroadcastReceiver> receiver(
      componentClass: KClass<T>,
      factory: (Intent?) -> T
    ): Pair<KClass<*>, (Intent?) -> Any> = componentClass to factory

    @Provide fun <@AddOn T : @AndroidComponent S, S : ContentProvider> provider(
      componentClass: KClass<T>,
      factory: (Intent?) -> T
    ): Pair<KClass<*>, (Intent?) -> Any> = componentClass to factory

    @Provide val defaultAndroidComponents get() = emptyList<Pair<KClass<*>, (Intent?) -> Any>>()
  }
}

@Provide @Service<AppScope> class AndroidComponentFactoryComponent(
  val factories: List<Pair<KClass<*>, (Intent?) -> Any>>
)
