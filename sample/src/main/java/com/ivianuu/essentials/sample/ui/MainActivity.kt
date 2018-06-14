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
import android.arch.lifecycle.Lifecycle
import android.os.Bundle
import com.ivianuu.daggerextensions.AutoContribute
import com.ivianuu.essentials.injection.ActivityBindingModule
import com.ivianuu.essentials.injection.ActivityBindingSet
import com.ivianuu.essentials.injection.FragmentBindingModule_
import com.ivianuu.essentials.injection.PerActivity
import com.ivianuu.essentials.ui.base.BaseActivity
import com.ivianuu.essentials.ui.base.EssentialsActivityModule
import com.ivianuu.essentials.util.ext.autoDisposable
import com.ivianuu.essentials.util.ext.d
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

@ActivityBindingSet
@ActivityBindingModule
@PerActivity
@AutoContribute(modules = [EssentialsActivityModule::class, FragmentBindingModule_::class])
class MainActivity : BaseActivity() {

    @SuppressLint("PrivateResource")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Observable.interval(1, TimeUnit.SECONDS)
            .doOnDispose { d { "on dispose" } }
            .doOnSubscribe { d { "on sub" } }
            .doOnNext { d { "do on next $it" } }
            .autoDisposable(this, Lifecycle.Event.ON_PAUSE)
            .subscribe()

        if (savedInstanceState == null) {
            router.newRootScreen(MultipleChildsDestination)
        }
    }

}