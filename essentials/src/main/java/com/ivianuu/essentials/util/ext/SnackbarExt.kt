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

import android.support.design.widget.BaseTransientBottomBar
import android.support.design.widget.Snackbar

fun Snackbar.doOnShown(action: (transientBottomBar: Snackbar) -> Unit) =
    addCallback(onShown = action)

fun Snackbar.doOnDismissed(action: (transientBottomBar: Snackbar, event: Int) -> Unit) =
    addCallback(onDismissed = action)

fun Snackbar.addCallback(
    onShown: ((transientBottomBar: Snackbar) -> Unit)? = null,
    onDismissed: ((transientBottomBar: Snackbar, event: Int) -> Unit)? = null
): BaseTransientBottomBar.BaseCallback<Snackbar> {
    val callback = object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
        override fun onShown(transientBottomBar: Snackbar) {
            onShown?.invoke(transientBottomBar)
        }

        override fun onDismissed(transientBottomBar: Snackbar, event: Int) {
            onDismissed?.invoke(transientBottomBar, event)
        }
    }
    addCallback(callback)
    return callback
}