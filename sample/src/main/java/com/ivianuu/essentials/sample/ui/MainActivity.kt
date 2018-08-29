
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

import android.annotation.SuppressLint
import android.app.Application
import android.os.Bundle
import com.ivianuu.essentials.ui.base.BaseActivity
import com.ivianuu.essentials.ui.common.BaseViewModel
import com.ivianuu.essentials.ui.common.PermissionDestination
import com.ivianuu.essentials.ui.common.PermissionResult
import com.ivianuu.essentials.util.ext.bindViewModel
import com.ivianuu.essentials.util.ext.d
import com.ivianuu.essentials.util.ext.navigateToForResult
import com.uber.autodispose.autoDisposable
import dagger.Binds
import dagger.Module
import io.reactivex.Observable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MainActivity : BaseActivity() {

    @Inject lateinit var app: Application
    @Inject lateinit var app2: Application

    private val viewModel by bindViewModel<MainViewModel>()

    @SuppressLint("PrivateResource")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel

        Observable.interval(1, TimeUnit.SECONDS)
            .doOnDispose { d { "on dispose" } }
            .doOnSubscribe { d { "on sub" } }
            .doOnNext { d { "do on next $it" } }
            .autoDisposable(scopeProvider)
            .subscribe()

        if (savedInstanceState == null) {
            router.newRootScreen(CounterDestination(1))
        }

        /*router.navigateTo(
            TextInputDestination(
                1,
                "Hello",
                "Hint"
            )
        )*/

        /*router.navigateToForResult<ActivityResult>(ActivityResultDestination(
            1,
            Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        ))
            .autoDisposable(scopeProvider)
            .subscribe {
                d { "on activity result -> $it" }
            }*/

        router.navigateToForResult<PermissionResult>(
            PermissionDestination(
                2,
                arrayOf(android.Manifest.permission.CAMERA)
            )
        )
            .autoDisposable(scopeProvider)
            .subscribe {
                d { "on permission result -> $it" }
            }
    }

}

class MainViewModel @Inject constructor() : BaseViewModel()

@Module
abstract class MainActivityModule {

    @Binds
    abstract fun bindBaseActivity(mainActivity: MainActivity): BaseActivity

}