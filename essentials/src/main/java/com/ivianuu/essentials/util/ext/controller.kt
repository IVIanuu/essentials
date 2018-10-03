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

package com.ivianuu.essentials.util.ext

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import com.ivianuu.androidktx.core.app.hideInputMethod
import com.ivianuu.androidktx.core.app.showInputMethod
import com.ivianuu.androidktx.core.app.startActivityForResult
import com.ivianuu.androidktx.core.content.app
import com.ivianuu.androidktx.core.content.intent
import com.ivianuu.director.Controller
import com.ivianuu.director.requireActivity

fun Controller.hideInputMethod() {
    requireActivity().hideInputMethod()
}

fun Controller.showInputMethod(view: View, flags: Int = 0) {
    requireActivity().showInputMethod(view, flags)
}

inline fun <reified T : Controller> Controller.startActivity() {
    startActivity(requireActivity().intent<T>())
}

inline fun <reified T : Controller> Controller.startActivity(init: Intent.() -> Unit) {
    startActivity(requireActivity().intent<T>(init))
}

@RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
inline fun <reified T : Activity> Controller.startActivityForResult(
    requestCode: Int,
    options: Bundle? = null
) {
    requireActivity().startActivityForResult<T>(requestCode, options)
}

@RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
inline fun <reified T : Activity> Controller.startActivityForResult(
    requestCode: Int,
    options: Bundle? = null,
    init: Intent.() -> Unit
) {
    startActivityForResult(requireActivity().intent<T>(init), requestCode, options)
}

inline fun <reified T : Activity> Controller.activity() = requireActivity() as T

fun Controller.requireParentController() =
    parentController ?: throw IllegalStateException("parent Controller is null")

fun Controller.requireTargetController() =
    targetController ?: throw IllegalStateException("target Controller is null")

inline fun <reified T : Controller> Controller.parentController() = requireParentController() as T

inline fun <reified T : Controller> Controller.parentControllerOrNull() = try {
    parentController<T>()
} catch (e: Exception) {
    null
}

inline fun <reified T : Controller> Controller.targetController() = requireTargetController() as T

inline fun <reified T : Controller> Controller.targetControllerOrNull() = try {
    targetController<T>()
} catch (e: Exception) {
    null
}

inline fun <reified T : Application> Controller.app() = requireActivity().app<T>()