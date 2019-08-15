package com.ivianuu.essentials.ui.prefs

import android.content.SharedPreferences
import com.ivianuu.compose.ComponentComposition
import com.ivianuu.compose.invalidate
import com.ivianuu.compose.onActive
import com.ivianuu.essentials.ui.compose.injekt.inject

fun ComponentComposition.Prefs(
    children: ComponentComposition.() -> Unit
) {
    val sharedPreferences = inject<SharedPreferences>()
    val invalidate = invalidate
    onActive {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
            invalidate()
        }

        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)

        onDispose {
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }

    children()
}