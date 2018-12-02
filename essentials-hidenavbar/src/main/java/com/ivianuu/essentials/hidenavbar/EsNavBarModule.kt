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

package com.ivianuu.essentials.hidenavbar

import android.content.Context
import android.content.SharedPreferences
import com.ivianuu.essentials.app.AppService
import com.ivianuu.essentials.injection.AppServiceKey
import com.ivianuu.essentials.injection.PerController
import com.ivianuu.injectors.ContributesInjector
import com.ivianuu.kprefs.KPrefs
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import javax.inject.Qualifier

@Qualifier
annotation class NavBarSharedPrefs

/**
 * Nav bar module
 */
@Module
abstract class EsNavBarModule {

    @Binds
    @IntoMap
    @AppServiceKey(NavBarController::class)
    abstract fun bindNavBarController(navBarController: NavBarController): AppService

    @Module
    companion object {

        @NavBarSharedPrefs
        @JvmStatic
        @Provides
        fun provideNavBarSharedPrefs(context: Context) =
            NavBarPlugins.getDefaultSharedPreferences(context)

        @NavBarSharedPrefs
        @JvmStatic
        @Provides
        fun provideNavBarKPrefs(
            @NavBarSharedPrefs sharedPrefs: SharedPreferences
        ) = KPrefs(sharedPrefs)

    }

}

@Module(includes = [EsNavBarBindingModule_Contributions::class])
abstract class EsNavBarBindingModule {

    @PerController
    @ContributesInjector
    abstract fun bindNavBarSettingsController(): NavBarSettingsController

}