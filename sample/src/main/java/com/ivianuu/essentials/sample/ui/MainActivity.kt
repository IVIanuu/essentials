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
import dagger.Module

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        d { "created" }

        if (savedInstanceState == null) {
            router.newRootScreen(ChildNavigationKey(0, 1))
        }
    }

    override fun onStart() {
        super.onStart()
        d { "started" }
    }

    override fun onResume() {
        super.onResume()
        d { "resumed" }
    }

    override fun onPause() {
        super.onPause()
        d { "paused" }
    }

    override fun onStop() {
        super.onStop()
        d { "stopped" }
    }

    override fun onDestroy() {
        super.onDestroy()
        d { "destroyed" }
    }
}

@Module
abstract class MainActivityModule : BaseActivityModule<MainActivity>()