package com.ivianuu.essentials.util

import android.content.Context
import android.content.ContextWrapper
import com.ivianuu.contributor.view.HasViewInjector

/**
 * Wraps a [Context] and is a [HasViewInjector]
 * to make it possible to inject stuff from a [android.support.v4.app.Fragment]
 * into [View]'s
 */
class ViewInjectionContextWrapper(
    context: Context,
    private val hasViewInjector: HasViewInjector
) : ContextWrapper(context), HasViewInjector by hasViewInjector