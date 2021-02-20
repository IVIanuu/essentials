/*
 * Copyright 2020 Manuel Wrage
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

package com.ivianuu.essentials.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.ui.platform.setContent
import com.ivianuu.essentials.ui.DecorateUi
import com.ivianuu.essentials.ui.LocalUiComponent
import com.ivianuu.essentials.ui.UiComponent
import com.ivianuu.essentials.ui.UiWorkerRunner
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.android.ActivityComponent
import com.ivianuu.injekt.android.activityComponent
import com.ivianuu.injekt.component.ComponentElementBinding
import com.ivianuu.injekt.component.get

/**
 * Base activity
 */
abstract class EsActivity : AppCompatActivity() {

    private val uiComponent by lazy {
        activityComponent.get<() -> UiComponent>()()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val component = uiComponent.get<EsActivityComponent>()
        component.uiWorkerRunner()

        setContent {
            Providers(LocalUiComponent provides uiComponent) {
                component.decorateUi {
                    Content()
                }
            }
        }
    }

    // todo tmp fix for https://issuetracker.google.com/issues/139738913
    override fun onBackPressed() {
        if (onBackPressedDispatcher.hasEnabledCallbacks()) {
            super.onBackPressed()
        } else {
            finishAfterTransition()
        }
    }

    @Composable protected abstract fun Content()
}

@ComponentElementBinding<ActivityComponent>
@Given
class EsActivityComponent(
    @Given val decorateUi: DecorateUi,
    @Given val uiWorkerRunner: UiWorkerRunner
)
