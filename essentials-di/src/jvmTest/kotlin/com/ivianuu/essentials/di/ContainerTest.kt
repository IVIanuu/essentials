/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.di

import io.kotest.matchers.types.*
import org.junit.*

class ContainerTest {
  @Test fun test() {
    val app = App()
    val container = buildContainer {
      add { app }
      add { resolve(::AppComponent) }
      add { resolve(::ActivityComponent) }
      addScoped(AppScope) { resolve(::ActivityService) }
    }

    val r = container.get<AppComponent>()

    r.activityComponent(Scope()).service shouldNotBeSameInstanceAs r.activityComponent(Scope()).service
  }
}

class App

object AppScope

class AppComponent(val activityComponent: (Scope<ActivityScope>) -> ActivityComponent)

object ActivityScope

class ActivityComponent(val service: ActivityService)

class ActivityService(val app: App)
