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
import es.dmoral.toasty.Toasty
import javax.inject.Inject

/**
 * Toasts system messages
 */
class Toaster @Inject constructor(
    private val context: Context,
    private val resourceProvider: ResourceProvider
) {

    fun error(messageRes: Int, vararg args: Any) = mainThread {
        Toasty.error(context, string(messageRes, *args)).show()
    }

    fun error(message: CharSequence) = mainThread {
        Toasty.error(context, message).show()
    }

    fun info(messageRes: Int, vararg args: Any) = mainThread {
        Toasty.info(context, string(messageRes, *args)).show()
    }

    fun info(message: CharSequence) = mainThread {
        Toasty.info(context, message).show()
    }

    fun normal(messageRes: Int, vararg args: Any) = mainThread {
        Toasty.normal(context, string(messageRes, *args)).show()
    }

    fun normal(message: CharSequence) = mainThread {
        Toasty.normal(context, message).show()
    }

    fun success(messageRes: Int, vararg args: Any) = mainThread {
        Toasty.success(context, string(messageRes, *args)).show()
    }

    fun success(message: CharSequence) = mainThread {
        Toasty.success(context, message).show()
    }

    fun warning(messageRes: Int, vararg args: Any) = mainThread {
        Toasty.warning(context, string(messageRes, *args)).show()
    }

    fun warning(message: CharSequence) = mainThread {
        Toasty.warning(context, message).show()
    }

    private fun string(resId: Int, vararg args: Any) = resourceProvider.string(resId, *args)
}