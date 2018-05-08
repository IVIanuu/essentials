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

package com.ivianuu.essentials.util

import android.content.Context
import android.os.Handler
import com.ivianuu.essentials.injection.ForApp
import com.ivianuu.essentials.util.ext.toast
import javax.inject.Inject

/**
 * Toasts system messages
 */
class Toaster @Inject constructor(@ForApp private val context: Context) {

    private val handler = Handler()

    fun toast(messageRes: Int, vararg args: Any) {
        toast(context.getString(messageRes, *args))
    }

    fun toast(message: String) {
        handler.post { context.toast(message) }
    }

}