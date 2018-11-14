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
import androidx.lifecycle.Lifecycle
import com.ivianuu.androidktx.core.app.hideInputMethod
import com.ivianuu.androidktx.core.app.showInputMethod
import com.ivianuu.androidktx.core.app.startActivityForResult
import com.ivianuu.androidktx.core.content.app
import com.ivianuu.androidktx.core.content.intent
import com.ivianuu.director.Controller
import com.ivianuu.director.arch.lifecycle.LifecycleController
import com.ivianuu.scopes.archlifecycle.onCreate
import com.ivianuu.scopes.archlifecycle.onDestroy
import com.ivianuu.scopes.archlifecycle.onPause
import com.ivianuu.scopes.archlifecycle.onResume
import com.ivianuu.scopes.archlifecycle.onStart
import com.ivianuu.scopes.archlifecycle.onStop
import com.ivianuu.scopes.archlifecycle.scopeFor

fun Controller.hideInputMethod() {
    activity.hideInputMethod()
}

fun Controller.showInputMethod(view: View, flags: Int = 0) {
    activity.showInputMethod(view, flags)
}

inline fun <reified T : Controller> Controller.startActivity() {
    startActivity(activity.intent<T>())
}

inline fun <reified T : Controller> Controller.startActivity(init: Intent.() -> Unit) {
    startActivity(activity.intent<T>(init))
}

@RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
inline fun <reified T : Activity> Controller.startActivityForResult(
    requestCode: Int,
    options: Bundle? = null
) {
    activity.startActivityForResult<T>(requestCode, options)
}

@RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
inline fun <reified T : Activity> Controller.startActivityForResult(
    requestCode: Int,
    options: Bundle? = null,
    init: Intent.() -> Unit
) {
    startActivityForResult(activity.intent<T>(init), requestCode, options)
}

inline fun <reified T : Activity> Controller.activity() = activity as T

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

inline fun <reified T : Application> Controller.app() = activity.app<T>()

fun LifecycleController.viewScopeFor(event: Lifecycle.Event) = viewLifecycleOwner.scopeFor(event)

val LifecycleController.viewOnCreate
    get() = viewLifecycleOwner.onCreate
val LifecycleController.viewOnStart
    get() = viewLifecycleOwner.onStart
val LifecycleController.viewOnResume
    get() = viewLifecycleOwner.onResume
val LifecycleController.viewOnPause
    get() = viewLifecycleOwner.onPause
val LifecycleController.viewOnStop
    get() = viewLifecycleOwner.onStop
val LifecycleController.viewOnDestroy
    get() = viewLifecycleOwner.onDestroy