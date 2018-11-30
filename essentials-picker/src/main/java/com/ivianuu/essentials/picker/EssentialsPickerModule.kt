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

package com.ivianuu.essentials.picker

import com.ivianuu.essentials.injection.PerController
import com.ivianuu.injectors.ContributesInjector
import dagger.Module

/**
 * Essentials picker module
 */
@Module(includes = [EssentialsPickerModule_Contributions::class])
abstract class EssentialsPickerModule {

    @PerController
    @ContributesInjector
    abstract fun bindColorPickerController(): ColorPickerDialog

    @PerController
    @ContributesInjector
    abstract fun bindTextInputDialog(): TextInputDialog

}