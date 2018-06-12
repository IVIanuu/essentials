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

@file:Suppress("NOTHING_TO_INLINE") // Aliases to other public API.

package com.ivianuu.essentials.util.ext

import android.content.Context
import android.widget.Toast
import com.ivianuu.essentials.util.ContextAware

inline fun Context.toast(message: CharSequence, duration: Int = Toast.LENGTH_SHORT): Toast =
    Toast.makeText(this, message, duration).apply { show() }

inline fun Context.toast(message: Int, duration: Int = Toast.LENGTH_SHORT) =
    toast(getString(message), duration)

inline fun ContextAware.toast(message: CharSequence) = providedContext.toast(message)
inline fun ContextAware.toast(message: Int) = providedContext.toast(message)