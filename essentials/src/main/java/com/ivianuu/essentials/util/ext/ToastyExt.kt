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
import es.dmoral.toasty.Toasty

inline fun Context.errorToast(message: String): Toast =
    Toasty.error(this, message).apply { show() }

inline fun Context.errorToast(messageRes: Int, vararg args: Any) =
    errorToast(getString(messageRes, *args))

inline fun Context.infoToast(message: String): Toast =
    Toasty.info(this, message).apply { show() }

inline fun Context.infoToast(messageRes: Int, vararg args: Any) =
    infoToast(getString(messageRes, *args))

inline fun Context.normalToast(message: String): Toast =
    Toasty.normal(this, message).apply { show() }

inline fun Context.normalToast(messageRes: Int, vararg args: Any) =
    normalToast(getString(messageRes, *args))

inline fun Context.successToast(message: String): Toast =
    Toasty.success(this, message).apply { show() }

inline fun Context.successToast(messageRes: Int, vararg args: Any) =
    successToast(getString(messageRes, *args))

inline fun Context.warningToast(message: String): Toast =
    Toasty.warning(this, message).apply { show() }

inline fun Context.warningToast(messageRes: Int, vararg args: Any) =
    warningToast(getString(messageRes, *args))

inline fun ContextAware.errorToast(message: String) =
    providedContext.errorToast(message)

inline fun ContextAware.errorToast(messageRes: Int, vararg args: Any) =
    providedContext.errorToast(messageRes, *args)

inline fun ContextAware.infoToast(message: String) =
    providedContext.infoToast(message)

inline fun ContextAware.infoToast(messageRes: Int, vararg args: Any) =
    providedContext.infoToast(messageRes, *args)

inline fun ContextAware.normalToast(message: String) =
    providedContext.normalToast(message)

inline fun ContextAware.normalToast(messageRes: Int, vararg args: Any) =
    providedContext.normalToast(messageRes, *args)

inline fun ContextAware.successToast(message: String) =
    providedContext.successToast(message)

inline fun ContextAware.successToast(messageRes: Int, vararg args: Any) =
    providedContext.successToast(messageRes, *args)

inline fun ContextAware.warningToast(message: String) =
    providedContext.warningToast(message)

inline fun ContextAware.warningToast(messageRes: Int, vararg args: Any) =
    providedContext.warningToast(messageRes, *args)
