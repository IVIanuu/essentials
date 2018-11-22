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

import com.ivianuu.essentials.ui.common.ActivityResultFragment
import com.ivianuu.essentials.ui.common.PermissionFragment
import com.ivianuu.injectors.ContributesInjector
import dagger.Module

/**
 * Essentials fragment binding module
 */
@Module(includes = [EssentialsFragmentBindingModule_Contributions::class])
abstract class EssentialsFragmentBindingModule {

    @PerFragment
    @ContributesInjector
    abstract fun bindActivityResultFragment(): ActivityResultFragment

    @PerFragment
    @ContributesInjector
    abstract fun bindPermissionFragment(): PermissionFragment

}