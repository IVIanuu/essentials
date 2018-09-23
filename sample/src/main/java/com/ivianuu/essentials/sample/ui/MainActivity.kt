
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

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import com.ivianuu.essentials.sample.ui.counter.CounterDestination
import com.ivianuu.essentials.ui.base.BaseActivity
import com.ivianuu.essentials.ui.base.BaseActivityModule
import com.ivianuu.essentials.ui.common.ColorPickerDestination
import com.ivianuu.essentials.util.ext.addFragmentForResult
import com.ivianuu.essentials.util.ext.navigateForActivityResult
import com.ivianuu.essentials.util.ext.requestPermissions
import com.ivianuu.timberktx.d
import dagger.Module
import kotlinx.coroutines.launch

class MainActivity : BaseActivity() {

    override val startDestination: Any?
        get() = CounterDestination(1)

    override fun navigatorPlugins() =
        listOf(MaterialDialogNavigatorPlugin(this))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        launch {
            val result =
                router.navigateForActivityResult(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))

            d { "result -> $result" }

            val granted =
                router.requestPermissions(Manifest.permission.CAMERA)

            d { "granted -> $granted" }

            val color =
                router.addFragmentForResult(ColorPickerDestination())

            d { "color -> $color" }
        }
    }
}

@Module
abstract class MainActivityModule : BaseActivityModule<MainActivity>()