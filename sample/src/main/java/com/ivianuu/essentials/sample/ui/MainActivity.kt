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

import android.os.Bundle
import com.ivianuu.essentials.ui.base.BaseActivity
import com.ivianuu.essentials.ui.base.BaseActivityModule
import com.ivianuu.essentials.util.ext.d
import com.ivianuu.essentials.util.ext.subscribeForUi
import dagger.Module
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            router.newRootScreen(MultipleCountersKey)
        }

        Observable.interval(1, TimeUnit.SECONDS)
            .doOnDispose { d { "on dispose" } }
            .doOnSubscribe { d { "on sub" } }
            .subscribeForUi(this, this::handleResult)
    }

    private fun handleResult(long: Long) {
        d { "on next $long" }
    }
}

@Module
abstract class MainActivityModule : BaseActivityModule<MainActivity>()