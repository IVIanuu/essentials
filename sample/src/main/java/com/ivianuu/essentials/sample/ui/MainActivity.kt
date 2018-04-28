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

package com.ivianuu.essentials.sample.ui

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import com.f2prateek.rx.preferences2.RxSharedPreferences
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.injection.DefaultSharedPrefs
import com.ivianuu.essentials.ui.base.BaseActivity
import com.ivianuu.essentials.ui.base.BaseActivityModule
import com.ivianuu.rxactivityresult.ActivityResultStarter
import dagger.Module
import dagger.Provides
import io.reactivex.rxkotlin.addTo
import javax.inject.Inject

class MainActivity : BaseActivity() {

    @Inject lateinit var myApplication: Application
    @DefaultSharedPrefs lateinit var rxPrefs: RxSharedPreferences
    @Inject lateinit var activityResultStarter: ActivityResultStarter
    @Inject lateinit var something: Something

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityResultStarter.start(
            Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        ).subscribe {
            d { "on result" }
        }
            .addTo(disposables)
    }
}

object Something

@Module
abstract class MainActivityModule : BaseActivityModule<MainActivity>()  {

    @Module
    companion object {

        @JvmStatic
        @Provides
        fun provideSomething(activity: Activity) = Something

    }

}