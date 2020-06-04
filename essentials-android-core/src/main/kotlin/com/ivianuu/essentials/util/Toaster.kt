/*
 * Copyright 2019 Manuel Wrage
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

import android.content.Context
import android.widget.Toast
import com.ivianuu.injekt.ForApplication
import com.ivianuu.injekt.Transient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Shows system toasts
 */
@Transient
class Toaster(
    private val context: @ForApplication Context,
    private val coroutineScope: @ForApplication CoroutineScope,
    private val dispatchers: AppCoroutineDispatchers,
    private val resourceProvider: ResourceProvider
) {

    fun toast(message: String) {
        showToast(message, false)
    }

    fun toast(messageId: Int, vararg args: Any?) {
        showToast(resourceProvider.getString(messageId, *args), false)
    }

    fun toastLong(message: String) {
        showToast(message, true)
    }

    fun toastLong(messageId: Int, vararg args: Any?) {
        showToast(resourceProvider.getString(messageId, *args), true)
    }

    private fun showToast(message: String, long: Boolean) =
        coroutineScope.launch(dispatchers.main) {
            Toast.makeText(context, message, if (long) Toast.LENGTH_LONG else Toast.LENGTH_SHORT)
            .show()
    }
}
