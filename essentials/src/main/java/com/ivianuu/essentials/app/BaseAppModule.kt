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
import android.content.pm.PackageManager
import android.content.res.Resources
import android.preference.PreferenceManager
import androidx.work.WorkManager
import com.ivianuu.essentials.util.ContextAware
import com.ivianuu.essentials.util.coroutines.AppCoroutineDispatchers
import com.ivianuu.essentials.util.rx.AppRxSchedulers
import com.ivianuu.kprefs.KSharedPreferences
import com.ivianuu.rxsystemsettings.RxSystemSettings
import dagger.Binds
import dagger.Module
import dagger.Provides
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.android.UI
import kotlinx.coroutines.rx2.asCoroutineDispatcher
import javax.inject.Singleton

/**
 * Essentials app module
 */
@Module
abstract class BaseAppModule<T : BaseApp> {

    @Binds
    abstract fun bindBaseApp(t: T): BaseApp

    @Binds
    abstract fun bindApplication(baseApp: BaseApp): Application

    @Binds
    abstract fun bindContext(app: Application): Context

    @Module
    companion object {

        @JvmStatic
        @Singleton
        @Provides
        fun provideContextAwareness(context: Context) = object : ContextAware {
            override val providedContext = context
        }

        @JvmStatic
        @Singleton
        @Provides
        fun provideCoroutineDispatchers(schedulers: AppRxSchedulers) =
            AppCoroutineDispatchers(
                io = schedulers.io.asCoroutineDispatcher(),
                computation = schedulers.computation.asCoroutineDispatcher(),
                main = UI
            )

        @JvmStatic
        @Singleton
        @Provides
        fun provideRxSchedulers() = AppRxSchedulers(
            io = Schedulers.io(),
            computation = Schedulers.computation(),
            main = AndroidSchedulers.mainThread()
        )

        @JvmStatic
        @Provides
        fun provideSharedPrefs(context: Context): SharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(context)

        @JvmStatic
        @Singleton
        @Provides
        fun provideKSharedPrefs(prefs: SharedPreferences) =
            KSharedPreferences(prefs)

        @JvmStatic
        @Provides
        fun providePackageManager(context: Context): PackageManager = context.packageManager

        @JvmStatic
        @Provides
        fun provideResources(context: Context): Resources = context.resources

        @JvmStatic
        @Singleton
        @Provides
        fun provideRxSystemSettings(context: Context) = RxSystemSettings.create(context)

        @JvmStatic
        @Provides
        fun provideWorkManager(): WorkManager = WorkManager.getInstance()

    }

}