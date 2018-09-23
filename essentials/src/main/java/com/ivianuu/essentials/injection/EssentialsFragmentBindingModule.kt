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
import com.ivianuu.essentials.ui.common.AppPickerDialog
import com.ivianuu.essentials.ui.common.ColorPickerFragment
import com.ivianuu.essentials.ui.common.PermissionFragment
import com.ivianuu.essentials.ui.common.TextInputDialog
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Essentials fragment binding module
 */
@Module
abstract class EssentialsFragmentBindingModule {

    @PerFragment
    @ContributesAndroidInjector
    abstract fun bindActivityResultFragment(): ActivityResultFragment

    @PerFragment
    @ContributesAndroidInjector
    abstract fun bindAppPickerDialog(): AppPickerDialog

    @PerFragment
    @ContributesAndroidInjector
    abstract fun bindColorPickerFragment(): ColorPickerFragment

    @PerFragment
    @ContributesAndroidInjector
    abstract fun bindPermissionFragment(): PermissionFragment

    @PerFragment
    @ContributesAndroidInjector
    abstract fun bindTextInputDialog(): TextInputDialog
}