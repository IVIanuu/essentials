/*
 * Copyright 2018 Manuel Wrage
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

package com.ivianuu.essentials.data.base

import android.annotation.TargetApi
import android.os.Build
import android.service.quicksettings.TileService
import com.ivianuu.essentials.util.asMainCoroutineScope
import com.ivianuu.essentials.util.ext.unsafeLazy
import com.ivianuu.injekt.Component
import com.ivianuu.injekt.InjektTrait
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.android.serviceComponent
import com.ivianuu.scopes.MutableScope
import com.ivianuu.scopes.ReusableScope
import com.ivianuu.scopes.Scope

/**
 * Base tile service
 */
@TargetApi(Build.VERSION_CODES.N)
abstract class EsTileService : TileService(), InjektTrait {

    override val component by unsafeLazy {
        serviceComponent {
            modules(this@EsTileService.modules())
        }
    }

    val scope: Scope get() = _scope
    private val _scope = MutableScope()

    val coroutineScope = scope.asMainCoroutineScope()

    val listeningScope: Scope get() = _listeningScope
    private val _listeningScope = ReusableScope()

    val listeningCoroutineScope get() = _listeningCoroutineScope
    private var _listeningCoroutineScope = _listeningScope.asMainCoroutineScope()

    override fun onDestroy() {
        _scope.close()
        super.onDestroy()
    }

    override fun onStartListening() {
        super.onStartListening()
        _listeningCoroutineScope = _listeningScope.asMainCoroutineScope()
    }

    override fun onStopListening() {
        _listeningScope.clear()
        super.onStopListening()
    }

    protected open fun dependencies() = emptyList<Component>()

    protected open fun modules(): List<Module> = emptyList()

}