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
import com.ivianuu.director.Controller
import com.ivianuu.director.Router
import com.ivianuu.kommon.core.app.hideInputMethod
import com.ivianuu.kommon.core.app.showInputMethod
import com.ivianuu.kommon.core.app.startActivityForResult
import com.ivianuu.kommon.core.content.app
import com.ivianuu.kommon.core.content.intent

val Controller.rootRouter: Router
    get() {
        fun Controller.rootController(): Controller =
                if (parentController != null) parentController!!.rootController() else this
        return rootController().router
    }

fun Controller.hideInputMethod() {
    activity.hideInputMethod()
}

fun Controller.showInputMethod(view: View, flags: Int = 0) {
    activity.showInputMethod(view, flags)
}

inline fun <reified T : Activity> Controller.activity(): T = activity as T

fun Controller.requireParentController(): Controller =
    parentController ?: error("parent Controller is null")

fun Controller.requireTargetController(): Controller =
    targetController ?: error("target Controller is null")

inline fun <reified T : Controller> Controller.parentController(): T = requireParentController() as T

inline fun <reified T : Controller> Controller.parentControllerOrNull(): T? = try {
    parentController()
} catch (e: Exception) {
    null
}

inline fun <reified T : Controller> Controller.targetController(): T = requireTargetController() as T

inline fun <reified T : Controller> Controller.targetControllerOrNull(): T? = try {
    targetController()
} catch (e: Exception) {
    null
}

inline fun <reified T : Application> Controller.app(): T = activity.app()