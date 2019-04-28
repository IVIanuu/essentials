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

package com.ivianuu.essentials.util

import com.ivianuu.injekt.factory
import com.ivianuu.injekt.get
import com.ivianuu.injekt.module
import com.ivianuu.injekt.single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.rx2.asCoroutineDispatcher

val esUtilModule = module {
    single {
        AppSchedulers(
            Schedulers.io(),
            Schedulers.computation(),
            AndroidSchedulers.mainThread()
        )
    }
    single {
        val schedulers = get<AppSchedulers>()
        AppDispatchers(
            schedulers.io.asCoroutineDispatcher(),
            schedulers.computation.asCoroutineDispatcher(),
            schedulers.main.asCoroutineDispatcher()
        )
    }

    factory { BroadcastFactory(get()) }

    factory { Clock() }

    factory { StringProvider(get()) }

    factory { Toaster(get(), get()) }
}