@file:Suppress("NOTHING_TO_INLINE")

package com.ivianuu.essentials.util.ext

import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.ivianuu.essentials.util.ContextAware

inline fun Intent.clearTask(): Intent = addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)

inline fun Intent.clearTop(): Intent = addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

inline fun Intent.clearWhenTaskReset(): Intent =
    addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)

inline fun Intent.excludeFromRecents(): Intent = addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)

inline fun Intent.forwardResult(): Intent = addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT)

@RequiresApi(Build.VERSION_CODES.N)
inline fun Intent.launchAdjacent(): Intent = addFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT)

inline fun Intent.multipleTask(): Intent = addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)

inline fun Intent.newDocument(): Intent = addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT)

inline fun Intent.newTask(): Intent = addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

inline fun Intent.noAnimation(): Intent = addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)

inline fun Intent.noHistory(): Intent = addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)

inline fun Intent.noUserAction(): Intent = addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)

inline fun Intent.previousIsTop(): Intent = addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP)

inline fun Intent.reorderToFront(): Intent = addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)

inline fun Intent.resetTaskIfNeeded(): Intent = addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)

inline fun Intent.retainInRecents(): Intent = addFlags(Intent.FLAG_ACTIVITY_RETAIN_IN_RECENTS)

inline fun Intent.singleTop(): Intent = addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)

inline fun Intent.taskOnHome(): Intent = addFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME)

@PublishedApi
internal val initStub: Intent.() -> Unit = {}

inline fun <reified T> Context.intent() = intent<T>(initStub)

inline fun <reified T> ContextAware.intent() = providedContext.intent<T>()

inline fun <reified T> Fragment.intent() = requireContext().intent<T>()

inline fun <reified T> Context.intent(init: Intent.() -> Unit): Intent {
    val intent = Intent(this, T::class.java)
    init.invoke(intent)
    return intent
}

inline fun <reified T> ContextAware.intent(init: Intent.() -> Unit) =
    providedContext.intent<T>(init)

inline fun <reified T> Fragment.intent(init: Intent.() -> Unit) =
    requireContext().intent<T>(init)

inline fun <reified T> View.intent(init: Intent.() -> Unit) =
    context.intent<T>(init)

inline fun <reified T : Activity> Context.startActivity() {
    startActivity(intent<T>())
}

inline fun <reified T : Activity> ContextAware.startActivity() {
    providedContext.startActivity<T>()
}

inline fun <reified T : Activity> Fragment.startActivity() {
    requireContext().startActivity<T>()
}

inline fun <reified T : Activity> View.startActivity() {
    context.startActivity<T>()
}

inline fun <reified T : Activity> Context.startActivity(init: Intent.() -> Unit) {
    startActivity(intent<T>(init))
}

inline fun <reified T : Activity> ContextAware.startActivity(init: Intent.() -> Unit) {
    providedContext.startActivity<T>(init)
}

inline fun <reified T : Activity> Fragment.startActivity(init: Intent.() -> Unit) {
    requireContext().startActivity<T>(init)
}

inline fun <reified T : Activity> View.startActivity(init: Intent.() -> Unit) {
    context.startActivity<T>(init)
}

inline fun <reified T : Activity> Activity.startActivityForResult(
    requestCode: Int,
    options: Bundle? = null
) {
    startActivityForResult(intent<T>(), requestCode, options)
}

inline fun <reified T : Activity> Activity.startActivityForResult(
    requestCode: Int,
    options: Bundle? = null,
    init: Intent.() -> Unit
) {
    startActivityForResult(intent<T>(init), requestCode, options)
}

inline fun <reified T : Service> Context.startService() {
    startService(intent<T>())
}

inline fun <reified T : Service> ContextAware.startService() {
    providedContext.startService<T>()
}

inline fun <reified T : Service> Fragment.startService() {
    requireContext().startService<T>()
}

inline fun <reified T : Service> View.startService() {
    context.startService<T>()
}

inline fun <reified T : Service> Context.startService(init: Intent.() -> Unit) {
    startService(intent<T>(init))
}

inline fun <reified T : Service> ContextAware.startService(init: Intent.() -> Unit) {
    providedContext.startService<T>(init)
}

inline fun <reified T : Service> Fragment.startService(init: Intent.() -> Unit) {
    requireContext().startService<T>(init)
}

inline fun <reified T : Service> View.startService(init: Intent.() -> Unit) {
    context.startService<T>(init)
}

@RequiresApi(Build.VERSION_CODES.O)
inline fun <reified T : Service> Context.startForegroundService() {
    startForegroundService(intent<T>())
}

@RequiresApi(Build.VERSION_CODES.O)
inline fun <reified T : Service> ContextAware.startForegroundService() {
    providedContext.startForegroundService<T>()
}

@RequiresApi(Build.VERSION_CODES.O)
inline fun <reified T : Service> Fragment.startForegroundService() {
    requireContext().startForegroundService<T>()
}

@RequiresApi(Build.VERSION_CODES.O)
inline fun <reified T : Service> View.startForegroundService() {
    context.startForegroundService<T>()
}

@RequiresApi(Build.VERSION_CODES.O)
inline fun <reified T : Service> Context.startForegroundService(init: Intent.() -> Unit) {
    startForegroundService(intent<T>(init))
}

@RequiresApi(Build.VERSION_CODES.O)
inline fun <reified T : Service> ContextAware.startForegroundService(init: Intent.() -> Unit) {
    providedContext.startForegroundService<T>(init)
}

@RequiresApi(Build.VERSION_CODES.O)
inline fun <reified T : Service> Fragment.startForegroundService(init: Intent.() -> Unit) {
    requireContext().startForegroundService<T>(init)
}

@RequiresApi(Build.VERSION_CODES.O)
inline fun <reified T : Service> View.startForegroundService(init: Intent.() -> Unit) {
    context.startForegroundService<T>(init)
}

inline fun <reified T : Service> Context.startForegroundServiceCompat() {
    startForegroundServiceCompat(intent<T>())
}

inline fun <reified T : Service> ContextAware.startForegroundServiceCompat() {
    providedContext.startForegroundServiceCompat<T>()
}

inline fun <reified T : Service> Fragment.startForegroundServiceCompat() {
    requireContext().startForegroundServiceCompat<T>()
}

inline fun <reified T : Service> View.startForegroundServiceCompat() {
    context.startForegroundServiceCompat<T>()
}

inline fun <reified T : Service> Context.startForegroundServiceCompat(init: Intent.() -> Unit) {
    startForegroundServiceCompat(intent<T>(init))
}

inline fun <reified T : Service> ContextAware.startForegroundServiceCompat(init: Intent.() -> Unit) {
    providedContext.startForegroundServiceCompat<T>(init)
}

inline fun <reified T : Service> Fragment.startForegroundServiceCompat(init: Intent.() -> Unit) {
    requireContext().startForegroundServiceCompat<T>(init)
}

inline fun <reified T : Service> View.startForegroundServiceCompat(init: Intent.() -> Unit) {
    context.startForegroundServiceCompat<T>(init)
}