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

import androidx.fragment.app.Fragment
import com.ivianuu.essentials.util.coroutineScope
import com.ivianuu.scopes.android.onDestroy
import com.ivianuu.scopes.android.viewOnDestroy

val Fragment.coroutineScope get() = onDestroy.coroutineScope
val Fragment.viewCoroutineScope get() = viewOnDestroy.coroutineScope

fun Fragment.clearTransitions() {
    enterTransition = null
    exitTransition = null
    reenterTransition = null
    returnTransition = null
    sharedElementEnterTransition = null
    sharedElementReturnTransition = null
    allowEnterTransitionOverlap = true
    allowReturnTransitionOverlap = true
}