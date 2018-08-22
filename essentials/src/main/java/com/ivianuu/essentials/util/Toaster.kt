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

import android.app.Application
import android.os.Handler
import com.ivianuu.essentials.util.ext.*
import javax.inject.Inject

/**
 * Toasts system messages
 */
class Toaster @Inject constructor(private val app: Application) {

    private val handler = Handler()

    fun toastError(messageRes: Int, vararg args: Any) {
        handler.post { app.toastError(messageRes, *args) }
    }

    fun toastError(message: String) {
        handler.post { app.toastError(message) }
    }

    fun toastInfo(messageRes: Int, vararg args: Any) {
        handler.post { app.toastInfo(messageRes, *args) }
    }

    fun toastInfo(message: String) {
        handler.post { app.toastInfo(message) }
    }

    fun toastNormal(messageRes: Int, vararg args: Any) {
        handler.post { app.toastNormal(messageRes, *args) }
    }

    fun toastNormal(message: String) {
        handler.post { app.toastNormal(message) }
    }

    fun toastSuccess(messageRes: Int, vararg args: Any) {
        handler.post { app.toastSuccess(messageRes, *args) }
    }

    fun toastSuccess(message: String) {
        handler.post { app.toastSuccess(message) }
    }

    fun toastWarning(messageRes: Int, vararg args: Any) {
        handler.post { app.toastWarning(messageRes, *args) }
    }

    fun toastWarning(message: String) {
        handler.post { app.toastWarning(message) }
    }
}