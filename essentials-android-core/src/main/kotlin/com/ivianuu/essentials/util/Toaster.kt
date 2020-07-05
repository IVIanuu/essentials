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
import com.ivianuu.essentials.app.applicationContext
import com.ivianuu.injekt.ForApplication
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.Unscoped
import com.ivianuu.injekt.get
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object Toaster {

    @Reader
    fun toast(message: String) {
        showToast(message, false)
    }

    @Reader
    fun toast(messageId: Int, vararg args: Any?) {
        showToast(get<ResourceProvider>().getString(messageId, *args), false)
    }

    @Reader
    fun toastLong(message: String) {
        showToast(message, true)
    }

    @Reader
    fun toastLong(messageId: Int, vararg args: Any?) {
        showToast(get<ResourceProvider>().getString(messageId, *args), true)
    }

    @Reader
    private fun showToast(message: String, long: Boolean) {
        globalScope.launch(dispatchers.main) {
            Toast.makeText(
                applicationContext,
                message,
                if (long) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
            )
                .show()
        }
    }
}
