/*
 * Copyright 2019 Manuel Wrage
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

package com.ivianuu.essentials.ui.compose.common

import androidx.ui.core.FocusManagerAmbient
import com.ivianuu.essentials.ui.compose.core.ActivityAmbient
import com.ivianuu.essentials.ui.compose.core.ambient
import com.ivianuu.essentials.ui.compose.core.effect
import com.ivianuu.essentials.util.hideInputMethod

// todo refactor api

fun showKeyboard(id: String): () -> Unit = effect {
    val showKeyboard = showKeyboard()
    return@effect { showKeyboard(id) }
}

fun showKeyboard(): (String) -> Unit = effect {
    val focusManager = ambient(FocusManagerAmbient)
    return@effect { focusManager.requestFocusById(it) }
}

fun hideKeyboard(): () -> Unit = effect {
    val activity = ambient(ActivityAmbient)
    return@effect { activity.hideInputMethod() }
}