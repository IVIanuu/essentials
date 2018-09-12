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
import com.ivianuu.essentials.util.ext.mainThread
import com.ivianuu.essentials.util.ext.toastError
import com.ivianuu.essentials.util.ext.toastInfo
import com.ivianuu.essentials.util.ext.toastNormal
import com.ivianuu.essentials.util.ext.toastSuccess
import com.ivianuu.essentials.util.ext.toastWarning
import javax.inject.Inject

/**
 * Toasts system messages
 */
class Toaster @Inject constructor(context: Context) : ContextAware {

    override val providedContext = context

    fun error(messageRes: Int, vararg args: Any) = mainThread {
        toastError(messageRes, *args)
    }

    fun error(message: CharSequence) = mainThread {
        toastError(message)
    }

    fun info(messageRes: Int, vararg args: Any) = mainThread {
        toastInfo(messageRes, *args)
    }

    fun info(message: CharSequence) = mainThread {
        toastInfo(message)
    }

    fun normal(messageRes: Int, vararg args: Any) = mainThread {
        toastNormal(messageRes, *args)
    }

    fun normal(message: CharSequence) = mainThread {
        toastNormal(message)
    }

    fun success(messageRes: Int, vararg args: Any) = mainThread {
        toastSuccess(messageRes, *args)
    }

    fun success(message: CharSequence) = mainThread {
        toastSuccess(message)
    }

    fun warning(messageRes: Int, vararg args: Any) = mainThread {
        toastWarning(messageRes, *args)
    }

    fun warning(message: CharSequence) = mainThread {
        toastWarning(message)
    }

}