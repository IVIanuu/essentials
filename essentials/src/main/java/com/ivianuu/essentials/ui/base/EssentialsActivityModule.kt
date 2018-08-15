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
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import com.ivianuu.essentials.util.ext.navigationHolder
import com.ivianuu.essentials.util.ext.router
import com.ivianuu.essentials.util.ext.traveler
import com.ivianuu.rxactivityresult.RxActivityResult
import com.ivianuu.rxpermissions.RxPermissions
import dagger.Binds
import dagger.Module
import dagger.Provides

/**
 * Provides some common deps for activities
 */
@Module(includes = [EssentialsFragmentBindingModule::class])
abstract class EssentialsActivityModule {

    @Binds
    abstract fun bindAppCompatActivity(baseActivity: BaseActivity): AppCompatActivity

    @Binds
    abstract fun bindFragmentActivity(appCompatActivity: AppCompatActivity): FragmentActivity

    @Binds
    abstract fun bindActivity(fragmentActivity: FragmentActivity): Activity

    @Module
    companion object {

        @JvmStatic
        @Provides
        fun provideActivityResultStarter(activity: FragmentActivity) =
            RxActivityResult.get(activity)

        @JvmStatic
        @Provides
        fun providePermissionRequester(activity: FragmentActivity) =
            RxPermissions.get(activity)

        @JvmStatic
        @Provides
        fun provideTraveler(activity: BaseActivity) =
            activity.traveler(activity.fragmentContainer)

        @JvmStatic
        @Provides
        fun provideNavigatorHolder(activity: BaseActivity) =
            activity.navigationHolder(activity.fragmentContainer)

        @JvmStatic
        @Provides
        fun provideRouter(activity: BaseActivity) =
            activity.router(activity.fragmentContainer)

    }

}
