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

import android.view.View
import com.ivianuu.director.Controller
import com.ivianuu.director.activity
import com.ivianuu.director.parentController
import com.ivianuu.director.scopes.destroy
import com.ivianuu.director.scopes.destroyView
import com.ivianuu.essentials.util.coroutineScope
import com.ivianuu.kommon.core.app.hideInputMethod
import com.ivianuu.kommon.core.app.showInputMethod

val Controller.coroutineScope get() = destroy.coroutineScope
val Controller.viewCoroutineScope get() = destroyView.coroutineScope

fun Controller.hideInputMethod() {
    activity.hideInputMethod()
}

fun Controller.showInputMethod(view: View, flags: Int = 0) {
    activity.showInputMethod(view, flags)
}

fun Controller.requireParentController(): Controller =
    parentController ?: error("parent Controller is null")