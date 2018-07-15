@file:Suppress("NOTHING_TO_INLINE")

// Aliases to other public API.

package com.ivianuu.essentials.util.ext

import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import com.ivianuu.essentials.util.ContextAware

inline fun Intent.clearTask() = addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)

inline fun Intent.clearTop() = addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

inline fun Intent.clearWhenTaskReset() = addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)

inline fun Intent.excludeFromRecents() = addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)

inline fun Intent.forwardResult() = addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT)

@RequiresApi(Build.VERSION_CODES.N)
inline fun Intent.launchAdjacent() = addFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT)

inline fun Intent.multipleTask() = addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)

inline fun Intent.newDocument() = addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT)

inline fun Intent.newTask() = addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

inline fun Intent.noAnimation() = addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)

inline fun Intent.noHistory() = addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)

inline fun Intent.noUserAction() = addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)

inline fun Intent.previousIsTop() = addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP)

inline fun Intent.reorderToFront() = addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)

inline fun Intent.resetTaskIfNeeded() = addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)

inline fun Intent.retainInRecents() = addFlags(Intent.FLAG_ACTIVITY_RETAIN_IN_RECENTS)

inline fun Intent.singleTop() = addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)

inline fun Intent.taskOnHome() = addFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME)

@PublishedApi
internal val initStub: Intent.() -> Unit = {}

inline fun <reified T> Context.intentFor() = intentFor<T>(initStub)

inline fun <reified T> ContextAware.intentFor() = providedContext.intentFor<T>()

inline fun <reified T> Context.intentFor(init: Intent.() -> Unit): Intent {
    val intent = Intent(this, T::class.java)
    init.invoke(intent)
    return intent
}

inline fun <reified T> ContextAware.intentFor(init: Intent.() -> Unit) =
    providedContext.intentFor<T>()

inline fun <reified T : Activity> Context.startActivity() {
    startActivity(intentFor<T>())
}

inline fun <reified T : Activity> ContextAware.startActivity() {
    providedContext.startActivity<T>()
}

inline fun <reified T : Activity> Context.startActivity(init: Intent.() -> Unit) {
    startActivity(intentFor<T>(init))
}

inline fun <reified T : Activity> ContextAware.startActivity(init: Intent.() -> Unit) {
    providedContext.startActivity<T>(init)
}

inline fun <reified T : Activity> Activity.startActivityForResult(
    requestCode: Int,
    options: Bundle? = null
) {
    startActivityForResult(intentFor<T>(), requestCode, options)
}

inline fun <reified T : Activity> Activity.startActivityForResult(
    requestCode: Int,
    options: Bundle? = null,
    init: Intent.() -> Unit
) {
    startActivityForResult(intentFor<T>(init), requestCode, options)
}

inline fun <reified T : Service> Context.startService() {
    startService(intentFor<T>())
}

inline fun <reified T : Service> ContextAware.startService() {
    providedContext.startService<T>()
}

inline fun <reified T : Service> Context.startService(init: Intent.() -> Unit) {
    startService(intentFor<T>(init))
}

inline fun <reified T : Service> ContextAware.startService(init: Intent.() -> Unit) {
    providedContext.startService<T>(init)
}

@RequiresApi(Build.VERSION_CODES.O)
inline fun <reified T : Service> Context.startForegroundService() {
    startForegroundService(intentFor<T>())
}

@RequiresApi(Build.VERSION_CODES.O)
inline fun <reified T : Service> ContextAware.startForegroundService() {
    providedContext.startForegroundService<T>()
}

@RequiresApi(Build.VERSION_CODES.O)
inline fun <reified T : Service> Context.startForegroundService(init: Intent.() -> Unit) {
    startForegroundService(intentFor<T>(init))
}

@RequiresApi(Build.VERSION_CODES.O)
inline fun <reified T : Service> ContextAware.startForegroundService(init: Intent.() -> Unit) {
    providedContext.startForegroundService<T>(init)
}

inline fun <reified T : Service> Context.startForegroundServiceCompat() {
    startForegroundServiceCompat(intentFor<T>())
}

inline fun <reified T : Service> ContextAware.startForegroundServiceCompat() {
    providedContext.startForegroundServiceCompat<T>()
}

inline fun <reified T : Service> Context.startForegroundServiceCompat(init: Intent.() -> Unit) {
    startForegroundServiceCompat(intentFor<T>(init))
}

inline fun <reified T : Service> ContextAware.startForegroundServiceCompat(init: Intent.() -> Unit) {
    providedContext.startForegroundServiceCompat<T>(init)
}