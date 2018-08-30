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
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.preference.PreferenceManager
import androidx.work.WorkManager
import com.f2prateek.rx.preferences2.RxSharedPreferences
import com.ivianuu.rxsystemsettings.RxSystemSettings
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Essentials app module
 */
@Module
object EssentialsAppModule {

    @JvmStatic
    @Provides
    fun provideSharedPrefs(application: Application): SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(application)

    @JvmStatic
    @Singleton
    @Provides
    fun provideRxSharedPrefs(prefs: SharedPreferences): RxSharedPreferences =
        RxSharedPreferences.create(prefs)

    @JvmStatic
    @Provides
    fun providePackageManager(app: Application): PackageManager = app.packageManager

    @JvmStatic
    @Singleton
    @Provides
    fun provideRxSystemSettings(app: Application) = RxSystemSettings.create(app)

    @JvmStatic
    @Provides
    fun provideWorkManager(): WorkManager = WorkManager.getInstance()

}