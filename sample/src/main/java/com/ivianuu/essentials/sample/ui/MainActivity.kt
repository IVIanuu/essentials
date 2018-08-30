
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
import dagger.Binds
import dagger.Module
import javax.inject.Inject

class MainActivity : BaseActivity() {

    @Inject lateinit var app: Application
    @Inject lateinit var app2: Application

    @SuppressLint("PrivateResource")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            router.newRootScreen(CounterDestination(1))
        }
    }

}

@Module
abstract class MainActivityModule {

    @Binds
    abstract fun bindBaseActivity(mainActivity: MainActivity): BaseActivity

}