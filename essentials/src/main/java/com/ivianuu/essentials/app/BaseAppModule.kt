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

package com.ivianuu.essentials.app

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.support.v7.preference.PreferenceManager
import com.f2prateek.rx.preferences2.RxSharedPreferences
import com.ivianuu.essentials.injection.DefaultSharedPrefs
import com.ivianuu.essentials.injection.ForApp
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Base app module
 */
@Module
abstract class BaseAppModule<T : BaseApp> {

    @Module
    companion object {

        @JvmStatic
        @ForApp
        @Provides
        fun provideContext(app: Application): Context = app

        @JvmStatic
        @DefaultSharedPrefs
        @Provides
        fun provideSharedPrefs(@ForApp context: Context): SharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(context)

        @JvmStatic
        @DefaultSharedPrefs
        @Singleton
        @Provides
        fun provideRxSharedPrefs(@DefaultSharedPrefs prefs: SharedPreferences): RxSharedPreferences =
            RxSharedPreferences.create(prefs)

    }

}