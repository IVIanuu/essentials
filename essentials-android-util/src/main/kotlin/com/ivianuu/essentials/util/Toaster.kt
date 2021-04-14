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

package com.ivianuu.essentials.util

import android.widget.Toast
import com.ivianuu.essentials.coroutines.MainDispatcher
import com.ivianuu.essentials.coroutines.ScopeCoroutineScope
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.android.AppContext
import com.ivianuu.injekt.scope.AppGivenScope
import kotlinx.coroutines.launch

typealias Toaster = (String) -> Unit

@Given
fun toaster(
    @Given appContext: AppContext,
    @Given mainDispatcher: MainDispatcher,
    @Given scope: ScopeCoroutineScope<AppGivenScope>
): Toaster = { message ->
    scope.launch(mainDispatcher) {
        Toast.makeText(
            appContext,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }
}
