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

@file:Suppress("NOTHING_TO_INLINE")

package com.ivianuu.essentials.util.ext

import android.content.Context
import android.widget.Toast
import com.ivianuu.essentials.util.ContextAware
import es.dmoral.toasty.Toasty

// todo make toasts always run on the main thread

inline fun Context.toastError(message: CharSequence): Toast =
    Toasty.error(this, message).apply { show() }

inline fun Context.toastError(messageRes: Int, vararg args: Any) =
    toastError(getString(messageRes, *args))

inline fun Context.toastInfo(message: CharSequence): Toast =
    Toasty.info(this, message).apply { show() }

inline fun Context.toastInfo(messageRes: Int, vararg args: Any) =
    toastInfo(getString(messageRes, *args))

inline fun Context.toastNormal(message: CharSequence): Toast =
    Toasty.normal(this, message).apply { show() }

inline fun Context.toastNormal(messageRes: Int, vararg args: Any) =
    toastNormal(getString(messageRes, *args))

inline fun Context.toastSuccess(message: CharSequence): Toast =
    Toasty.success(this, message).apply { show() }

inline fun Context.toastSuccess(messageRes: Int, vararg args: Any) =
    toastSuccess(getString(messageRes, *args))

inline fun Context.toastWarning(message: CharSequence): Toast =
    Toasty.warning(this, message).apply { show() }

inline fun Context.toastWarning(messageRes: Int, vararg args: Any) =
    toastWarning(getString(messageRes, *args))

inline fun ContextAware.toastError(message: CharSequence) =
    providedContext.toastError(message)

inline fun ContextAware.toastError(messageRes: Int, vararg args: Any) =
    providedContext.toastError(messageRes, *args)

inline fun ContextAware.toastInfo(message: CharSequence) =
    providedContext.toastInfo(message)

inline fun ContextAware.toastInfo(messageRes: Int, vararg args: Any) =
    providedContext.toastInfo(messageRes, *args)

inline fun ContextAware.toastNormal(message: CharSequence) =
    providedContext.toastNormal(message)

inline fun ContextAware.toastNormal(messageRes: Int, vararg args: Any) =
    providedContext.toastNormal(messageRes, *args)

inline fun ContextAware.toastSuccess(message: CharSequence) =
    providedContext.toastSuccess(message)

inline fun ContextAware.toastSuccess(messageRes: Int, vararg args: Any) =
    providedContext.toastSuccess(messageRes, *args)

inline fun ContextAware.toastWarning(message: CharSequence) =
    providedContext.toastWarning(message)

inline fun ContextAware.toastWarning(messageRes: Int, vararg args: Any) =
    providedContext.toastWarning(messageRes, *args)
