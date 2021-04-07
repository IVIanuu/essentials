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
import androidx.lifecycle.lifecycleScope
import com.ivianuu.essentials.coroutines.runOnCancellation
import com.ivianuu.essentials.ui.DecorateUi
import com.ivianuu.essentials.ui.LocalUiGivenScope
import com.ivianuu.essentials.ui.UiGivenScope
import com.ivianuu.essentials.ui.core.AppUi
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.android.activityGivenScope
import com.ivianuu.injekt.scope.GivenScopeElementBinding
import com.ivianuu.injekt.scope.element
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch

class EsActivity : ComponentActivity() {
    private val uiGivenScope by lazy {
        activityGivenScope.element<() -> UiGivenScope>()()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val component = uiGivenScope.element<EsActivityComponent>()

        lifecycleScope.launch(start = CoroutineStart.UNDISPATCHED) {
            runOnCancellation {
                uiGivenScope.dispose()
            }
        }

        setContent {
            CompositionLocalProvider(LocalUiGivenScope provides uiGivenScope) {
                component.decorateUi {
                    component.appUi()
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

@GivenScopeElementBinding<UiGivenScope>
@Given
class EsActivityComponent(
    @Given val appUi: AppUi,
    @Given val decorateUi: DecorateUi
)
