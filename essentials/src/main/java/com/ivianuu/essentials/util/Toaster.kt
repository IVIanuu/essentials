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
import com.ivianuu.essentials.injection.ForApp
import com.ivianuu.essentials.util.ext.*
import javax.inject.Inject

/**
 * Toasts system messages
 */
class Toaster @Inject constructor(private val app: Application) {

    private val handler = Handler()

    fun errorToast(messageRes: Int, vararg args: Any) {
        handler.post { app.errorToast(messageRes, *args) }
    }

    fun errorToast(message: String) {
        handler.post { app.errorToast(message) }
    }

    fun infoToast(messageRes: Int, vararg args: Any) {
        handler.post { app.infoToast(messageRes, *args) }
    }

    fun infoToast(message: String) {
        handler.post { app.infoToast(message) }
    }

    fun normalToast(messageRes: Int, vararg args: Any) {
        handler.post { app.normalToast(messageRes, *args) }
    }

    fun normalToast(message: String) {
        handler.post { app.normalToast(message) }
    }

    fun successToast(messageRes: Int, vararg args: Any) {
        handler.post { app.successToast(messageRes, *args) }
    }

    fun successToast(message: String) {
        handler.post { app.successToast(message) }
    }

    fun warningToast(messageRes: Int, vararg args: Any) {
        handler.post { app.warningToast(messageRes, *args) }
    }

    fun warningToast(message: String) {
        handler.post { app.warningToast(message) }
    }
}