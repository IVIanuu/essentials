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
import com.ivianuu.essentials.coroutines.GlobalScope
import com.ivianuu.essentials.coroutines.MainDispatcher
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.GivenFun
import com.ivianuu.injekt.android.ApplicationContext
import kotlinx.coroutines.launch

@GivenFun
fun showToast(
    message: String,
    @Given applicationContext: ApplicationContext,
    @Given globalScope: GlobalScope,
    @Given mainDispatcher: MainDispatcher
) {
    globalScope.launch(mainDispatcher) {
        Toast.makeText(
            applicationContext,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }
}

@GivenFun
fun showToastRes(
    messageRes: Int,
    @Given showToast: showToast,
    @Given stringResource: stringResource
) {
    showToast(stringResource(messageRes))
}
