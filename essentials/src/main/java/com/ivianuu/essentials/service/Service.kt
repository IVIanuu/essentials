/*
 * Copyright 2019 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.service

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.ivianuu.essentials.util.ext.unsafeLazy
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provider
import com.ivianuu.injekt.android.ApplicationScope
import com.ivianuu.kommon.lifecycle.doOnAny
import com.ivianuu.scopes.ReusableScope
import com.ivianuu.scopes.Scope
import com.ivianuu.scopes.ScopeOwner

interface Service {
    fun start() {
    }

    fun stop() {
    }
}

interface AppService : Service

interface LifecycleService : Service, LifecycleEventObserver

interface UiVisibleService : Service {

    fun uiVisible() {
    }

    fun uiInvisible() {
    }

}

@Inject
class LifecycleServicesManager(private val services: Set<LifecycleService>) :
    LifecycleEventObserver {
    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        services.forEach { it.onStateChanged(source, event) }
    }
}

@Inject
@ApplicationScope
class UiVisibleServicesManager(
    private val serviceProviders: Set<Provider<UiVisibleService>>
) : AppService {

    private val initializedServices by unsafeLazy {
        serviceProviders.map { it() }
    }

    private var wasStarted = false

    override fun start() {
        super.start()
        ProcessLifecycleOwner.get().lifecycle.doOnAny { owner, _ ->
            val isStarted =
                owner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)

            if (isStarted != wasStarted) {
                wasStarted = isStarted
                if (isStarted) {
                    initializedServices.forEach { it.uiVisible() }
                } else {
                    initializedServices.forEach { it.uiInvisible() }
                }
            }
        }
    }
}

abstract class InitializingService : Service {

    private var initialized = false

    final override fun start() {
        if (!initialized) {
            initialized = true
            initialize()
        }
    }

    final override fun stop() {
    }

    protected abstract fun initialize()
}

abstract class AbstractService : Service, ScopeOwner {

    override val scope: Scope
        get() = _scope
    private val _scope = ReusableScope()

    override fun stop() {
        super.stop()
        _scope.clear()
    }

}