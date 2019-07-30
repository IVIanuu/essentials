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

package com.ivianuu.essentials.ui.dialog

import android.content.Context
import com.afollestad.materialdialogs.MaterialDialog
import com.ivianuu.essentials.util.ContextAware

inline fun Context.dialog(body: MaterialDialog.() -> Unit): MaterialDialog =
    MaterialDialog(this).apply(body)

inline fun ContextAware.dialog(body: MaterialDialog.() -> Unit): MaterialDialog =
    providedContext.dialog(body)

inline fun Context.showDialog(body: MaterialDialog.() -> Unit): MaterialDialog =
    MaterialDialog(this).apply(body).apply { show() }

inline fun ContextAware.showDialog(body: MaterialDialog.() -> Unit): MaterialDialog =
    providedContext.showDialog(body)
