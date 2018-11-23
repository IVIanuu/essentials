

// Aliases to other public API.

package com.ivianuu.essentials.util.ext

import android.content.Context
import android.view.View
import com.ivianuu.androidktx.core.content.string
import com.ivianuu.essentials.util.ContextAware
import es.dmoral.toasty.Toasty

fun Context.toastError(message: CharSequence) = mainThread {
    Toasty.error(this, message).apply { show() }
}

fun Context.toastError(messageRes: Int, vararg args: Any) {
    toastError(string(messageRes, *args))
}

fun Context.toastInfo(message: CharSequence) = mainThread {
    Toasty.info(this, message).apply { show() }
}

fun Context.toastInfo(messageRes: Int, vararg args: Any) {
    toastInfo(string(messageRes, *args))
}

fun Context.toastNormal(message: CharSequence) = mainThread {
    Toasty.normal(this, message).apply { show() }
}

fun Context.toastNormal(messageRes: Int, vararg args: Any) {
    toastNormal(string(messageRes, *args))
}

fun Context.toastSuccess(message: CharSequence) = mainThread {
    Toasty.success(this, message).apply { show() }
}

fun Context.toastSuccess(messageRes: Int, vararg args: Any) {
    toastSuccess(string(messageRes, *args))
}

fun Context.toastWarning(message: CharSequence) = mainThread {
    Toasty.warning(this, message).apply { show() }
}

fun Context.toastWarning(messageRes: Int, vararg args: Any) {
    toastWarning(string(messageRes, *args))
}

fun ContextAware.toastError(message: CharSequence) =
    providedContext.toastError(message)

fun ContextAware.toastError(messageRes: Int, vararg args: Any) =
    providedContext.toastError(messageRes, *args)

fun ContextAware.toastInfo(message: CharSequence) =
    providedContext.toastInfo(message)

fun ContextAware.toastInfo(messageRes: Int, vararg args: Any) =
    providedContext.toastInfo(messageRes, *args)

fun ContextAware.toastNormal(message: CharSequence) =
    providedContext.toastNormal(message)

fun ContextAware.toastNormal(messageRes: Int, vararg args: Any) =
    providedContext.toastNormal(messageRes, *args)

fun ContextAware.toastSuccess(message: CharSequence) =
    providedContext.toastSuccess(message)

fun ContextAware.toastSuccess(messageRes: Int, vararg args: Any) =
    providedContext.toastSuccess(messageRes, *args)

fun ContextAware.toastWarning(message: CharSequence) =
    providedContext.toastWarning(message)

fun ContextAware.toastWarning(messageRes: Int, vararg args: Any) =
    providedContext.toastWarning(messageRes, *args)

fun View.toastError(message: CharSequence) =
    context.toastError(message)

fun View.toastError(messageRes: Int, vararg args: Any) =
    context.toastError(messageRes, *args)

fun View.toastInfo(message: CharSequence) =
    context.toastInfo(message)

fun View.toastInfo(messageRes: Int, vararg args: Any) =
    context.toastInfo(messageRes, *args)

fun View.toastNormal(message: CharSequence) =
    context.toastNormal(message)

fun View.toastNormal(messageRes: Int, vararg args: Any) =
    context.toastNormal(messageRes, *args)

fun View.toastSuccess(message: CharSequence) =
    context.toastSuccess(message)

fun View.toastSuccess(messageRes: Int, vararg args: Any) =
    context.toastSuccess(messageRes, *args)

fun View.toastWarning(message: CharSequence) =
    context.toastWarning(message)

fun View.toastWarning(messageRes: Int, vararg args: Any) =
    context.toastWarning(messageRes, *args)