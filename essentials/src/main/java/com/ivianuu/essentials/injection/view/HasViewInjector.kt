package com.ivianuu.essentials.injection.view

import android.view.View
import dagger.android.AndroidInjector

/**
 * @author Manuel Wrage (IVIanuu)
 */
interface HasViewInjector {
    fun viewInjector(): AndroidInjector<View>
}