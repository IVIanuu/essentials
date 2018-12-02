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

package com.ivianuu.essentials.injection

import com.ivianuu.essentials.app.AppInitializer
import com.ivianuu.essentials.app.RxJavaAppInitializer
import com.ivianuu.essentials.app.StateStoreAppInitializer
import com.ivianuu.essentials.app.TimberAppInitializer
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * Essentials app initializer module
 */
@Module
abstract class EsAppInitializerModule {

    @Binds
    @IntoMap
    @AppInitializerKey(RxJavaAppInitializer::class)
    abstract fun bindRxJavaAppInitializer(initializer: RxJavaAppInitializer): AppInitializer

    @Binds
    @IntoMap
    @AppInitializerKey(StateStoreAppInitializer::class)
    abstract fun bindStateStoreAppInitializer(initializer: StateStoreAppInitializer): AppInitializer

    @Binds
    @IntoMap
    @AppInitializerKey(TimberAppInitializer::class)
    abstract fun bindTimberAppInitializer(initializer: TimberAppInitializer): AppInitializer

}