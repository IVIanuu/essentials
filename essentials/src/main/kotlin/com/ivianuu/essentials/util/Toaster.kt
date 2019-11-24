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
import com.ivianuu.injekt.Factory
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Shows system toasts
 */
@Factory
class Toaster(
    private val context: Context,
    private val dispatchers: AppDispatchers,
    private val stringProvider: StringProvider
) {

    fun toast(msg: String) {
        showToast(msg, false)
    }

    fun toast(msgRes: Int, vararg args: Any?) {
        showToast(stringProvider.getString(msgRes, *args), false)
    }

    fun toastLong(msg: String) {
        showToast(msg, true)
    }

    fun toastLong(msgRes: Int, vararg args: Any?) {
        showToast(stringProvider.getString(msgRes, *args), true)
    }

    private fun showToast(msg: String, long: Boolean) = GlobalScope.launch(dispatchers.main) {
        Toast.makeText(context, msg, if (long) Toast.LENGTH_LONG else Toast.LENGTH_SHORT)
            .show()
    }
}