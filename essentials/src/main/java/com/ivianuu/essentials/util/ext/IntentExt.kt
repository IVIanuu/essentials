package com.ivianuu.essentials.util.ext

import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import com.ivianuu.essentials.util.ContextAware

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