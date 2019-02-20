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

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.ivianuu.essentials.util.ext.unsafeLazy
import com.ivianuu.injekt.InjektTrait
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.android.serviceComponent
import com.ivianuu.injekt.modules
import com.ivianuu.scopes.MutableScope
import com.ivianuu.scopes.Scope
import com.ivianuu.scopes.ScopeOwner

/**
 * Base service
 */
abstract class EsService : Service(), InjektTrait, ScopeOwner {

    override val component by unsafeLazy {
        serviceComponent {
            modules(this@EsService.modules())
        }
    }

    override val scope: Scope get() = _scope
    private val _scope = MutableScope()

    override fun onDestroy() {
        _scope.close()
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? = null

    protected open fun modules(): List<Module> = emptyList()

}