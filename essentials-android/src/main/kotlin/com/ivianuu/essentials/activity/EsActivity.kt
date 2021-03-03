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
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import com.ivianuu.essentials.ui.DecorateUi
import com.ivianuu.essentials.ui.LocalUiComponent
import com.ivianuu.essentials.ui.UiComponent
import com.ivianuu.essentials.ui.UiWorkerRunner
import com.ivianuu.essentials.ui.core.AppUi
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.android.ActivityComponent
import com.ivianuu.injekt.android.activityComponent
import com.ivianuu.injekt.component.ComponentElementBinding
import com.ivianuu.injekt.component.get

class EsActivity : ComponentActivity() {

    private val uiComponent by lazy {
        activityComponent.get<() -> UiComponent>()()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val component = uiComponent.get<EsActivityComponent>()
        component.uiWorkerRunner()

        setContent {
            CompositionLocalProvider(LocalUiComponent provides uiComponent) {
                component.decorateUi {
                    uiComponent
                        .get<EsActivityComponent>()
                        .appUi()
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

}

@ComponentElementBinding<ActivityComponent>
@Given
class EsActivityComponent(
    @Given val appUi: AppUi,
    @Given val decorateUi: DecorateUi,
    @Given val uiWorkerRunner: UiWorkerRunner
)
