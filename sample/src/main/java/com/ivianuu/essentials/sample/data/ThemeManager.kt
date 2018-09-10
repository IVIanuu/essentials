package com.ivianuu.essentials.sample.data

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import com.f2prateek.rx.preferences2.RxSharedPreferences
import com.ivianuu.essentials.app.AppService
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.util.ext.d
import com.ivianuu.essentials.util.ext.registerActivityLifecycleCallbacks
import com.ivianuu.essentials.util.resources.ResourcesPlugins
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author Manuel Wrage (IVIanuu)
 */
@Singleton
class ThemeManager @Inject constructor(
    private val application: Application,
    rxSharedPreferences: RxSharedPreferences
) : AppService {

    private val darkPref = rxSharedPreferences.getBoolean("dark")

    private var currentActivity: Activity? = null

    @SuppressLint("CheckResult")
    override fun start() {
        application.registerActivityLifecycleCallbacks(
            onActivityResumed = { currentActivity = it },
            onActivityPaused = { currentActivity = null }
        )

        darkPref.asObservable()

            .subscribe { setTheme(it) }
    }

    fun toggleTheme() {
        darkPref.set(!darkPref.get())
    }

    private fun setTheme(dark: Boolean) {
        d { "set theme $dark" }
        val themeResId = if (dark) R.style.AppThemeDark else R.style.AppThemeLight
        application.setTheme(themeResId)
        ResourcesPlugins.themeResId = themeResId
        currentActivity?.recreate()
    }
}