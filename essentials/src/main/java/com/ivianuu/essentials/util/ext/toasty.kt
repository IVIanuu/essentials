@file:Suppress("NOTHING_TO_INLINE")

// Aliases to other public API.

package com.ivianuu.essentials.util.ext

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.ivianuu.essentials.util.ContextAware
import es.dmoral.toasty.Toasty

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

inline fun Fragment.toastError(message: CharSequence) =
    requireContext().toastError(message)

inline fun Fragment.toastError(messageRes: Int, vararg args: Any) =
    requireContext().toastError(messageRes, *args)

inline fun Fragment.toastInfo(message: CharSequence) =
    requireContext().toastInfo(message)

inline fun Fragment.toastInfo(messageRes: Int, vararg args: Any) =
    requireContext().toastInfo(messageRes, *args)

inline fun Fragment.toastNormal(message: CharSequence) =
    requireContext().toastNormal(message)

inline fun Fragment.toastNormal(messageRes: Int, vararg args: Any) =
    requireContext().toastNormal(messageRes, *args)

inline fun Fragment.toastSuccess(message: CharSequence) =
    requireContext().toastSuccess(message)

inline fun Fragment.toastSuccess(messageRes: Int, vararg args: Any) =
    requireContext().toastSuccess(messageRes, *args)

inline fun Fragment.toastWarning(message: CharSequence) =
    requireContext().toastWarning(message)

inline fun Fragment.toastWarning(messageRes: Int, vararg args: Any) =
    requireContext().toastWarning(messageRes, *args)

inline fun View.toastError(message: CharSequence) =
    context.toastError(message)

inline fun View.toastError(messageRes: Int, vararg args: Any) =
    context.toastError(messageRes, *args)

inline fun View.toastInfo(message: CharSequence) =
    context.toastInfo(message)

inline fun View.toastInfo(messageRes: Int, vararg args: Any) =
    context.toastInfo(messageRes, *args)

inline fun View.toastNormal(message: CharSequence) =
    context.toastNormal(message)

inline fun View.toastNormal(messageRes: Int, vararg args: Any) =
    context.toastNormal(messageRes, *args)

inline fun View.toastSuccess(message: CharSequence) =
    context.toastSuccess(message)

inline fun View.toastSuccess(messageRes: Int, vararg args: Any) =
    context.toastSuccess(messageRes, *args)

inline fun View.toastWarning(message: CharSequence) =
    context.toastWarning(message)

inline fun View.toastWarning(messageRes: Int, vararg args: Any) =
    context.toastWarning(messageRes, *args)