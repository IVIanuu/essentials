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

package com.ivianuu.essentials.ui.base

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.ivianuu.essentials.injection.ForActivity
import com.ivianuu.essentials.util.ThemeHelper
import com.ivianuu.essentials.util.resources.AttributeProvider
import com.ivianuu.essentials.util.resources.ResourceProvider
import dagger.Binds
import dagger.Module
import dagger.Provides

/**
 * Provides some common deps for activities
 */
@Module(includes = [EssentialsFragmentBindingModule::class])
abstract class BaseActivityModule<T : BaseActivity> {

    @Binds
    abstract fun bindBaseActivity(t: T): BaseActivity

    @Binds
    abstract fun bindAppCompatActivity(baseActivity: BaseActivity): AppCompatActivity

    @Binds
    abstract fun bindFragmentActivity(appCompatActivity: AppCompatActivity): FragmentActivity

    @Binds
    abstract fun bindActivity(fragmentActivity: FragmentActivity): Activity

    @ForActivity
    @Binds
    abstract fun bindContext(activity: Activity): Context

    @Module
    companion object {

        @JvmStatic
        @ForActivity
        @Provides
        fun provideActivityAttributeProvider(@ForActivity context: Context) =
            AttributeProvider(context)

        @JvmStatic
        @ForActivity
        @Provides
        fun provideActivityResourceProvider(@ForActivity context: Context) =
            ResourceProvider(context)

        @JvmStatic
        @ForActivity
        @Provides
        fun provideActivityThemeHelper(
            @ForActivity attributeProvider: AttributeProvider,
            @ForActivity resourceProvider: ResourceProvider
        ) = ThemeHelper(attributeProvider, resourceProvider)
    }

}
