package com.ivianuu.essentials.injection.view

import android.view.View

/**
 * View injection
 */
object ViewInjection {

    fun inject(view: View) {
        val hasViewInjector = findHasViewInjector(view)
        val viewInjector = hasViewInjector.viewInjector()
        viewInjector.inject(view)
    }

    private fun findHasViewInjector(view: View): HasViewInjector {
        if (view.parent != null) {
            var parent = view.parent

            while (parent != null) {
                if (parent is HasViewInjector) {
                    return parent
                }

                parent = parent.parent
            }
        }

        val context = view.context
        if (context is HasViewInjector) {
            return context
        }

        val applicationContext = context.applicationContext
        if (applicationContext is HasViewInjector) {
            return applicationContext
        }

        throw IllegalArgumentException("no injector found for ${view.javaClass.name}")
    }
}