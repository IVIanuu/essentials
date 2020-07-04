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

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.ivianuu.essentials.util.AppCoroutineDispatchers
import com.ivianuu.injekt.android.newServiceComponent
import com.ivianuu.injekt.composition.runReader
import com.ivianuu.injekt.get
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel

/**
 * Base service
 */
abstract class EsService : Service() {

    val component by lazy { newServiceComponent() }

    private val dispatchers: AppCoroutineDispatchers by lazy {
        component.runReader { get() }
    }

    val scope by lazy { CoroutineScope(dispatchers.default) }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? = null

}
