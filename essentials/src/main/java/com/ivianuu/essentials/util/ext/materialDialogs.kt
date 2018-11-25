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
import android.view.ViewGroup
import com.ivianuu.director.Controller
import com.ivianuu.materialdialogs.MaterialDialog
import com.ivianuu.materialdialogs.callback.onDismiss

val Activity.dialogContainer get() = findViewById<ViewGroup>(android.R.id.content)
val Controller.dialogContainer get() = activity.dialogContainer

fun MaterialDialog.setupWithController(controller: Controller) = apply {
    onDismiss { controller.router.popCurrentController() }
}

fun Controller.MaterialDialog() = com.ivianuu.materialdialogs.MaterialDialog(activity)
    .also { it.setupWithController(this) }